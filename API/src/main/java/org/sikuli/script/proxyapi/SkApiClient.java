package org.sikuli.script.proxyapi;

import org.sikuli.basics.Settings;
import org.sikuli.script.*;
import org.sikuli.script.proxyapi.converters.ImageConverter;
import org.sikuli.script.proxyapi.converters.MatchConverter;
import org.sikuli.script.proxyapi.converters.PatternConverter;
import org.sikuli.script.proxyapi.converters.RegionConverter;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SkApiClient {
    private static final Logger logger = Logger.getLogger(SkApiClient.class.getName());
    private static Process process;
    private static InputStream input;
    private static OutputStream output;

    static public boolean isClient() {
        return Boolean.parseBoolean(System.getProperty("sikulixapiclient", "false"));
    }

    static public void start() throws Exception {
        try {
            stop();

            List<String> args = new ArrayList<>();
            args.add("java");
            args.add("-cp");
            args.add("target" + File.separator + "lib" + File.separator + "*");
            args.add("-Xms1024m");
            args.add("-Xmx1024m");
            args.add("-Dfile.encoding=" + System.getProperty("file.encoding", StandardCharsets.UTF_8.name()));

            args.add(SkApiServer.class.getName());

            ProcessBuilder builder = new ProcessBuilder(args);
            process = builder.start();
            input = process.getInputStream();
            output = process.getOutputStream();

            InputStream error = process.getErrorStream();

            Thread task = new Thread(() -> {
                try {
                    Scanner sc = new Scanner(error);
                    while (!Thread.currentThread().isInterrupted() && process != null && process.isAlive()) {
                        logger.log(Level.INFO, sc.nextLine());
                    }
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, ex.getMessage(), ex);
                } finally {
                    try {
                        if (error != null) {
                            error.close();
                        }
                    } catch (Exception exc) {
                        logger.log(Level.SEVERE, exc.getMessage(), exc);
                    }
                }
            });
            task.setName("process-output-"+task.getId());
            task.setDaemon(true);
            task.start();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    static public void stop() {
        try {
            if (output != null) {
                output.close();
                output = null;
            }

            if (input != null) {
                input.close();
                input = null;
            }

            if (process != null) {
                if (process.isAlive()) {
                    process.destroy();
                    if (!process.waitFor(3000L, TimeUnit.MILLISECONDS)) {
                        process.destroyForcibly();
                        process.waitFor();
                    }
                }
                process = null;
            }
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    static private Message sendMessage(Message request) {
        if (process == null) {
            throw new RuntimeException("server stopped!");
        }

        synchronized (process) {
            try {
                MessageWriter writer = new MessageWriter(output);

                byte[] name = request.getName().getBytes();

                logger.log(Level.INFO, "sending message...");
                logger.log(Level.INFO, "message.name.length:" + name.length);
                logger.log(Level.INFO, "message.data.length:" + request.getData().length);

                writer.writeInt(name.length);
                writer.writeBytes(name);

                writer.writeInt(request.getData().length);
                writer.writeBytes(request.getData());

                output.flush();

                MessageReader reader = new MessageReader(input);
                Message response = new Message(null, null);
                response.setName(new String(reader.readBytes(reader.readInt())));
                response.setData(reader.readBytes(reader.readInt()));

                if ("exception".equals(response.getName())) {
                    throw new RuntimeException(new String(response.getData()));
                }

                return response;
            } catch (Exception ex) {
                throw new RuntimeException(ex.getMessage(), ex);
            }
        }
    }

    static public <PSI> Match find(Region region, PSI target) throws FindFailed {

        RegionConverter regionConverter = new RegionConverter();
        ImageConverter imageConverter = ImageConverter.getInstance();

        byte[] regionData = regionConverter.toByte(region);
        byte[] targetData;

        boolean targetTypeImage;

        if (target instanceof Image) {
            targetTypeImage = true;
            targetData = imageConverter.toByte((Image) target);
        } else if (target instanceof Pattern) {
            targetTypeImage = false;
            PatternConverter patternConverter = new PatternConverter();
            targetData = patternConverter.toByte((Pattern) target);
        } else {
            throw new RuntimeException("target deve ser Image ou Pattern!");
        }

        int capacity = Integer.BYTES + regionData.length + 1 + Integer.BYTES + targetData.length;

        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.putInt(regionData.length);
        buffer.put(regionData);
        buffer.put(targetTypeImage ? (byte)1 : (byte)0);
        buffer.putInt(targetData.length);
        buffer.put(targetData);

        Message response = sendMessage(new Message("find", buffer.array()));

        if (response.getData().length == 0) {
            return null;
        }

        MatchConverter matchConverter = new MatchConverter();
        return matchConverter.fromByte(response.getData());
    }

    static public <PSI> Iterator<Match> findAll(Region region, PSI target) throws FindFailed {

        RegionConverter regionConverter = new RegionConverter();
        ImageConverter imageConverter = ImageConverter.getInstance();

        byte[] regionData = regionConverter.toByte(region);
        byte[] targetData;

        boolean targetTypeImage;

        if (target instanceof Image) {
            targetTypeImage = true;
            targetData = imageConverter.toByte((Image) target);
        } else if (target instanceof Pattern) {
            targetTypeImage = false;
            PatternConverter patternConverter = new PatternConverter();
            targetData = patternConverter.toByte((Pattern) target);
        } else {
            throw new RuntimeException("target deve ser Image ou Pattern!");
        }

        int capacity = Integer.BYTES + regionData.length + 1 + Integer.BYTES + targetData.length;

        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.putInt(regionData.length);
        buffer.put(regionData);
        buffer.put(targetTypeImage ? (byte)1 : (byte)0);
        buffer.putInt(targetData.length);
        buffer.put(targetData);

        Message response = sendMessage(new Message("findAll", buffer.array()));

        if (response.getData().length == 0) {
            imageConverter.clearCache();
            return Collections.emptyIterator();
        }

        buffer = ByteBuffer.wrap(response.getData());

        List<Match> matches = new ArrayList<>();

        MatchConverter matchConverter = new MatchConverter();

        int length;
        byte[] matchData;
        while ((length = buffer.getInt()) != 0) {
            matchData = new byte[length];
            buffer.get(matchData);
            matches.add(matchConverter.fromByte(matchData));
        }

        return matches.iterator();
    }

    static public Match findBest(Region region, Object... args) {
        if (args == null || args.length == 0) {
            return null;
        }

        RegionConverter regionConverter = new RegionConverter();

        byte[] regionData = regionConverter.toByte(region);

        ImageConverter imageConverter = ImageConverter.getInstance();
        PatternConverter patternConverter = new PatternConverter();

        List<byte[]> targets = new ArrayList<>();
        for (Object target : args) {
            byte[] data;
            if (target instanceof Image) {
                data = imageConverter.toByte((Image) target);

                byte[] dataSized = new byte[data.length + 1];
                System.arraycopy(data, 0, dataSized, 1, data.length);
                dataSized[0] = (byte)1;
                data = dataSized;
            } else if (target instanceof Pattern) {
                data = patternConverter.toByte((Pattern) target);

                byte[] dataSized = new byte[data.length + 1];
                System.arraycopy(data, 0, dataSized, 1, data.length);
                dataSized[0] = (byte)0;
                data = dataSized;
            } else {
                throw new RuntimeException("target deve ser Image ou Pattern!");
            }

            targets.add(data);
        }

        int capacity = Integer.BYTES + regionData.length;

        for (byte[] targetData : targets) {
            capacity += Integer.BYTES + targetData.length;
        }
        capacity += Integer.BYTES;// fim da lista

        ByteBuffer buffer = ByteBuffer.allocate(capacity);
        buffer.putInt(regionData.length);
        buffer.put(regionData);

        for (byte[] targetData : targets) {
            buffer.putInt(targetData.length);
            buffer.put(targetData);
        }
        buffer.putInt(0);

        Message response = sendMessage(new Message("findBest", buffer.array()));

        if (response.getData().length == 0) {
            return null;
        }

        MatchConverter matchConverter = new MatchConverter();
        return matchConverter.fromByte(response.getData());
    }
}
