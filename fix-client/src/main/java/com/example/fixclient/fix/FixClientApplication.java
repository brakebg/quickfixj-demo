package com.example.fixclient.fix;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.field.*;
import quickfix.fix42.*;

@Component
public class FixClientApplication extends quickfix.MessageCracker implements Application {
    private static final Logger logger = LoggerFactory.getLogger(FixClientApplication.class);

    @Override
    public void onCreate(SessionID sessionID) {
        logger.info("Session created: {}", sessionID);
    }

    @Override
    public void onLogon(SessionID sessionID) {
        logger.info("Client logged on: {}", sessionID);
    }

    @Override
    public void onLogout(SessionID sessionID) {
        logger.info("Client logged out: {}", sessionID);
    }

    @Override
    public void toAdmin(quickfix.Message message, SessionID sessionID) {
        logger.info("To Admin: {}", message);
    }

    @Override
    public void fromAdmin(quickfix.Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        logger.info("From Admin: {}", message);
    }

    @Override
    public void toApp(quickfix.Message message, SessionID sessionID) throws DoNotSend {
        logger.info("To App: {}", message);
    }

    @Override
    public void fromApp(quickfix.Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        logger.info("From App: {}", message);
        crack(message, sessionID);
    }

    public void onMessage(ExecutionReport execution, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        logger.info("Received execution report: {}", execution);
    }
} 