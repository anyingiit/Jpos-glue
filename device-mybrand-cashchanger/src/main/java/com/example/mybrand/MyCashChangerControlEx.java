package com.example.mybrand;

import jpos.JposException;

public interface MyCashChangerControlEx {
    void smartDispense(int amountYen) throws JposException;
}
