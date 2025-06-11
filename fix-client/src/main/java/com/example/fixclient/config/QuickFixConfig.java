package com.example.fixclient.config;

import com.example.fixclient.fix.FixClientHandler;
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
    public FixClientHandler fixClientHandler() {
        return new FixClientHandler();
    }

    @Bean
    public SocketInitiator socketInitiator(SessionSettings sessionSettings,
                                         MessageStoreFactory messageStoreFactory,
                                         LogFactory logFactory,
                                         MessageFactory messageFactory,
                                         FixClientHandler fixClientHandler) throws ConfigError {
        return new SocketInitiator(
            fixClientHandler,
            messageStoreFactory,
            sessionSettings,
            logFactory,
            messageFactory
        );
    }
} 