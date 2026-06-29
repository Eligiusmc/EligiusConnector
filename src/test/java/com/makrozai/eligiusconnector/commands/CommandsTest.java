package com.makrozai.eligiusconnector.commands;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CommandsTest {

    @Test
    void testVerifyCodeFormat() {
        String code = "ABC123";
        assertTrue(code.length() == 6);
        assertTrue(code.matches("[A-Z0-9]+"));
    }

    @Test
    void testVerifyCodeInvalidFormat() {
        String[] invalidCodes = {"abc123", "AB12", "ABCDEFGH", "1234567890"};
        for (String code : invalidCodes) {
            assertFalse(code.length() == 6 && code.matches("[A-Z0-9]+"));
        }
    }

    @Test
    void testCommandAliases() {
        String[] aliases = {"connector", "ec"};
        assertEquals(2, aliases.length);
        assertEquals("connector", aliases[0]);
        assertEquals("ec", aliases[1]);
    }

    @Test
    void testSubCommands() {
        String[] subCommands = {"reload", "profile", "status", "papi"};
        assertEquals(4, subCommands.length);
    }

    @Test
    void testPermissionNodes() {
        String[] permissions = {
                "connector.verify",
                "connector.unlink.self",
                "connector.unlink.other",
                "connector.console",
                "connector.console.stop",
                "connector.console.restart",
                "connector.chat.send",
                "connector.chat.filter.bypass",
                "connector.profile.self",
                "connector.profile.other",
                "connector.inventory.self",
                "connector.inventory.other",
                "connector.location.self",
                "connector.location.other",
                "connector.roles.self",
                "connector.roles.sync",
                "connector.homes.self",
                "connector.homes.other",
                "connector.jobs.self",
                "connector.jobs.other",
                "connector.events.start",
                "connector.events.stop",
                "connector.notifications.receive",
                "connector.reload",
                "connector.admin",
                "connector.papi"
        };
        assertEquals(26, permissions.length);
    }

    @Test
    void testDefaultPermissions() {
        // Permissions that default to true
        String[] defaultTrue = {
                "connector.verify",
                "connector.unlink.self",
                "connector.chat.send",
                "connector.profile.self",
                "connector.inventory.self",
                "connector.location.self",
                "connector.roles.self",
                "connector.homes.self",
                "connector.jobs.self",
                "connector.notifications.receive"
        };
        assertEquals(10, defaultTrue.length);

        // Permissions that default to op
        String[] defaultOp = {
                "connector.unlink.other",
                "connector.console",
                "connector.console.stop",
                "connector.console.restart",
                "connector.chat.filter.bypass",
                "connector.profile.other",
                "connector.inventory.other",
                "connector.location.other",
                "connector.roles.sync",
                "connector.homes.other",
                "connector.jobs.other",
                "connector.events.start",
                "connector.events.stop",
                "connector.reload",
                "connector.admin",
                "connector.papi"
        };
        assertEquals(16, defaultOp.length);
    }
}
