package com.makrozai.eligiusconnector.chat;

import be.seeseemelk.mockbukkit.MockBukkit;
import be.seeseemelk.mockbukkit.ServerMock;
import be.seeseemelk.mockbukkit.entity.PlayerMock;
import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.config.ConfigAdapter;
import com.makrozai.eligiusconnector.database.DatabaseManager;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatFilterEngineTest {

    private static ServerMock server;
    private ChatFilterEngine engine;
    private EligiusConnector pluginMock;
    private ConfigAdapter configMock;
    private DatabaseManager dbMock;

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
        configMock = mock(ConfigAdapter.class);
        dbMock = mock(DatabaseManager.class);
        when(pluginMock.getConfigAdapter()).thenReturn(configMock);
        when(pluginMock.getDatabaseManager()).thenReturn(dbMock);

        // Default: all filters disabled (passes everything)
        when(configMock.isFilterEnabled()).thenReturn(false);

        engine = new ChatFilterEngine(pluginMock);
    }

    @Test
    void filterPassesWhenAllFiltersDisabled() {
        PlayerMock player = server.addPlayer();
        ChatFilterEngine.FilterResult result = engine.filter("test message", player);

        assertFalse(result.blocked());
        assertNull(result.reason());
    }

    @Test
    void filterPassesCleanMessage() {
        PlayerMock player = server.addPlayer();
        when(configMock.isFilterEnabled()).thenReturn(true);
        when(configMock.isAntiSpamEnabled()).thenReturn(true);
        when(configMock.getMaxMessages()).thenReturn(5);
        when(configMock.getTimeWindowSeconds()).thenReturn(10);

        // No profanity, no links, no caps abuse
        when(configMock.getBlockedWords()).thenReturn(List.of("badword"));
        when(configMock.isLinksFilterEnabled()).thenReturn(true);
        when(configMock.getLinksDomains()).thenReturn(List.of("youtube.com"));

        ChatFilterEngine.FilterResult result = engine.filter("hello world", player);

        assertFalse(result.blocked());
    }

    @Test
    void filterBlocksProfanity() {
        PlayerMock player = server.addPlayer();
        when(configMock.isFilterEnabled()).thenReturn(true);
        when(configMock.isProfanityFilterEnabled()).thenReturn(true);
        when(configMock.getBlockedWords()).thenReturn(List.of("badword"));

        ChatFilterEngine.FilterResult result = engine.filter("this contains badword", player);

        assertTrue(result.blocked());
        assertEquals("profanity", result.reason());
    }

    @Test
    void filterBlocksCapsAbuse() {
        PlayerMock player = server.addPlayer();
        when(configMock.isFilterEnabled()).thenReturn(true);
        when(configMock.isCapsFilterEnabled()).thenReturn(true);
        when(configMock.isAntiSpamEnabled()).thenReturn(false);
        when(configMock.getCapsMinLength()).thenReturn(10);
        when(configMock.getCapsMaxPercentage()).thenReturn(70);

        String capsMessage = "AAAAAAAAAAAAAAAAAAAAA";
        ChatFilterEngine.FilterResult result = engine.filter(capsMessage, player);

        assertTrue(result.blocked());
        assertEquals("caps", result.reason());
    }

    @Test
    void filterDoesNotBlockShortCapsMessage() {
        PlayerMock player = server.addPlayer();
        when(configMock.isFilterEnabled()).thenReturn(true);
        when(configMock.isCapsFilterEnabled()).thenReturn(true);
        when(configMock.isAntiSpamEnabled()).thenReturn(false);
        when(configMock.getCapsMinLength()).thenReturn(10);
        when(configMock.getCapsMaxPercentage()).thenReturn(70);

        // 5 chars, below min length
        ChatFilterEngine.FilterResult result = engine.filter("HELLO", player);

        assertFalse(result.blocked());
    }

    @Test
    void nullPlayerReturnsPassed() {
        when(configMock.isFilterEnabled()).thenReturn(true);
        ChatFilterEngine.FilterResult result = engine.filter("test", null);
        assertFalse(result.blocked());
    }

    @Test
    void nullMessageReturnsPassed() {
        PlayerMock player = server.addPlayer();
        when(configMock.isFilterEnabled()).thenReturn(true);
        ChatFilterEngine.FilterResult result = engine.filter(null, player);
        assertFalse(result.blocked());
    }

    @Test
    void resetViolationsClearsState() {
        PlayerMock player = server.addPlayer();
        when(configMock.isFilterEnabled()).thenReturn(true);
        when(configMock.isProfanityFilterEnabled()).thenReturn(true);
        when(configMock.getBlockedWords()).thenReturn(List.of("bad"));

        engine.filter("bad", player);
        engine.resetViolations(player.getUniqueId());

        // After reset, first offense should be warn, not mute
        engine.filter("bad", player);
        // Should still be 1st violation = warn (punishment is "warn")
        verify(dbMock, atLeastOnce()).addFilterWarning(eq(player.getUniqueId()), eq("profanity"), anyInt());
    }

    @Test
    void filterResultPassedHasNullReason() {
        ChatFilterEngine.FilterResult result = ChatFilterEngine.FilterResult.passed();
        assertFalse(result.blocked());
        assertNull(result.reason());
        assertNull(result.punishment());
    }

    @Test
    void filterResultBlockedHasReason() {
        ChatFilterEngine.FilterResult result = ChatFilterEngine.FilterResult.blocked("spam", "mute");
        assertTrue(result.blocked());
        assertEquals("spam", result.reason());
        assertEquals("mute", result.punishment());
    }
}
