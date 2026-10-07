package com.makrozai.eligiusconnector.discord;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Cubre los vectores de bypass del issue #4: la blacklist debe mirar el mismo
 * token normalizado que {@code Bukkit.dispatchCommand} va a ejecutar.
 */
class ConsoleBlacklistTest {

    private static final List<String> BLACKLIST = List.of(
            "stop", "restart", "reload", "ban", "ban-ip", "pardon", "pardon-ip",
            "op", "deop", "say", "gamemode", "whitelist", "kick", "plugins"
    );

    @Test
    void blocksPlainCommand() {
        assertTrue(DiscordListener.isBlacklisted("stop", BLACKLIST));
        assertTrue(DiscordListener.isBlacklisted("op Notch", BLACKLIST));
    }

    @Test
    void blocksLeadingSpaces() {
        assertTrue(DiscordListener.isBlacklisted("  op Notch", BLACKLIST));
        assertTrue(DiscordListener.isBlacklisted("\tstop", BLACKLIST));
    }

    @Test
    void blocksNamespacePrefix() {
        assertTrue(DiscordListener.isBlacklisted("minecraft:op Notch", BLACKLIST));
        assertTrue(DiscordListener.isBlacklisted("essentials:ban Notch", BLACKLIST));
        assertTrue(DiscordListener.isBlacklisted("minecraft:stop", BLACKLIST));
    }

    @Test
    void blocksSlashPrefix() {
        assertTrue(DiscordListener.isBlacklisted("/op Notch", BLACKLIST));
        assertTrue(DiscordListener.isBlacklisted("/minecraft:op Notch", BLACKLIST));
    }

    @Test
    void blocksCaseInsensitive() {
        assertTrue(DiscordListener.isBlacklisted("OP Notch", BLACKLIST));
        assertTrue(DiscordListener.isBlacklisted("StOp", BLACKLIST));
    }

    @Test
    void blocksCollapsesInnerWhitespace() {
        assertTrue(DiscordListener.isBlacklisted("op    Notch", BLACKLIST));
    }

    @Test
    void allowsNonBlacklistedCommand() {
        assertFalse(DiscordListener.isBlacklisted("list", BLACKLIST));
        assertFalse(DiscordListener.isBlacklisted("minecraft:list", BLACKLIST));
        assertFalse(DiscordListener.isBlacklisted("/help", BLACKLIST));
    }

    @Test
    void allowsPrefixThatIsNotTheCommand() {
        assertFalse(DiscordListener.isBlacklisted("stopthegrief", BLACKLIST));
        assertFalse(DiscordListener.isBlacklisted("operator", BLACKLIST));
    }

    @Test
    void blacklistEntriesMatchIgnoringCaseAndSpaces() {
        assertTrue(DiscordListener.isBlacklisted("stop", List.of("  STOP  ")));
    }

    @Test
    void emptyOrNullBlacklistBlocksNothing() {
        assertFalse(DiscordListener.isBlacklisted("stop", List.of()));
        assertFalse(DiscordListener.isBlacklisted("stop", null));
    }
}
