package org.sikuli.script.proxyapi.converters;

import org.sikuli.script.Region;
import org.sikuli.script.Screen;

import java.nio.ByteBuffer;

public class RegionConverter {
    public byte[] toByte(Region region) {

        int capacity = 4 * Integer.BYTES + 2;

        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.putInt(region.x);
        buffer.putInt(region.y);
        buffer.putInt(region.w);
        buffer.putInt(region.h);

        byte hasScreen = region.getScreen() != null ? (byte)1 : (byte)0;
        byte hasOtherScreen = region.isOtherScreen() ? (byte)1 : (byte)0;

        buffer.put(hasScreen);
        buffer.put(hasOtherScreen);

        return buffer.array();
    }

    public Region fromByte(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int x = buffer.getInt();
        int y = buffer.getInt();
        int w = buffer.getInt();
        int h = buffer.getInt();
        boolean hasScreen = buffer.get() == (byte)1;
        boolean hasOtherScreen = buffer.get() == (byte)1;

        Screen screen = hasScreen ? Screen.getPrimaryScreen() : null;

        Region region = new Region(x, y, w, h, screen);

        if (hasOtherScreen)
            region.setOtherScreen();

        return region;
    }
}
