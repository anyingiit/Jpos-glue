package com.example.mybrand;

import com.example.core.*;
import jpos.JposException;

public class MyCashChangerControl extends AbstractCashChangerControl implements MyCashChangerControlEx {

    public MyCashChangerControl() {
        super(new MyCashChangerService());
    }

    @Override
    public void smartDispense(int amountYen) throws JposException {
        service.runWithPolicy(Command.SMART_DISPENSE, amountYen);
    }
}
