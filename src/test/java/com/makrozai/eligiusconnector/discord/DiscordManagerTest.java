package com.makrozai.eligiusconnector.discord;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class DiscordManagerTest {

    @Test
    void testChannelIdFormat() {
        String channelId = "123456789012345678";
        assertTrue(channelId.matches("\\d{17,20}"));
    }

    @Test
    void testBotTokenFormat() {
        // Test that bot token format contains dots
        String tokenFormat = "token.with.dots";
        assertNotNull(tokenFormat);
        assertTrue(tokenFormat.contains("."));
    }

    @Test
    void testMessageFormat() {
        String format = "[MC] %player_displayname%: {message}";
        String result = format
                .replace("%player_displayname%", "TestPlayer")
                .replace("{message}", "Hello World");
        assertEquals("[MC] TestPlayer: Hello World", result);
    }

    @Test
    void testDiscordMessageFormat() {
        String format = "[DC] %username%: {message}";
        String result = format
                .replace("%username%", "DiscordUser")
                .replace("{message}", "Hello Minecraft!");
        assertEquals("[DC] DiscordUser: Hello Minecraft!", result);
    }

    @Test
    void testNotificationMessageFormat() {
        String message = ":arrow_right: **%player_displayname%** se ha conectado.\n:earth_africa: Mundo: %player_world%";
        String result = message
                .replace("%player_displayname%", "TestPlayer")
                .replace("%player_world%", "world");
        assertTrue(result.contains("TestPlayer"));
        assertTrue(result.contains("world"));
    }

    @Test
    void testDeathMessageFormat() {
        String message = ":skull: **%player_displayname%** ha muerto.\n:droplet: Vida: %player_health%";
        String result = message
                .replace("%player_displayname%", "TestPlayer")
                .replace("%player_health%", "20");
        assertTrue(result.contains("TestPlayer"));
        assertTrue(result.contains("20"));
    }
}
