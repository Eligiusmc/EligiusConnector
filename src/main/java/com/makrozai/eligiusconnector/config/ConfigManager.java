package com.makrozai.eligiusconnector.config;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

public class ConfigManager {

    private final EligiusConnector plugin;
    private FileConfiguration config;
    private FileConfiguration chatConfig;
    private FileConfiguration synchronizationConfig;
    private FileConfiguration notificationsConfig;
    private FileConfiguration eventsConfig;

    private File configFile;
    private File chatFile;
    private File synchronizationFile;
    private File notificationsFile;
    private File eventsFile;

    public ConfigManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void loadConfigs() {
        configFile = new File(plugin.getDataFolder(), "config.yml");
        chatFile = new File(plugin.getDataFolder(), "chat.yml");
        synchronizationFile = new File(plugin.getDataFolder(), "synchronization.yml");
        notificationsFile = new File(plugin.getDataFolder(), "notifications.yml");
        eventsFile = new File(plugin.getDataFolder(), "events.yml");

        // Create default configs
        saveDefaultConfig("config.yml");
        saveDefaultConfig("chat.yml");
        saveDefaultConfig("synchronization.yml");
        saveDefaultConfig("notifications.yml");
        saveDefaultConfig("events.yml");

        // Load configs
        config = YamlConfiguration.loadConfiguration(configFile);
        chatConfig = YamlConfiguration.loadConfiguration(chatFile);
        synchronizationConfig = YamlConfiguration.loadConfiguration(synchronizationFile);
        notificationsConfig = YamlConfiguration.loadConfiguration(notificationsFile);
        eventsConfig = YamlConfiguration.loadConfiguration(eventsFile);

        // Load defaults
        config.setDefaults(getDefaultConfig("config.yml"));
        chatConfig.setDefaults(getDefaultConfig("chat.yml"));
        synchronizationConfig.setDefaults(getDefaultConfig("synchronization.yml"));
        notificationsConfig.setDefaults(getDefaultConfig("notifications.yml"));
        eventsConfig.setDefaults(getDefaultConfig("events.yml"));

        plugin.getLogger().info("Configuration files loaded!");
    }

    private void saveDefaultConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource(fileName, false);
        }
    }

    private FileConfiguration getDefaultConfig(String fileName) {
        InputStream stream = plugin.getResource(fileName);
        if (stream != null) {
            return YamlConfiguration.loadConfiguration(new InputStreamReader(stream, StandardCharsets.UTF_8));
        }
        return null;
    }

    public void reloadConfigs() {
        config = YamlConfiguration.loadConfiguration(configFile);
        chatConfig = YamlConfiguration.loadConfiguration(chatFile);
        synchronizationConfig = YamlConfiguration.loadConfiguration(synchronizationFile);
        notificationsConfig = YamlConfiguration.loadConfiguration(notificationsFile);
        eventsConfig = YamlConfiguration.loadConfiguration(eventsFile);

        plugin.getLogger().info("Configuration files reloaded!");
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public FileConfiguration getChatConfig() {
        return chatConfig;
    }

    public FileConfiguration getSynchronizationConfig() {
        return synchronizationConfig;
    }

    public FileConfiguration getNotificationsConfig() {
        return notificationsConfig;
    }

    public FileConfiguration getEventsConfig() {
        return eventsConfig;
    }

    public String getBotToken() {
        return config.getString("BotToken", "").replace("${DISCORD_BOT_TOKEN}", System.getenv("DISCORD_BOT_TOKEN") != null ? System.getenv("DISCORD_BOT_TOKEN") : "");
    }

    public String getServerId() {
        return config.getString("ServerId", "");
    }

    public boolean isDebug() {
        return config.getBoolean("Debug", false);
    }

    public String getLanguage() {
        return config.getString("ForcedLanguage", "EN");
    }

    public String getTimezone() {
        return config.getString("Timezone", "default");
    }

    public String getTimestampFormat() {
        return config.getString("TimestampFormat", "dd/MM/yyyy HH:mm:ss");
    }

    public String getAvatarUrl() {
        return config.getString("AvatarUrl", "https://minotar.net/avatar/%player_name%/128");
    }

    public String getGlobalChannelId() {
        return config.getString("Channels.global", "");
    }

    public String getAdminChannelId() {
        return config.getString("Channels.admin", "");
    }

    public String getConsoleChannelId() {
        return config.getString("DiscordConsoleChannelId", "");
    }

    public boolean isPlaceholderAPIEnabled() {
        return config.getBoolean("PlaceholderAPI.Enabled", true);
    }

    public boolean isCachePlaceholders() {
        return config.getBoolean("PlaceholderAPI.CachePlaceholders", true);
    }

    public int getCacheTimeInSeconds() {
        return config.getInt("PlaceholderAPI.CacheTimeInSeconds", 30);
    }

    public String getDatabaseType() {
        return config.getString("Database.Type", "sqlite");
    }

    public String getMySQLHost() {
        return config.getString("Database.MySQL.Host", "localhost");
    }

    public int getMySQLPort() {
        return config.getInt("Database.MySQL.Port", 3306);
    }

    public String getMySQLDatabase() {
        return config.getString("Database.MySQL.Database", "eligiusconnector");
    }

    public String getMySQLUsername() {
        return config.getString("Database.MySQL.Username", "root");
    }

    public String getMySQLPassword() {
        return config.getString("Database.MySQL.Password", "");
    }

    public int getMySQLMaxPoolSize() {
        return config.getInt("Database.MySQL.MaxPoolSize", 10);
    }

    public String getRedisHost() {
        return config.getString("Database.Redis.Host", "localhost");
    }

    public int getRedisPort() {
        return config.getInt("Database.Redis.Port", 6379);
    }

    public String getRedisPassword() {
        return config.getString("Database.Redis.Password", "");
    }

    public int getRedisDatabase() {
        return config.getInt("Database.Redis.Database", 0);
    }
}
