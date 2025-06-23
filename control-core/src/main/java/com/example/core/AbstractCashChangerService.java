package com.example.core;

import jpos.JposConst;
import jpos.JposException;

import java.util.Map;
import java.util.concurrent.*;

public abstract class AbstractCashChangerService {

    protected final DeviceStateMachine sm = new DeviceStateMachine();
    protected final ExecutorService executor = Executors.newSingleThreadExecutor();
    protected Map<Command, RetryPolicy> policyMap = Map.of();

    protected abstract Object doExecute(Command cmd, Object... args) throws JposException;

    public Object runWithPolicy(Command cmd, Object... args) throws JposException {
        RetryPolicy p = policyMap.getOrDefault(cmd, RetryPolicy.NONE);
        for (int i = 0; i <= p.maxRetries(); i++) {
            try {
                Future<Object> fut = executor.submit(() -> doExecute(cmd, args));
                return fut.get(p.timeout().toMillis(), TimeUnit.MILLISECONDS);
            } catch (TimeoutException te) {
                if (i == p.maxRetries()) {
                    // would send abort here
                    throw new JposException(JposConst.JPOS_E_TIMEOUT, "Timeout & aborted");
                }
            } catch (Exception e) {
                if (e instanceof JposException je) throw je;
                throw new JposException(JposConst.JPOS_E_FAILURE, e.getMessage());
            }
        }
        return null;
    }
}
