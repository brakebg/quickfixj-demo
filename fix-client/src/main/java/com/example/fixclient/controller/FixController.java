package com.example.fixclient.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import quickfix.Session;
import quickfix.SessionID;
import quickfix.SessionNotFound;
import quickfix.SocketInitiator;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/fix")
@RequiredArgsConstructor
public class FixController {

    private final SocketInitiator socketInitiator;

    @GetMapping("/status")
    public String getStatus() {
        return socketInitiator.getSessions().stream()
            .map(sessionID -> {
                Session session = Session.lookupSession(sessionID);
                return String.format("Session %s: %s", 
                    sessionID, 
                    session != null && session.isLoggedOn() ? "Connected" : "Disconnected");
            })
            .collect(Collectors.joining("\n"));
    }

    @PostMapping("/send-order")
    public String sendOrder() throws SessionNotFound {
        // Create a new order
        NewOrderSingle order = new NewOrderSingle(
            new ClOrdID(UUID.randomUUID().toString()),
            new HandlInst('1'),
            new Symbol("AAPL"),
            new Side(Side.BUY),
            new TransactTime(),
            new OrdType(OrdType.MARKET)
        );

        // Get the session ID
        SessionID sessionID = socketInitiator.getSessions().get(0);
        
        // Send the order
        Session.sendToTarget(order, sessionID);
        
        return "Order sent successfully";
    }
} 