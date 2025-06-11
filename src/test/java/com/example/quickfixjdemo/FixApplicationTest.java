package com.example.quickfixjdemo;

import com.example.fixserver.fix.FixApplication;
import com.example.fixserver.FixServerApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertTrue;

class FixApplicationTest {

    private SocketInitiator initiator;
    private FixApplication clientApplication;
    private CountDownLatch messageReceivedLatch;
    private ConfigurableApplicationContext applicationContext;

    @BeforeEach
    void setUp() throws ConfigError, IOException, InterruptedException {
        // Start Spring Boot application
        applicationContext = SpringApplication.run(FixServerApplication.class);
        
        // Wait for the application to start and the FIX acceptor to be ready
        Thread.sleep(2000);

        // Load client configuration
        try (InputStream inputStream = getClass().getResourceAsStream("/quickfix-client.cfg")) {
            SessionSettings settings = new SessionSettings(inputStream);
            
            // Create client application
            clientApplication = new FixApplication();
            messageReceivedLatch = new CountDownLatch(1);
            
            // Create initiator
            MessageStoreFactory storeFactory = new FileStoreFactory(settings);
            LogFactory logFactory = new ScreenLogFactory(settings);
            MessageFactory messageFactory = new DefaultMessageFactory();
            
            initiator = new SocketInitiator(
                clientApplication,
                storeFactory,
                settings,
                logFactory,
                messageFactory
            );
            
            // Start the initiator
            initiator.start();
        }
    }

    @AfterEach
    void tearDown() {
        if (initiator != null) {
            initiator.stop();
        }
        if (applicationContext != null) {
            applicationContext.close();
        }
    }

    @Test
    void testNewOrderSingle() throws Exception {
        // Wait for connection
        Thread.sleep(1000);

        // Find the session ID for the client session
        SessionID sessionID = null;
        for (SessionID id : initiator.getSessions()) {
            sessionID = id;
            break;
        }
        if (sessionID == null) {
            throw new IllegalStateException("No session found for initiator");
        }

        // Create a new order
        NewOrderSingle order = new NewOrderSingle(
            new ClOrdID("ORDER-1"),
            new HandlInst('1'),
            new Symbol("AAPL"),
            new Side(Side.BUY),
            new TransactTime(),
            new OrdType(OrdType.MARKET)
        );

        // Send the order to the correct session
        Session.sendToTarget(order, sessionID);

        // Wait for response (timeout after 5 seconds)
        assertTrue(messageReceivedLatch.await(5, TimeUnit.SECONDS), "No response received within timeout");
    }
} 