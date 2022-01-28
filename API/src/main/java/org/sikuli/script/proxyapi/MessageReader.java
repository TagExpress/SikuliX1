package org.sikuli.script.proxyapi;

import java.io.InputStream;
import java.nio.ByteBuffer;

public class MessageReader {
    private final InputStream input;

    public MessageReader(InputStream input) {
        this.input = input;
    }

    public int readInt() throws Exception {
        byte[] data = new byte[Integer.BYTES];
        if (input.read(data) != data.length)
            throw new RuntimeException("server closed!");
        return ByteBuffer.wrap(data).getInt();
    }

    public byte[] readBytes(int length) throws Exception {
        if (length == 0)
            return new byte[0];

        byte[] data = new byte[length];
        if (input.read(data) != data.length)
            throw new RuntimeException("server closed!");

        return data;
    }
}
