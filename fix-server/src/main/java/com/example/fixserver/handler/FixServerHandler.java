package com.example.fixserver.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import quickfix.*;
import quickfix.Message;
import quickfix.field.*;
import quickfix.fix42.*;
import quickfix.fix42.MessageCracker;

@Component
public class FixServerHandler extends MessageCracker implements Application {
    private static final Logger logger = LoggerFactory.getLogger(FixServerHandler.class);

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
    public void toAdmin(Message message, SessionID sessionID) {
        logger.info("To Admin: {}", message);
    }

    @Override
    public void fromAdmin(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, RejectLogon {
        logger.info("From Admin: {}", message);
    }

    @Override
    public void toApp(Message message, SessionID sessionID) throws DoNotSend {
        logger.info("To App: {}", message);
    }

    @Override
    public void fromApp(Message message, SessionID sessionID) throws FieldNotFound, IncorrectDataFormat, IncorrectTagValue, UnsupportedMessageType {
        logger.info("From App: {}", message);
        crack(message, sessionID);
    }

    public void onMessage(NewOrderSingle order, SessionID sessionID) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue {
        logger.info("Received new order: {}", order);
        
        // Create execution report
        ExecutionReport execution = new ExecutionReport(
            new OrderID("1"),
            new ExecID("1"),
            new ExecTransType(ExecTransType.NEW),
            new ExecType(ExecType.FILL),
            new OrdStatus(OrdStatus.FILLED),
            new Symbol(order.get(new Symbol()).getValue()),
            new Side(order.get(new Side()).getValue()),
            new LeavesQty(0.0),
            new CumQty(order.get(new OrderQty()).getValue()),
            new AvgPx(order.get(new Price()).getValue())
        );
        
        try {
            Session.sendToTarget(execution, sessionID);
            logger.info("Sent execution report: {}", execution);
        } catch (SessionNotFound e) {
            logger.error("Failed to send execution report", e);
        }
    }
} 