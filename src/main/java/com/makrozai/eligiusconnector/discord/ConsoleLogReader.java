package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.StartupLogger;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsoleLogReader {

    private final EligiusConnector plugin;
    private ScheduledExecutorService executor;
    private long lastReadPosition = 0;
    private File logFile;

    public ConsoleLogReader(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void restart() {
        stop();
        start();
    }

    public void start() {
        logFile = new File("logs/latest.log");
        if (!logFile.exists()) {
            plugin.getLogger().warning("Console log file not found: " + logFile.getAbsolutePath());
            return;
        }

        // Skip existing content, only stream new lines
        lastReadPosition = logFile.length();

        executor = Executors.newSingleThreadScheduledExecutor();
        int refreshRate = plugin.getConfigAdapter().getConsolePollInterval();
        executor.scheduleAtFixedRate(this::readLogs, refreshRate, refreshRate, TimeUnit.SECONDS);
        StartupLogger.printStep("Console log reader started (" + refreshRate + "s interval)");
    }

    public void stop() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdownNow();
        }
    }

    private void readLogs() {
        try {
            if (!logFile.exists()) return;

            try (RandomAccessFile raf = new RandomAccessFile(logFile, "r")) {
                // ponytail: detect log rotation (file truncated/rotated) → reset position.
                if (lastReadPosition > raf.length()) lastReadPosition = 0;
                raf.seek(lastReadPosition);
                String line;
                StringBuilder buffer = new StringBuilder();
                int lineCount = 0;

                while ((line = raf.readLine()) != null) {
                    buffer.append(new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8)).append("\n");
                    lineCount++;

                    if (buffer.length() > 1800 || lineCount >= 50) {
                        sendToDiscord(buffer.toString());
                        buffer.setLength(0);
                        lineCount = 0;
                    }
                }

                lastReadPosition = raf.getFilePointer();

                if (buffer.length() > 0) {
                    sendToDiscord(buffer.toString());
                }
            }
        } catch (IOException e) {
            plugin.getLogger().warning("Error reading console log: " + e.getMessage());
        }
    }

    private void sendToDiscord(String logs) {
        if (!plugin.getDiscordManager().isConnected()) return;
        TextChannel channel = plugin.getDiscordManager().getChannel(plugin.getConfigAdapter().getConsoleChannelId());
        if (channel == null) return;

        splitLogMessages(logs).forEach(message -> channel.sendMessage(message).queue(
                success -> {},
                error -> plugin.getLogger().warning("Failed to send console log: " + error.getMessage())));
    }

    static List<String> splitLogMessages(String logs) {
        String sanitized = logs.replace("```", "` ` `");
        List<String> chunks = new ArrayList<>();
        StringBuilder content = new StringBuilder();
        for (String sourceLine : sanitized.split("\n", -1)) {
            String line = sourceLine;
            while (line.length() > 1800) {
                int end = 1800;
                if (Character.isHighSurrogate(line.charAt(end - 1))) end--;
                if (content.length() > 0) {
                    chunks.add("```ansi\n" + content + "```");
                    content.setLength(0);
                }
                chunks.add("```ansi\n" + line.substring(0, end) + "\n```");
                line = line.substring(end);
            }
            if (content.length() + line.length() + 1 > 1800) {
                chunks.add("```ansi\n" + content + "```");
                content.setLength(0);
            }
            if (content.length() > 0) content.append('\n');
            content.append(line);
        }
        if (content.length() > 0) chunks.add("```ansi\n" + content + "\n```");
        return chunks;
    }
}
