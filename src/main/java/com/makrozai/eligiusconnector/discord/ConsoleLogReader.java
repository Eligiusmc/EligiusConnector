package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ConsoleLogReader {

    private final EligiusConnector plugin;
    private final String consoleChannelId;
    private ScheduledExecutorService executor;
    private long lastReadPosition = 0;
    private File logFile;

    public ConsoleLogReader(EligiusConnector plugin) {
        this.plugin = plugin;
        this.consoleChannelId = plugin.getConfigAdapter().getConsoleChannelId();
    }

    public void start() {
        logFile = new File("logs/latest.log");
        if (!logFile.exists()) {
            plugin.getLogger().warning("Console log file not found: " + logFile.getAbsolutePath());
            return;
        }

        executor = Executors.newSingleThreadScheduledExecutor();
        int refreshRate = plugin.getConfigAdapter().getConsoleRefreshRate();
        executor.scheduleAtFixedRate(this::readLogs, 0, refreshRate, TimeUnit.SECONDS);
        plugin.getLogger().info("Console log reader started (refresh: " + refreshRate + "s)");
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
                // ISO-8859-1 readLine is fine for ASCII logs; fix UTF-8 if non-ASCII appears.
                if (lastReadPosition > raf.length()) lastReadPosition = 0;
                raf.seek(lastReadPosition);
                String line;
                StringBuilder buffer = new StringBuilder();
                int lineCount = 0;

                while ((line = raf.readLine()) != null) {
                    buffer.append(line).append("\n");
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
        TextChannel channel = plugin.getDiscordManager().getChannel(consoleChannelId);
        if (channel == null) return;

        String formatted = "```\n" + logs + "\n```";

        if (formatted.length() > 2000) {
            String[] parts = formatted.split("(?<=\\G.{1900})");
            for (String part : parts) {
                channel.sendMessage(part).queue(
                    success -> {},
                    error -> plugin.getLogger().warning("Failed to send console log: " + error.getMessage())
                );
            }
        } else {
            channel.sendMessage(formatted).queue(
                success -> {},
                error -> plugin.getLogger().warning("Failed to send console log: " + error.getMessage())
            );
        }
    }
}
