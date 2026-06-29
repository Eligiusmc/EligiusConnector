package com.makrozai.eligiusconnector.listeners;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ListenersTest {

    @Test
    void testChatFormat() {
        String format = "[MC] %player_displayname%: {message}";
        String result = format
                .replace("%player_displayname%", "TestPlayer")
                .replace("{message}", "Hello World");
        assertEquals("[MC] TestPlayer: Hello World", result);
    }

    @Test
    void testJoinMessage() {
        String message = ":arrow_right: **%player_displayname%** se ha conectado.\n:earth_africa: Mundo: %player_world%";
        String result = message
                .replace("%player_displayname%", "TestPlayer")
                .replace("%player_world%", "world");
        assertTrue(result.contains("TestPlayer"));
        assertTrue(result.contains("world"));
    }

    @Test
    void testQuitMessage() {
        String message = ":arrow_left: **%player_displayname%** se ha desconectado.";
        String result = message
                .replace("%player_displayname%", "TestPlayer");
        assertTrue(result.contains("TestPlayer"));
    }

    @Test
    void testDeathMessage() {
        String message = ":skull: **%player_displayname%** ha muerto.\n:droplet: Vida: %player_health%";
        String result = message
                .replace("%player_displayname%", "TestPlayer")
                .replace("%player_health%", "20");
        assertTrue(result.contains("TestPlayer"));
        assertTrue(result.contains("20"));
    }

    @Test
    void testFilterMessage() {
        String message = "Tu mensaje ha sido filtrado. Razón: {filter}";
        String result = message.replace("{filter}", "spam");
        assertTrue(result.contains("spam"));
    }

    @Test
    void testEventMessage() {
        String message = ":skull: **¡BOSS APARECIO!**\n:clock1: Duración: {duration} minutos";
        String result = message.replace("{duration}", "30");
        assertTrue(result.contains("30"));
    }
}
