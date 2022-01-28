package org.sikuli.script.proxy.converters;

import org.sikuli.script.Image;

import javax.imageio.ImageIO;
import java.awt.image.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.TreeMap;

public class ImageConverter {
    private static ImageConverter instance;
    private final Map<Integer,Image> imageByHash;

    private ImageConverter() {
        imageByHash = new TreeMap<>();
    }

    synchronized static public ImageConverter getInstance() {
        if (instance == null) {
            instance = new ImageConverter();
        }
        return instance;
    }

    public void clearCache() {
        synchronized (imageByHash) {
            imageByHash.clear();
        }
    }

    public byte[] toByte(Image image) {
        byte[] name = image.getName().getBytes();
        byte[] fileName = image.getFilename().getBytes();
        byte[] data;

        try {
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ImageIO.write(image.get(), "png", output);
            data = output.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }

        int capacity = 5 * Integer.BYTES
                + Integer.BYTES + name.length
                + Integer.BYTES + fileName.length
                + Integer.BYTES + data.length;

        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.putInt(image.hashCode());
        buffer.putInt(image.x);
        buffer.putInt(image.y);
        buffer.putInt(image.w);
        buffer.putInt(image.h);

        buffer.putInt(name.length);
        buffer.put(name);

        buffer.putInt(fileName.length);
        buffer.put(fileName);

        buffer.putInt(data.length);
        buffer.put(data);

        synchronized (imageByHash) {
            imageByHash.put(image.hashCode(), image);
        }

        return buffer.array();
    }

    public Image fromByte(byte[] img) {

        ByteBuffer buffer = ByteBuffer.wrap(img);

        int id = buffer.getInt();

        Image image;
        synchronized (imageByHash) {
            image = imageByHash.get(id);
        }

        if (image != null) {
            return image;
        }

        int x = buffer.getInt();
        int y = buffer.getInt();
        int w = buffer.getInt();
        int h = buffer.getInt();

        byte[] name = new byte[buffer.getInt()];
        buffer.get(name);

        byte[] fileName = new byte[buffer.getInt()];
        buffer.get(fileName);

        byte[] data = new byte[buffer.getInt()];
        buffer.get(data);

        BufferedImage bimg;
        try {
            ByteArrayInputStream input = new ByteArrayInputStream(data);
            bimg = ImageIO.read(input);
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }

        image = Image.create(new String(fileName), bimg);
        image.setName(new String(name));
        image.x = x;
        image.y = y;
        image.w = w;
        image.h = h;

        return image;
    }
}
