package org.sikuli.script.proxyapi.converters;

import org.sikuli.script.Pattern;
import java.nio.ByteBuffer;

public class PatternConverter {
    public byte[] toByte(Pattern pattern) {
        ImageConverter converter = ImageConverter.getInstance();
        byte[] image = converter.toByte(pattern.getImage());

        int capacity = Double.BYTES
                + Integer.BYTES + image.length;

        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.putDouble(pattern.getSimilar());

        buffer.putInt(image.length);
        buffer.put(image);

        return buffer.array();
    }

    public Pattern fromByte(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        double similiar = buffer.getDouble();

        byte[] image = new byte[buffer.getInt()];
        buffer.get(image);

        ImageConverter converter = ImageConverter.getInstance();
        return new Pattern(converter.fromByte(image)).similar(similiar);
    }
}
