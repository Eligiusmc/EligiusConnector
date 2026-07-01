package com.makrozai.eligiusconnector.placeholders;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.discord.DiscordManager;
import net.dv8tion.jda.api.entities.Guild;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class PlaceholderResolverTest {

    private static ServerMock server;
    private PlaceholderResolver resolver;
    private EligiusConnector pluginMock;
    private DiscordManager discordManagerMock;

    @BeforeAll
    static void setUp() {
        server = MockBukkit.mock();
    }

    @AfterAll
    static void tearDown() {
        MockBukkit.unmock();
    }

    @BeforeEach
    void init() {
        pluginMock = mock(EligiusConnector.class);
        discordManagerMock = mock(DiscordManager.class);
        when(pluginMock.getStartTime()).thenReturn(System.currentTimeMillis());
        when(pluginMock.getDiscordManager()).thenReturn(discordManagerMock);
        when(discordManagerMock.getGuild()).thenReturn(null); // no guild in test

        resolver = new PlaceholderResolver(pluginMock);
    }

    @Test
    void resolvePlayerCount() {
        String result = resolver.resolve("%server_online%");
        assertNotNull(result);
        assertEquals("0", result);
    }

    @Test
    void resolveMaxPlayers() {
        String result = resolver.resolve("%server_max_players%");
        assertNotNull(result);
        assertEquals(String.valueOf(server.getMaxPlayers()), result);
    }

    @Test
    void resolveServerName() {
        String result = resolver.resolve("%server_name%");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void resolveRamUsed() {
        String result = resolver.resolve("%server_ram_used%");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void resolveRamFree() {
        String result = resolver.resolve("%server_ram_free%");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void resolveRamTotal() {
        String result = resolver.resolve("%server_ram_total%");
        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void resolveUptime() {
        String result = resolver.resolve("%server_uptime%");
        assertNotNull(result);
        assertTrue(result.endsWith("m"), "Uptime should end with 'm': " + result);
    }

    @Test
    void resolveCount() {
        String result = resolver.resolve("%count%");
        assertNotNull(result);
        assertEquals("0", result);
    }

    @Test
    void resolveMembersFallsBackToZeroWithoutGuild() {
        String result = resolver.resolve("%server_members%");
        assertEquals("0", result);
    }

    @Test
    void resolveMultiplePlaceholders() {
        String text = "%server_online%/%server_max_players%";
        String result = resolver.resolve(text);
        assertNotNull(result);
        assertTrue(result.matches("\\d+/\\d+"), "Expected N/N format: " + result);
    }

    @Test
    void resolveUnknownPlaceholderPassesThrough() {
        String result = resolver.resolve("%nonexistent_xyz%");
        assertEquals("%nonexistent_xyz%", result);
    }

    @Test
    void resolveNullText() {
        assertNull(resolver.resolve(null));
        assertNull(resolver.resolve(null, null));
    }

    @Test
    void resolveEmptyText() {
        assertEquals("", resolver.resolve(""));
    }

    @Test
    void resolveNoPlaceholders() {
        String plain = "Hello World 123";
        assertEquals(plain, resolver.resolve(plain));
    }

    @Test
    void resolvePlayerNameWithNullPlayer() {
        String result = resolver.resolve("%player_name%");
        assertEquals("", result);
    }

    @Test
    void resolvePlayerDisplayNameWithNullPlayer() {
        String result = resolver.resolve("%player_displayname%");
        assertEquals("", result);
    }

    @Test
    void customPlaceholderRegistration() {
        resolver.register("test_custom", player -> "custom_value");
        assertEquals("prefix custom_value suffix", resolver.resolve("prefix %test_custom% suffix"));
    }
}
