package org.sikuli.script.proxyapi;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;

public class Message {
    private String name;
    private byte[] data;

    public Message(String name, byte[] data) {
        this.name = name;
        this.data = data;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
