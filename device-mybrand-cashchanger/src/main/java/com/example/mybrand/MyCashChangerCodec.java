package com.example.mybrand;

import java.util.function.Predicate;

public class MyCashChangerCodec {

    public byte[] encodeDispense(String combo) {
        return (combo + "\r\n").getBytes();
    }

    public Predicate<byte[]> respEndForCounts() {
        return bytes -> bytes.length >= 24;
    }

    public boolean isSmartDispenseEnd(byte[] bytes) {
        return bytes.length > 0 && bytes[bytes.length-1] == '\n';
    }

    public byte[] abort() {
        return new byte[]{0x18}; // CAN
    }
}
