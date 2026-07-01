package com.makrozai.eligiusconnector.counters;

import org.bukkit.configuration.file.YamlConfiguration;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class CounterConfigTest {

    @Test
    void parsePeriodicCounter() {
        String yaml =
                "online_players:\n" +
                "  type: periodic\n" +
                "  enabled: true\n" +
                "  channel: '123'\n" +
                "  format: '🟢 online: %server_online%'\n" +
                "  interval: 30\n";
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        CounterConfig config = CounterConfig.fromSection("online_players",
                cfg.getConfigurationSection("online_players"));

        assertEquals("online_players", config.id());
        assertEquals("periodic", config.type());
        assertTrue(config.enabled());
        assertEquals("123", config.channelId());
        assertEquals(30, config.intervalSeconds());
        assertTrue(config.isPeriodic());
        assertFalse(config.isState());
    }

    @Test
    void parseStateCounter() {
        String yaml =
                "server_status:\n" +
                "  type: state\n" +
                "  enabled: true\n" +
                "  channel: '456'\n" +
                "  state_key: server_online\n" +
                "  states:\n" +
                "    online: '🟢 Online'\n" +
                "    offline: '🔴 Offline'\n";
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        CounterConfig config = CounterConfig.fromSection("server_status",
                cfg.getConfigurationSection("server_status"));

        assertEquals("server_status", config.id());
        assertEquals("state", config.type());
        assertTrue(config.enabled());
        assertEquals("456", config.channelId());
        assertEquals("server_online", config.stateKey());
        assertEquals("🟢 Online", config.states().get("online"));
        assertEquals("🔴 Offline", config.states().get("offline"));
        assertTrue(config.isState());
        assertFalse(config.isPeriodic());
    }

    @Test
    void parseDisabledCounter() {
        String yaml =
                "disabled_count:\n" +
                "  type: periodic\n" +
                "  enabled: false\n" +
                "  channel: '999'\n" +
                "  format: 'test'\n";
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        CounterConfig config = CounterConfig.fromSection("disabled_count",
                cfg.getConfigurationSection("disabled_count"));

        assertFalse(config.enabled());
    }

    @Test
    void defaultIntervalIs30() {
        String yaml =
                "no_interval:\n" +
                "  type: periodic\n" +
                "  enabled: true\n" +
                "  channel: '111'\n" +
                "  format: 'test'\n";
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        CounterConfig config = CounterConfig.fromSection("no_interval",
                cfg.getConfigurationSection("no_interval"));

        assertEquals(30, config.intervalSeconds());
    }

    @Test
    void supportsUpdateIntervalSecondsKey() {
        String yaml =
                "with_update:\n" +
                "  type: periodic\n" +
                "  enabled: true\n" +
                "  channel: '222'\n" +
                "  format: 'test'\n" +
                "  update_interval_seconds: 60\n";
        YamlConfiguration cfg = YamlConfiguration.loadConfiguration(new StringReader(yaml));
        CounterConfig config = CounterConfig.fromSection("with_update",
                cfg.getConfigurationSection("with_update"));

        assertEquals(60, config.intervalSeconds());
    }
}
