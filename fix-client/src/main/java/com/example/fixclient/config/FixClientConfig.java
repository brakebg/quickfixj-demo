package com.example.fixclient.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import quickfix.*;
import com.example.fixclient.fix.FixClientApplication;

@Configuration
public class FixClientConfig {

    @Value("${fix.client.config}")
    private String configFile;

    @Bean
    public FixClientApplication fixClientApplication() {
        return new FixClientApplication();
    }

    @Bean
    public SocketInitiator initiator(FixClientApplication application) throws ConfigError {
        SessionSettings settings = new SessionSettings(getClass().getResourceAsStream("/" + configFile));
        MessageStoreFactory storeFactory = new FileStoreFactory(settings);
        LogFactory logFactory = new FileLogFactory(settings);
        MessageFactory messageFactory = new DefaultMessageFactory();
        
        return new SocketInitiator(
            application,
            storeFactory,
            settings,
            logFactory,
            messageFactory
        );
    }
} 