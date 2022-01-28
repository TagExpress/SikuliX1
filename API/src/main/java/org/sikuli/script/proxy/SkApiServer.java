package org.sikuli.script.proxy;

import org.sikuli.script.Image;
import org.sikuli.script.Match;
import org.sikuli.script.Pattern;
import org.sikuli.script.Region;
import org.sikuli.script.proxy.converters.ImageConverter;
import org.sikuli.script.proxy.converters.MatchConverter;
import org.sikuli.script.proxy.converters.PatternConverter;
import org.sikuli.script.proxy.converters.RegionConverter;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class SkApiServer {
    private static Logger logger;
    private static InputStream input;
    private static OutputStream output;

    public static void main(String[] args) {
        input = System.in;
        output = System.out;

        System.setOut(System.err);

        try {
            initializeLogger();
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(1);
        }

        try {
            while (!Thread.currentThread().isInterrupted()) {
                receiveMessage();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, ex.getMessage(), ex);
            System.exit(1);
        }
    }

    static private void initializeLogger() throws Exception {
        Properties logConfigProps = new Properties();
        logConfigProps.put("handlers","java.util.logging.ConsoleHandler");
        logConfigProps.put("java.util.logging.ConsoleHandler.level","ALL");
        logConfigProps.put("java.util.logging.ConsoleHandler.formatter","java.util.logging.SimpleFormatter");
        logConfigProps.put("java.util.logging.SimpleFormatter.format","%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS [%4$.4s] %2$s: %5$s%n%6$s");

        ByteArrayOutputStream logData = new ByteArrayOutputStream();
        logConfigProps.store(logData, null);

        ByteArrayInputStream logConfig = new ByteArrayInputStream(logData.toByteArray());
        LogManager logManager = LogManager.getLogManager();
        logManager.readConfiguration(logConfig);

        logger = Logger.getLogger(SkApiServer.class.getName());
    }

    static private void receiveMessage() {
        try {
            MessageReader reader = new MessageReader(input);
            Message request = new Message(null, null);
            request.setName(new String(reader.readBytes(reader.readInt())));
            request.setData(reader.readBytes(reader.readInt()));

            logger.log(Level.INFO, "request message: {0}, data.length: {1}",
                    new Object[] {request.getName(), request.getData().length});

            Message response = processMessage(request);

            logger.log(Level.INFO, "response message: {0}, data.length: {1}",
                    new Object[] {response.getName(), response.getData().length});

            MessageWriter writer = new MessageWriter(output);
            byte[] name = response.getName().getBytes();

            writer.writeInt(name.length);
            writer.writeBytes(name);

            writer.writeInt(response.getData().length);
            writer.writeBytes(response.getData());

            output.flush();
        } catch (Exception ex) {
            throw new RuntimeException(ex.getMessage(), ex);
        }
    }

    static private Message processMessage(Message request) {
        try {
            if ("find".equals(request.getName())) {
                return find(request);
            } else if ("findAll".equals(request.getName())) {
                return findAll(request);
            } else if ("findBest".equals(request.getName())) {
                return findBest(request);
            }
            throw new Exception("method "+request.getName()+" not found!");
        } catch (Exception ex) {
            String msg = ex.getMessage();
            if (msg == null) {
                msg = "erro ao processar:" + request.getName() + System.lineSeparator();
            }

            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            printWriter.write(msg);
            ex.printStackTrace(printWriter);

            return new Message("exception", stringWriter.toString().getBytes());
        } finally {
            ImageConverter imageConverter = ImageConverter.getInstance();
            imageConverter.clearCache();
        }
    }

    static private Message find(Message request) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(request.getData());
        byte[] regionData = new byte[buffer.getInt()];
        buffer.get(regionData);

        RegionConverter regionConverter = new RegionConverter();
        Region region = regionConverter.fromByte(regionData);

        boolean targetTypeImage = buffer.get() == (byte)1;

        byte[] targetData = new byte[buffer.getInt()];
        buffer.get(targetData);

        Object target;
        if (targetTypeImage) {
            ImageConverter imageConverter = ImageConverter.getInstance();
            target = imageConverter.fromByte(targetData);
        } else {
            PatternConverter patternConverter = new PatternConverter();
            target = patternConverter.fromByte(targetData);
        }

        Match match;
        if (targetTypeImage) {
            match = region.find( (Image) target);
        } else {
            match = region.find( (Pattern) target);
        }

        Message response = new Message(request.getName(), null);

        if (match == null) {
            response.setData(new byte[0]);
        } else {
            MatchConverter matchConverter = new MatchConverter();
            response.setData(matchConverter.toByte(match));
        }

        return response;
    }

    static private Message findAll(Message request) throws Exception {
        ByteBuffer buffer = ByteBuffer.wrap(request.getData());
        byte[] regionData = new byte[buffer.getInt()];
        buffer.get(regionData);

        RegionConverter regionConverter = new RegionConverter();
        Region region = regionConverter.fromByte(regionData);

        boolean targetTypeImage = buffer.get() == (byte)1;

        byte[] targetData = new byte[buffer.getInt()];
        buffer.get(targetData);

        Object target;
        if (targetTypeImage) {
            ImageConverter imageConverter = ImageConverter.getInstance();
            target = imageConverter.fromByte(targetData);
        } else {
            PatternConverter patternConverter = new PatternConverter();
            target = patternConverter.fromByte(targetData);
        }

        Iterator<Match> matches;
        if (targetTypeImage) {
            matches = region.findAll( (Image) target);
        } else {
            matches = region.findAll( (Pattern) target);
        }

        List<Match> matchList = new ArrayList<>();
        while (matches.hasNext()) {
            matchList.add(matches.next());
        }

        Message response = new Message(request.getName(), null);

        if (matchList.isEmpty()) {
            response.setData(new byte[0]);
        } else {
            MatchConverter matchConverter = new MatchConverter();
            List<byte[]> matchDataList = new ArrayList<>();
            for (Match match : matchList) {
                matchDataList.add(matchConverter.toByte(match));
            }

            int capacity = 0;
            for (byte[] matchData : matchDataList) {
                capacity += Integer.BYTES + matchData.length;
            }
            capacity += Integer.BYTES;// fim da lista

            buffer = ByteBuffer.allocate(capacity);

            for (byte[] matchData : matchDataList) {
                buffer.putInt(matchData.length);
                buffer.put(matchData);
            }
            buffer.putInt(0);

            response.setData(buffer.array());
        }

        return response;
    }

    static private Message findBest(Message request) {
        ByteBuffer buffer = ByteBuffer.wrap(request.getData());
        byte[] regionData = new byte[buffer.getInt()];
        buffer.get(regionData);

        RegionConverter regionConverter = new RegionConverter();
        Region region = regionConverter.fromByte(regionData);

        ImageConverter imageConverter = ImageConverter.getInstance();
        PatternConverter patternConverter = new PatternConverter();

        List<Object> targets = new ArrayList<>();

        int length;
        byte[] targetData;
        while ((length = buffer.getInt()) != 0) {
            targetData = new byte[length];
            buffer.get(targetData);

            if (targetData[0] == (byte)1) {
                targets.add(imageConverter.fromByte(Arrays.copyOfRange(targetData, 1, targetData.length)));
            } else {
                targets.add(patternConverter.fromByte(Arrays.copyOfRange(targetData, 1, targetData.length)));
            }
        }

        Match match = region.findBest(targets.toArray());

        Message response = new Message(request.getName(), null);

        if (match == null) {
            response.setData(new byte[0]);
        } else {
            MatchConverter matchConverter = new MatchConverter();
            response.setData(matchConverter.toByte(match));
        }

        return response;
    }
}
