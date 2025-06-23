package com.example.mybrand;

import com.example.bridge.SerialBridge;
import com.example.core.*;
import jpos.JposConst;
import jpos.JposException;

import java.time.Duration;

public class MyCashChangerService extends AbstractCashChangerService {

    private final MyCashChangerCodec codec = new MyCashChangerCodec();
    private final SerialBridge bridge = new MyCashChangerBridge("COM3");

    public MyCashChangerService() {
        policyMap = java.util.Map.of(
            Command.DISPENSE_CASH, new RetryPolicy(Duration.ofSeconds(2),1,codec.abort()),
            Command.SMART_DISPENSE, new RetryPolicy(Duration.ofSeconds(5),1,codec.abort())
        );
    }

    @Override
    protected Object doExecute(Command cmd, Object... args) throws JposException {
        try {
            switch (cmd) {
                case DISPENSE_CASH -> {
                    String combo = (String) args[0];
                    bridge.write(codec.encodeDispense(combo));
                    bridge.expectAck(Duration.ofSeconds(2));
                    return null;
                }
                case READ_CASH_COUNTS -> {
                    bridge.write("CNT?\r\n".getBytes());
                    bridge.expectAck(Duration.ofSeconds(1));
                    byte[] resp = bridge.readResponse(Duration.ofSeconds(2), codec.respEndForCounts());
                    return new String(resp); // placeholder
                }
                case SMART_DISPENSE -> {
                    int amount = (Integer) args[0];
                    String combo = "1000,1"; // placeholder calc
                    bridge.write(codec.encodeDispense(combo));
                    bridge.expectAck(Duration.ofSeconds(2));
                    byte[] res = bridge.readResponse(Duration.ofSeconds(3), codec::isSmartDispenseEnd);
                    // In real impl, parse and fire event
                    return null;
                }
                default -> throw new JposException(JposConst.JPOS_E_ILLEGAL, "Unsupported cmd");
            }
        } catch (Exception e) {
            if (e instanceof JposException je) throw je;
            throw new JposException(JposConst.JPOS_E_FAILURE, e.getMessage());
        }
    }
}
