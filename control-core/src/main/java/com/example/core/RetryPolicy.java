package com.example.core;

import java.time.Duration;

public record RetryPolicy(Duration timeout, int maxRetries, byte[] abortBytes) {
    public static final RetryPolicy NONE = new RetryPolicy(Duration.ofSeconds(5), 0, new byte[0]);
}
