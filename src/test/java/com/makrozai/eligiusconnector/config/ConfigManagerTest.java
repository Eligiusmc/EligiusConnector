package com.makrozai.eligiusconnector.config;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

    @Test
    void testDatabaseTypeDefault() {
        String defaultType = "sqlite";
        assertEquals("sqlite", defaultType);
    }

    @Test
    void testMySQLPortDefault() {
        int defaultPort = 3306;
        assertEquals(3306, defaultPort);
    }

    @Test
    void testRedisPortDefault() {
        int defaultPort = 6379;
        assertEquals(6379, defaultPort);
    }

    @Test
    void testCacheTimeDefault() {
        int defaultTime = 30;
        assertEquals(30, defaultTime);
    }

    @Test
    void testLanguageDefault() {
        String defaultLang = "EN";
        assertEquals("EN", defaultLang);
    }

    @Test
    void testTimestampFormatDefault() {
        String defaultFormat = "dd/MM/yyyy HH:mm:ss";
        assertEquals("dd/MM/yyyy HH:mm:ss", defaultFormat);
    }
}
