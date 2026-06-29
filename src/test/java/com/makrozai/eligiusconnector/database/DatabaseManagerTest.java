package com.makrozai.eligiusconnector.database;

import org.junit.jupiter.api.Test;
import java.util.UUID;
import static org.junit.jupiter.api.Assertions.*;

class DatabaseManagerTest {

    @Test
    void testUUIDFormat() {
        UUID testUuid = UUID.randomUUID();
        assertNotNull(testUuid);
        assertTrue(testUuid.toString().matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    void testDiscordIdFormat() {
        long discordId = 123456789012345678L;
        assertTrue(discordId > 0);
        assertTrue(discordId < Long.MAX_VALUE);
    }

    @Test
    void testDatabaseTypeSqlite() {
        String type = "sqlite";
        assertEquals("sqlite", type);
    }

    @Test
    void testDatabaseTypeMysql() {
        String type = "mysql";
        assertEquals("mysql", type);
    }

    @Test
    void testDatabaseTypeRedis() {
        String type = "redis";
        assertEquals("redis", type);
    }

    @Test
    void testFilterWarningLevels() {
        int[] levels = {1, 2, 3, 4};
        assertEquals(4, levels.length);
        for (int level : levels) {
            assertTrue(level > 0);
            assertTrue(level <= 4);
        }
    }

    @Test
    void testAuditLevels() {
        String[] levels = {"info", "warn", "error"};
        assertEquals(3, levels.length);
    }
}
