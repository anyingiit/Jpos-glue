package com.example.core;

import java.util.concurrent.atomic.AtomicReference;

public class DeviceStateMachine {

    public enum State { CLOSED, CLAIMED, ENABLED, BUSY, IDLE, ERROR }

    private final AtomicReference<State> state = new AtomicReference<>(State.CLOSED);

    public State get() {
        return state.get();
    }

    public void transitionTo(State target) {
        State prev = state.getAndSet(target);
        // sanity: CLOSED -> ENABLED not allowed etc. (simplified)
    }
}
