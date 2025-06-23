package com.example.bridge;

import com.fazecast.jSerialComm.SerialPort;

import java.io.IOException;
import java.time.Duration;
import java.util.function.Predicate;

public class SerialBridge {

    private SerialPort port;

    public SerialBridge(String portName) {
        port = SerialPort.getCommPort(portName);
        port.setBaudRate(9600);
        port.openPort();
    }

    public synchronized void write(byte[] data) throws IOException {
        port.getOutputStream().write(data);
        port.getOutputStream().flush();
    }

    public void expectAck(Duration timeout) throws IOException {
        long limit = System.currentTimeMillis() + timeout.toMillis();
        while (System.currentTimeMillis() < limit) {
            if (port.bytesAvailable() > 0) {
                int b = port.getInputStream().read();
                if (b == 0x06) return; // ACK
            }
        }
        throw new IOException("ACK timeout");
    }

    public byte[] readResponse(Duration timeout, Predicate<byte[]> term) throws IOException {
        long limit = System.currentTimeMillis() + timeout.toMillis();
        java.io.ByteArrayOutputStream buf = new java.io.ByteArrayOutputStream();
        while (System.currentTimeMillis() < limit) {
            while (port.bytesAvailable() > 0) {
                buf.write(port.getInputStream().read());
                if (term.test(buf.toByteArray())) {
                    return buf.toByteArray();
                }
            }
        }
        throw new IOException("Resp timeout");
    }
}
