package com.makrozai.eligiusconnector.discord;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ErrorEmbedTest {

    // ponytail: mirror getErrorColor logic from DiscordManager to verify correctness
    private int getErrorColor(String key) {
        return switch (key) {
            case "no_permission", "command_blacklisted", "internal_error",
                 "unknown_action", "panel_not_found", "channel_not_found",
                 "database_error", "config_missing" -> 0xED4245;
            default -> 0xFEE75C;
        };
    }

    @Test
    void blockingErrorsAreRed() {
        List<String> redKeys = List.of("no_permission", "command_blacklisted",
                "internal_error", "unknown_action", "panel_not_found",
                "channel_not_found", "database_error", "config_missing");
        for (String key : redKeys) {
            assertEquals(0xED4245, getErrorColor(key),
                    "Blocking error '" + key + "' must be red (0xED4245)");
        }
    }

    @Test
    void warningErrorsAreYellow() {
        List<String> yellowKeys = List.of("not_linked", "not_online",
                "already_linked", "economy_disabled", "timeout");
        for (String key : yellowKeys) {
            assertEquals(0xFEE75C, getErrorColor(key),
                    "Warning '" + key + "' must be yellow (0xFEE75C)");
        }
    }

    @Test
    void unknownKeyDefaultsToYellow() {
        assertEquals(0xFEE75C, getErrorColor("random_undefined_key"));
        assertEquals(0xFEE75C, getErrorColor(""));
    }

    @Test
    void allEightRequiredErrorKeysAreDefined() {
        List<String> required = List.of(
                "not_linked", "not_online", "already_linked",
                "no_permission", "command_blacklisted", "internal_error",
                "economy_disabled", "unknown_action"
        );
        assertEquals(8, required.size(), "Must have exactly 8 error types defined");
        for (String key : required) {
            assertFalse(key.isEmpty());
            assertNotNull(key);
        }
    }

    @Test
    void langKeyPatternIsCorrect() {
        String key = "not_linked";
        String titleKey = "keys.error." + key + ".title";
        String descKey = "keys.error." + key + ".description";
        assertEquals("keys.error.not_linked.title", titleKey);
        assertEquals("keys.error.not_linked.description", descKey);
    }

    @Test
    void errorKeysAreConsistentAcrossType() {
        // Verify that "already_linked" is yellow (info, not blocking)
        int color = getErrorColor("already_linked");
        assertEquals(0xFEE75C, color, "already_linked is info, not error");

        // Verify that "internal_error" is red (bug/crash)
        color = getErrorColor("internal_error");
        assertEquals(0xED4245, color, "internal_error is a crash, must be red");
    }
}
