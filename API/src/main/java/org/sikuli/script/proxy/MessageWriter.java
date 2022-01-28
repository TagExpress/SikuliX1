package org.sikuli.script.proxy;

import java.io.OutputStream;
import java.nio.ByteBuffer;

public class MessageWriter {
    private final OutputStream output;

    public MessageWriter(OutputStream output) {
        this.output = output;
    }

    public void writeInt(int value) throws Exception {
        output.write(ByteBuffer.allocate(Integer.BYTES).putInt(value).array());
    }

    public void writeBytes(byte[] data) throws Exception {
        if (data.length > 0)
            output.write(data);
    }
}
