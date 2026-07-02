package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.StartupLogger;
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
        if (!plugin.getDiscordManager().isConnected()) return;
        TextChannel channel = plugin.getDiscordManager().getChannel(consoleChannelId);
        if (channel == null) return;

        String formatted = "```ansi\n" + logs + "\n```";

        if (formatted.length() > 2000) {
            int pos = 0;
            while (pos < formatted.length()) {
                int end = Math.min(pos + 1900, formatted.length());
                String chunk = formatted.substring(pos, end);
                channel.sendMessage(chunk).queue(
                    success -> {},
                    error -> plugin.getLogger().warning("Failed to send console log: " + error.getMessage())
                );
                pos = end;
            }
        } else {
            channel.sendMessage(formatted).queue(
                success -> {},
                error -> plugin.getLogger().warning("Failed to send console log: " + error.getMessage())
            );
        }
    }
}
