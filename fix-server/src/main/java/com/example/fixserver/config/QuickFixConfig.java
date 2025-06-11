package com.example.fixserver.config;

import com.example.fixserver.fix.FixApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import quickfix.*;

import java.io.IOException;
import java.io.InputStream;

@Configuration
public class QuickFixConfig {

    @Bean
    public SessionSettings sessionSettings() throws IOException, ConfigError {
        try (InputStream inputStream = getClass().getResourceAsStream("/quickfix.cfg")) {
            return new SessionSettings(inputStream);
        }
    }

    @Bean
    public MessageStoreFactory messageStoreFactory(SessionSettings sessionSettings) {
        return new FileStoreFactory(sessionSettings);
    }

    @Bean
    public LogFactory logFactory(SessionSettings sessionSettings) {
        return new ScreenLogFactory(sessionSettings);
    }

    @Bean
    public MessageFactory messageFactory() {
        return new DefaultMessageFactory();
    }

    @Bean
    public FixApplication fixApplication() {
        return new FixApplication();
    }

    @Bean
    public SocketAcceptor socketAcceptor(SessionSettings sessionSettings,
                                       MessageStoreFactory messageStoreFactory,
                                       LogFactory logFactory,
                                       MessageFactory messageFactory,
                                       FixApplication fixApplication) throws ConfigError {
        return new SocketAcceptor(fixApplication, messageStoreFactory, sessionSettings, logFactory, messageFactory);
    }
} 