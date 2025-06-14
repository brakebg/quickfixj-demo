package com.example.fixserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import quickfix.*;
import com.example.fixserver.handler.FixServerHandler;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Configuration
public class FixServerConfig {

    @Bean
    public FixServerHandler fixServerHandler() {
        return new FixServerHandler();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    public SocketAcceptor acceptor(FixServerHandler application) throws ConfigError {
        String config = """
[DEFAULT]
ConnectionType=acceptor
SocketAcceptPort=5001
StartTime=00:00:00
EndTime=23:59:59
FileLogPath=log
FileStorePath=store
HeartBtInt=30
ValidateUserDefinedFields=N

[SESSION]
BeginString=FIX.4.2
SenderCompID=SERVER
TargetCompID=CLIENT
""";

        try (InputStream inputStream = new ByteArrayInputStream(config.getBytes(StandardCharsets.US_ASCII))) {
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
        } catch (Exception e) {
            throw new ConfigError("Failed to create FIX settings: " + e.getMessage(), e);
        }
    }
}
