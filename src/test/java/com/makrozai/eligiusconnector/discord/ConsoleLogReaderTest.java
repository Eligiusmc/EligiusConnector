package com.makrozai.eligiusconnector.discord;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class ConsoleLogReaderTest {

    @Test
    void splitsLongLogsIntoClosedDiscordCodeBlocks() {
        var chunks = ConsoleLogReader.splitLogMessages("x".repeat(1900) + "\n``` @everyone " + "😀".repeat(1000));

        assertTrue(chunks.size() > 1);
        assertTrue(chunks.stream().allMatch(chunk -> chunk.length() <= 2000));
        assertTrue(chunks.stream().allMatch(chunk -> chunk.startsWith("```ansi\n") && chunk.endsWith("```")));
        assertTrue(chunks.stream().noneMatch(chunk -> chunk.contains("\n``` @everyone")));
    }
}
