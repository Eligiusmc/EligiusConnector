package com.makrozai.eligiusconnector.database;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.config.ConfigAdapter;
import com.makrozai.eligiusconnector.stats.PlayerStatsManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.util.UUID;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DatabaseManagerTest {

    @TempDir
    Path dataFolder;
    private DatabaseManager database;
    private PlayerStatsManager stats;

    @BeforeEach
    void setUp() {
        EligiusConnector plugin = mock(EligiusConnector.class);
        ConfigAdapter config = mock(ConfigAdapter.class);
        when(plugin.getConfigAdapter()).thenReturn(config);
        when(config.getDatabaseType()).thenReturn("sqlite");
        when(plugin.getDataFolder()).thenReturn(dataFolder.toFile());
        when(plugin.getLogger()).thenReturn(Logger.getLogger("DatabaseManagerTest"));

        database = new DatabaseManager(plugin);
        when(plugin.getDatabaseManager()).thenReturn(database);
        database.initialize();
        stats = new PlayerStatsManager(plugin);
        stats.createTable();
    }

    @AfterEach
    void tearDown() {
        database.close();
    }

    @Test
    void linksAndUnlinksAnAccountInSqlite() {
        UUID uuid = UUID.randomUUID();

        assertTrue(database.linkAccount(1234L, uuid, "TestPlayer", "TestPlayer"));
        assertEquals(uuid, database.getMinecraftUuid(1234L));
        assertEquals("TestPlayer", database.getMinecraftName(1234L));
        assertTrue(database.unlinkAccount(1234L));
        assertNull(database.getMinecraftUuid(1234L));
    }

    @Test
    void firstJoinCreatesStatsAndSubsequentJoinPreservesThem() {
        UUID uuid = UUID.randomUUID();
        assertFalse(database.hasPlayedBefore(uuid));

        stats.updateJoin(uuid, "FirstName");
        var first = stats.getStats(uuid);
        assertNotNull(first);
        assertEquals("FirstName", first.getPlayerName());
        assertTrue(database.hasPlayedBefore(uuid));

        stats.updateJoin(uuid, "NewName");
        var updated = stats.getStats(uuid);
        assertEquals("NewName", updated.getPlayerName());
        assertEquals(1, stats.getUniqueJoinCount());
    }
}
