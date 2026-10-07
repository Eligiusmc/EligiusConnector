package com.makrozai.eligiusconnector.config;

import org.junit.jupiter.api.Test;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {

    @Test
    void defaultConfigDefinesSupportedDatabaseAndLanguage() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("config.yml")) {
            assertNotNull(stream);
            YamlConfiguration config = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));

            assertEquals("sqlite", config.getString("database.type"));
            assertEquals(3306, config.getInt("database.mysql.port"));
            assertEquals("en", config.getString("language"));
            assertEquals("${DISCORD_BOT_TOKEN}", config.getString("bot.token"));
        }
    }
}
