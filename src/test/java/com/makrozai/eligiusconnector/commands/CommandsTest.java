package com.makrozai.eligiusconnector.commands;

import org.junit.jupiter.api.Test;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class CommandsTest {

    @Test
    void pluginDescriptorDeclaresCommandsAndPermissions() throws Exception {
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("plugin.yml")) {
            assertNotNull(stream);
            YamlConfiguration descriptor = YamlConfiguration.loadConfiguration(
                    new InputStreamReader(stream, StandardCharsets.UTF_8));

            assertEquals("com.makrozai.eligiusconnector.EligiusConnector", descriptor.getString("main"));
            assertNotNull(descriptor.getConfigurationSection("commands.verify"));
            assertTrue(descriptor.getStringList("commands.eligiusconnector.aliases").contains("ec"));
            assertEquals("true", descriptor.getString("permissions.connector.verify.default"));
            assertEquals("op", descriptor.getString("permissions.connector.reload.default"));
            assertNull(descriptor.get("permissions.connector.console"));
        }
    }
}
