package com.example.fixserver.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.Assert;
import quickfix.*;
import com.example.fixserver.handler.FixServerHandler;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class FixServerConfig {

    private final String configFile;

    public FixServerConfig(@Value("${fix.server.config}") String configFile) {
        this.configFile = configFile;
    }

    @Bean
    public FixServerHandler fixServerHandler() {
        return new FixServerHandler();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SocketAcceptor acceptor(FixServerHandler application) throws ConfigError {
        try {
            ClassPathResource resource = new ClassPathResource(configFile);
            Assert.isTrue(resource.exists(), "FIX configuration file not found: " + configFile + " in classpath");

            try (InputStream inputStream = resource.getInputStream()) {
                SessionSettings settings = new SessionSettings(inputStream);
                MessageStoreFactory storeFactory = new FileStoreFactory(settings);
                LogFactory logFactory = new FileLogFactory(settings);
                MessageFactory messageFactory = new DefaultMessageFactory();
                
                return new SocketAcceptor(
                    application,
                    storeFactory,
                    settings,
                    logFactory,
                    messageFactory
                );
            }
        } catch (IOException e) {
            throw new ConfigError("Failed to load FIX configuration from " + configFile + ": " + e.getMessage(), e);
        }
    }
} 
