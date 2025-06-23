package com.example.core;

import jpos.JposException;

public abstract class AbstractCashChangerControl {

    protected final AbstractCashChangerService service;

    protected AbstractCashChangerControl(AbstractCashChangerService svc) {
        this.service = svc;
    }

    public synchronized void dispenseCash(String combo) throws JposException {
        service.runWithPolicy(Command.DISPENSE_CASH, combo);
    }

    public synchronized Object readCashCounts() throws JposException {
        return service.runWithPolicy(Command.READ_CASH_COUNTS);
    }
}
