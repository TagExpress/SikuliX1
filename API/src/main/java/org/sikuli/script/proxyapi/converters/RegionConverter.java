package org.sikuli.script.proxyapi.converters;

import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import java.nio.ByteBuffer;

public class RegionConverter {
    public byte[] toByte(Region region) {
        ByteBuffer buffer = ByteBuffer.allocate(4 * Integer.BYTES);
        buffer.putInt(region.x);
        buffer.putInt(region.y);
        buffer.putInt(region.w);
        buffer.putInt(region.h);
        return buffer.array();
    }

    public Region fromByte(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        return new Region(buffer.getInt(), buffer.getInt(), buffer.getInt(), buffer.getInt(), Screen.getPrimaryScreen());
    }
}
