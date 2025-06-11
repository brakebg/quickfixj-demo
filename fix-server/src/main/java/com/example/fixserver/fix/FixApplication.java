package com.example.quickfixjdemo.fix;

import lombok.extern.slf4j.Slf4j;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

@Slf4j
public class FixApplication extends MessageCracker implements Application {

    @Override
    public void onCreate(SessionID sessionID) {
        log.info("Session created: {}", sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        log.info("Session logged on: {}", sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        log.info("Session logged out: {}", sessionID);
    }

    @Override
    public void toAdmin(Message message, SessionID sessionID) {
        log.info("To Admin: {}", message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        log.info("From Admin: {}", message);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        log.info("To App: {}", message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        log.info("From App: {}", message);
        crack(message, sessionID);
    }

    public void onMessage(NewOrderSingle message, SessionID sessionID) throws FieldNotFound {
        log.info("Received New Order Single: {}", message);
        
        // Echo back the order with a different ClOrdID
        try {
            NewOrderSingle response = new NewOrderSingle(
                new ClOrdID("RESP-" + message.getClOrdID().getValue()),
                new HandlInst('1'),
                new Symbol(message.getSymbol().getValue()),
                new Side(message.getSide().getValue()),
                new TransactTime(),
                new OrdType(message.getOrdType().getValue())
            );
            
            log.info("Sending response: {}", response);
            Session.sendToTarget(response, sessionID);
            log.info("Response sent successfully");
        } catch (SessionNotFound e) {
            log.error("Failed to send response", e);
        }
    }
} 