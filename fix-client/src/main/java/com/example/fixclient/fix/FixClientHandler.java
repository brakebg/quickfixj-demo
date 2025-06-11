package com.example.fixclient.fix;

import lombok.extern.slf4j.Slf4j;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.NewOrderSingle;

@Slf4j
public class FixClientHandler extends MessageCracker implements Application {

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
        log.info("Received New Order Single response: {}", message);
    }
} 