package org.sikuli.script.proxy.converters;

import org.sikuli.script.Match;
import org.sikuli.script.Screen;

import java.nio.ByteBuffer;

public class MatchConverter {
    public byte[] toByte(Match match) {

        ImageConverter converter = ImageConverter.getInstance();
        byte[] image = converter.toByte(match.getImage());

        int capacity = 5 * Integer.BYTES
                + Double.BYTES
                + 2 * Long.BYTES
                + 1
                + Integer.BYTES + image.length;

        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.putInt(match.x);
        buffer.putInt(match.y);
        buffer.putInt(match.w);
        buffer.putInt(match.h);
        buffer.putInt(match.getIndex());
        buffer.putDouble(match.getScore());
        buffer.putLong(match.getTime());
        buffer.putLong(match.getLastSearchTime());

        byte hasScreen = match.getScreen() != null ? (byte)1 : (byte)0;

        buffer.put(hasScreen);

        buffer.putInt(image.length);
        buffer.put(image);

        return buffer.array();
    }

    public Match fromByte(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int x = buffer.getInt();
        int y = buffer.getInt();
        int w = buffer.getInt();
        int h = buffer.getInt();
        int index = buffer.getInt();
        double score = buffer.getDouble();
        long findTime = buffer.getLong();
        long searchTime = buffer.getLong();

        boolean hasScreen = buffer.get() == (byte)1;
        Screen screen = hasScreen ? Screen.getPrimaryScreen() : null;

        byte[] image = new byte[buffer.getInt()];
        buffer.get(image);

        Match match = new Match(x, y, w, h, score, screen);
        match.setIndex(index);
        match.setTimes(findTime, searchTime);
        match.x = x;
        match.y = y;
        match.w = w;
        match.h = h;

//        if (targetFlag == (byte)1) {
//            match.setTarget(targetX, targetY);
//        }

        ImageConverter converter = ImageConverter.getInstance();
        match.setImage(converter.fromByte(image));

        return match;
    }
}
