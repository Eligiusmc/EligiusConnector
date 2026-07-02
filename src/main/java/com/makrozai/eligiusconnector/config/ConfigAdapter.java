package com.makrozai.eligiusconnector.config;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigAdapter {

    private final EligiusConnector plugin;
    private FileConfiguration config;
    private FileConfiguration chatConfig;
    private FileConfiguration statusConfig;
    private FileConfiguration playerEventsConfig;
    private FileConfiguration verifyConfig;
    private FileConfiguration consoleConfig;
    private FileConfiguration synchronizationConfig;
    private FileConfiguration countersConfig;
    private FileConfiguration profileConfig;
    private FileConfiguration language;

    public ConfigAdapter(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        config = loadConfig("config.yml");
        chatConfig = loadConfig("chat.yml");
        statusConfig = loadConfig("status.yml");
        playerEventsConfig = loadConfig("player_events.yml");
        verifyConfig = loadConfig("verify.yml");
        consoleConfig = loadConfig("console.yml");
        synchronizationConfig = loadConfig("synchronization.yml");
        countersConfig = loadConfig("counters.yml");
        profileConfig = loadConfig("profile.yml");
        loadLanguage();
    }

    public void reloadAll() {
        config = reloadConfig("config.yml");
        chatConfig = reloadConfig("chat.yml");
        statusConfig = reloadConfig("status.yml");
        playerEventsConfig = reloadConfig("player_events.yml");
        verifyConfig = reloadConfig("verify.yml");
        consoleConfig = reloadConfig("console.yml");
        synchronizationConfig = reloadConfig("synchronization.yml");
        countersConfig = reloadConfig("counters.yml");
        profileConfig = reloadConfig("profile.yml");
        loadLanguage();
    }

    private FileConfiguration loadConfig(String fileName) {
        File file = new File(plugin.getDataFolder(), fileName);
        if (!file.exists()) {
            plugin.getDataFolder().mkdirs();
            plugin.saveResource(fileName, false);
        }
        FileConfiguration cfg = YamlConfiguration.loadConfiguration(file);
        try (InputStream defStream = plugin.getResource(fileName)) {
            if (defStream != null) {
                FileConfiguration defaults = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defStream, StandardCharsets.UTF_8));
                cfg.setDefaults(defaults);
            }
        } catch (Exception ignored) {}
        return cfg;
    }

    private FileConfiguration reloadConfig(String fileName) {
        return YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), fileName));
    }

    private void loadLanguage() {
        String lang = config.getString("language", "es");
        File langFile = new File(plugin.getDataFolder(), "lang/" + lang + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + lang + ".yml", false);
        }
        language = YamlConfiguration.loadConfiguration(langFile);
    }

    // ==========================================
    //  CORE CONFIG GETTERS
    // ==========================================

    public String getBotToken() {
        String token = config.getString("bot.token", "");
        if (token == null || token.isEmpty()) return "";
        if (token.contains("${")) {
            int start = token.indexOf("${") + 2;
            int end = token.indexOf("}");
            if (end > start) {
                String envVar = token.substring(start, end);
                String envValue = System.getenv(envVar);
                return envValue != null ? envValue : token;
            }
        }
        return token;
    }

    public String getServerId() { return config.getString("bot.server_id", ""); }
    public String getLanguageCode() { return config.getString("language", "es"); }
    public boolean isDebug() { return config.getBoolean("debug", false); }

    public List<String> getGameStatus() {
        return config.getStringList("bot.game_status");
    }

    // ==========================================
    //  DATABASE CONFIG GETTERS
    // ==========================================

    public String getDatabaseType() { return config.getString("database.type", "sqlite"); }
    public String getMySQLHost() { return config.getString("database.mysql.host", "localhost"); }
    public String getMySQLPort() { return String.valueOf(config.getInt("database.mysql.port", 3306)); }
    public String getMySQLDatabase() { return config.getString("database.mysql.database", "eligiusconnector"); }
    public String getMySQLUsername() { return config.getString("database.mysql.username", "root"); }
    public String getMySQLPassword() { return config.getString("database.mysql.password", ""); }
    public int getMySQLMaxPoolSize() { return config.getInt("database.mysql.max_pool_size", 10); }

    // ==========================================
    //  MODULE TOGGLES (file presence + channel validity)
    //  Only synchronization.yml uses its own enabled: flag
    //  Counters enabled only if counters section has entries
    // ==========================================

    private boolean hasChannel(FileConfiguration cfg, String path) {
        String channelId = cfg.getString(path, "");
        return channelId != null && !channelId.isEmpty() && !channelId.equals("CHANNEL_ID");
    }

    private void log(String module, boolean enabled, String reason) {
        String status = enabled ? "&aENABLED" : "&cDISABLED";
        String extra = reason != null && !reason.isEmpty() ? " &7(" + reason + ")" : "";
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&',
                "&6[EC] &r" + module + ": " + status + extra));
    }

    public boolean isChatEnabled() {
        boolean enabled = hasChannel(chatConfig, "channel");
        if (!enabled) log("Chat", false, "no channel in chat.yml");
        return enabled;
    }

    public boolean isStatusEnabled() {
        return true;
    }

    public boolean isJoinLeaveEnabled() {
        return true;
    }

    public boolean isJoinEnabled() {
        boolean enabled = playerEventsConfig.getBoolean("join.enabled", true);
        if (!enabled) log("Join", false, "join.enabled=false");
        return enabled;
    }

    public boolean isLeaveEnabled() {
        boolean enabled = playerEventsConfig.getBoolean("leave.enabled", true);
        if (!enabled) log("Leave", false, "leave.enabled=false");
        return enabled;
    }

    public boolean isDeathsEnabled() {
        if (!playerEventsConfig.getBoolean("death.enabled", true)) {
            log("Deaths", false, "death.enabled=false");
            return false;
        }
        if (!hasChannel(playerEventsConfig, "death.channel")) {
            log("Deaths", false, "no channel in player_events.yml");
            return false;
        }
        return true;
    }

    public boolean isAdvancementsEnabled() {
        if (!playerEventsConfig.getBoolean("advancement.enabled", true)) {
            log("Advancements", false, "advancement.enabled=false");
            return false;
        }
        if (!hasChannel(playerEventsConfig, "advancement.channel")) {
            log("Advancements", false, "no channel in player_events.yml");
            return false;
        }
        return true;
    }

    public boolean isVerifyEnabled() {
        boolean enabled = hasChannel(verifyConfig, "channel");
        if (!enabled) log("Verify", false, "no channel in verify.yml");
        return enabled;
    }

    public boolean isConsoleEnabled() {
        boolean enabled = hasChannel(consoleConfig, "channel");
        if (!enabled) log("Console relay", false, "no channel in console.yml");
        return enabled;
    }

    public boolean isSynchronizationEnabled() {
        boolean enabled = synchronizationConfig.getBoolean("enabled", true);
        if (!enabled) log("Synchronization", false, "enabled=false");
        return enabled;
    }

    public boolean isCountersEnabled() {
        boolean hasEntries = countersConfig.getConfigurationSection("counters") != null
                && !countersConfig.getConfigurationSection("counters").getKeys(false).isEmpty();
        if (!hasEntries) log("Counters", false, "no counters defined");
        return hasEntries;
    }

    public boolean isProfileEnabled() {
        return true;
    }

    public boolean isEventsEnabled() {
        File eventsDir = new File(plugin.getDataFolder(), "events");
        if (!eventsDir.exists() || !eventsDir.isDirectory()) {
            return false;
        }
        File[] eventFiles = eventsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        boolean enabled = eventFiles != null && eventFiles.length > 0;
        if (!enabled) log("Events", false, "no event files in events/");
        return enabled;
    }

    public void logModules() {
        Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&6=== Module Status ==="));
        log("Chat", isChatEnabled(), null);
        log("Status", isStatusEnabled(), null);
        log("Player Events", isJoinLeaveEnabled(), null);
        log("  - Join", isJoinEnabled(), null);
        log("  - Leave", isLeaveEnabled(), null);
        log("  - Deaths", isDeathsEnabled(), null);
        log("  - Advancements", isAdvancementsEnabled(), null);
        log("Verify", isVerifyEnabled(), null);
        log("Console relay", isConsoleEnabled(), null);
        log("Synchronization", isSynchronizationEnabled(), null);
        log("Counters", isCountersEnabled(), null);
        log("Profile panels", isProfileEnabled(), null);
        log("Events", isEventsEnabled(), null);
    }

    // ==========================================
    //  CHAT CONFIG GETTERS
    // ==========================================

    public FileConfiguration getChatConfig() { return chatConfig; }
    public String getChatChannelId() { return chatConfig.getString("channel", ""); }
    public String getGlobalChannelId() { return chatConfig.getString("channel", ""); }
    public boolean isMcToDiscordEnabled() { return chatConfig.getBoolean("mc_to_discord", true); }
    public boolean isDiscordToMcEnabled() { return chatConfig.getBoolean("discord_to_mc", true); }
    public String getChatFormatMCToDC() { return chatConfig.getString("format.mc_to_discord", "{player}: {message}"); }
    public String getChatFormatDiscordToMC() { return chatConfig.getString("format.discord_to_mc", "[DC] {player}: {message}"); }
    public boolean isWebhookEnabled() { return chatConfig.getBoolean("webhooks.enabled", true); }
    public String getWebhookName() { return chatConfig.getString("webhooks.name", "Minecraft"); }
    public String getWebhookAvatar() { return chatConfig.getString("webhooks.avatar", "https://minotar.net/avatar/{player}/128"); }

    public boolean isSpamFilterEnabled() { return chatConfig.getBoolean("filters.spam.enabled", true); }
    public int getSpamMaxMessages() { return chatConfig.getInt("filters.spam.max_messages", 5); }
    public int getSpamTimeWindowSeconds() { return chatConfig.getInt("filters.spam.time_window_seconds", 10); }

    public boolean isProfanityFilterEnabled() { return chatConfig.getBoolean("filters.profanity.enabled", true); }
    public List<String> getBlockedWords() { return chatConfig.getStringList("filters.profanity.words"); }
    public String getProfanityReplacement() { return chatConfig.getString("filters.profanity.replacement", "*"); }

    public boolean isLinksFilterEnabled() { return chatConfig.getBoolean("filters.links.enabled", true); }
    public String getLinksMode() { return chatConfig.getString("filters.links.mode", "whitelist"); }
    public List<String> getLinksDomains() { return chatConfig.getStringList("filters.links.domains"); }

    public boolean isCapsFilterEnabled() { return chatConfig.getBoolean("filters.caps.enabled", true); }
    public int getCapsMaxPercentage() { return chatConfig.getInt("filters.caps.max_percentage", 70); }
    public int getCapsMinLength() { return chatConfig.getInt("filters.caps.min_length", 10); }

    // Convenience aliases for callers
    public boolean isChatBridgeEnabled() { return isChatEnabled() && isMcToDiscordEnabled(); }
    public boolean isFilterEnabled() { return isSpamFilterEnabled() || isProfanityFilterEnabled() || isLinksFilterEnabled() || isCapsFilterEnabled(); }
    public boolean isAntiSpamEnabled() { return isSpamFilterEnabled(); }
    public int getMaxMessages() { return getSpamMaxMessages(); }
    public int getTimeWindowSeconds() { return getSpamTimeWindowSeconds(); }
    public boolean isUseWebhooks() { return isWebhookEnabled(); }
    public String getChatMcToDiscord() { return getChatFormatMCToDC(); }

    // ==========================================
    //  STATUS CONFIG GETTERS
    // ==========================================

    public FileConfiguration getStatusConfig() { return statusConfig; }
    public String getStatusChannelId() { return statusConfig.getString("channel", ""); }
    public Map<String, Object> getStatusOnEmbed() { return getEmbedFromConfig(statusConfig, "online"); }
    public Map<String, Object> getStatusOffEmbed() { return getEmbedFromConfig(statusConfig, "offline"); }

    // ==========================================
    //  PLAYER EVENTS CONFIG GETTERS
    // ==========================================

    public FileConfiguration getPlayerEventsConfig() { return playerEventsConfig; }

    public String getJoinChannelId() { return playerEventsConfig.getString("join.channel", ""); }
    public Map<String, Object> getJoinEmbed() { return getEmbedFromConfig(playerEventsConfig, "join.embed"); }

    public String getLeaveChannelId() { return playerEventsConfig.getString("leave.channel", ""); }
    public Map<String, Object> getLeaveEmbed() { return getEmbedFromConfig(playerEventsConfig, "leave.embed"); }

    public String getDeathChannelId() { return playerEventsConfig.getString("death.channel", ""); }
    public Map<String, Object> getDeathEmbed() { return getEmbedFromConfig(playerEventsConfig, "death.embed"); }

    public String getAdvancementChannelId() { return playerEventsConfig.getString("advancement.channel", ""); }
    public Map<String, Object> getAdvancementEmbed() { return getEmbedFromConfig(playerEventsConfig, "advancement.embed"); }

    // Aliases for callers using old names
    public String getJoinsChannelId() { return getJoinChannelId(); }
    public String getDeathsChannelId() { return getDeathChannelId(); }
    public String getMissionsChannelId() { return getAdvancementChannelId(); }
    public String getBossEventsChannelId() { return chatConfig.getString("boss_events_channel", ""); }
    public Map<String, Object> getBossSpawnEmbed() { return getEmbedFromConfig(chatConfig, "boss_spawn_embed"); }
    public Map<String, Object> getBossDeathEmbed() { return getEmbedFromConfig(chatConfig, "boss_death_embed"); }

    // ==========================================
    //  VERIFY CONFIG GETTERS
    // ==========================================

    public FileConfiguration getVerifyConfig() { return verifyConfig; }
    public String getVerifyChannelId() { return verifyConfig.getString("channel", ""); }
    public int getVerifyCodeLength() { return verifyConfig.getInt("code.length", 6); }
    public int getVerifyCodeExpiryMinutes() { return verifyConfig.getInt("code.expiry_minutes", 5); }
    public Map<String, Object> getVerifyWelcomeEmbed() { return getEmbedFromConfig(verifyConfig, "welcome_embed"); }

    // Verify sounds
    public boolean isVerifySoundsEnabled() { return verifyConfig.getBoolean("sounds.enabled", true); }
    public String getVerifySuccessSound() { return verifyConfig.getString("sounds.success.sound", "entity.player.levelup"); }
    public float getVerifySuccessVolume() { return (float) verifyConfig.getDouble("sounds.success.volume", 1.0); }
    public float getVerifySuccessPitch() { return (float) verifyConfig.getDouble("sounds.success.pitch", 1.0); }
    public String getVerifyErrorSound() { return verifyConfig.getString("sounds.error.sound", "entity.villager.no"); }
    public float getVerifyErrorVolume() { return (float) verifyConfig.getDouble("sounds.error.volume", 1.0); }
    public float getVerifyErrorPitch() { return (float) verifyConfig.getDouble("sounds.error.pitch", 1.0); }

    // Post-verify commands
    public java.util.List<String> getPostVerifyCommands() { return verifyConfig.getStringList("post_verify_commands"); }

    public int getCodeExpiryMinutes() { return getVerifyCodeExpiryMinutes(); }

    // ==========================================
    //  CONSOLE CONFIG GETTERS
    // ==========================================

    public FileConfiguration getConsoleConfig() { return consoleConfig; }
    public String getConsoleChannelId() { return consoleConfig.getString("channel", ""); }
    public String getConsoleLogLevel() { return consoleConfig.getString("log_level", "info"); }
    public int getConsoleRefreshRate() { return consoleConfig.getInt("refresh_rate_seconds", 2); }
    public List<String> getConsoleBlacklist() { return consoleConfig.getStringList("blacklist"); }
    public boolean isConsoleReverseEnabled() { return consoleConfig.getBoolean("reverse.enabled", true); }
    public String getConsoleReversePrefix() { return consoleConfig.getString("reverse.prefix", "!"); }
    public String getConsoleReversePermission() { return consoleConfig.getString("reverse.permission_required", "connector.console"); }
    public List<String> getConsoleReverseBlacklist() { return consoleConfig.getStringList("reverse.blacklist"); }

    // ==========================================
    //  COUNTERS CONFIG
    // ==========================================

    public FileConfiguration getCountersConfig() { return countersConfig; }

    // ==========================================
    //  SYNCHRONIZATION CONFIG GETTERS
    // ==========================================

    public boolean isRoleSyncEnabled() { return synchronizationConfig.getBoolean("roles.enabled", false); }
    public boolean isRoleOnLinkEnabled() { return synchronizationConfig.getBoolean("roles.on_link", true); }
    public String getVerifiedRoleId() { return synchronizationConfig.getString("roles.group_mapping.verified", ""); }
    public void setVerifiedRoleId(String roleId) {
        synchronizationConfig.set("roles.group_mapping.verified", roleId);
        try {
            File file = new File(plugin.getDataFolder(), "synchronization.yml");
            synchronizationConfig.save(file);
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save verified role ID: " + e.getMessage());
        }
    }
    public Map<String, String> getRoleGroupMapping() {
        Map<String, String> mapping = new HashMap<>();
        var section = synchronizationConfig.getConfigurationSection("roles.group_mapping");
        if (section != null) {
            for (String key : section.getKeys(false)) {
                mapping.put(key, String.valueOf(section.get(key)));
            }
        }
        return mapping;
    }

    // Nickname sync
    public boolean isNicknameSyncEnabled() { return synchronizationConfig.getBoolean("nickname.enabled", true); }
    public int getNicknameCycleSeconds() { return synchronizationConfig.getInt("nickname.cycle_time_seconds", 30); }
    public String getNicknameFormat() { return synchronizationConfig.getString("nickname.format", "%player_name%"); }

    // ==========================================
    //  PROFILE CONFIG GETTERS
    // ==========================================

    public FileConfiguration getProfileConfig() { return profileConfig; }
    public Map<String, Object> getProfileEmbed() { return getEmbedFromConfig(profileConfig, "profile"); }
    public Map<String, Object> getInventoryEmbed() { return getEmbedFromConfig(profileConfig, "inventory"); }
    public Map<String, Object> getWhereamiEmbed() { return getEmbedFromConfig(profileConfig, "whereami"); }

    // ==========================================
    //  EVENTS CONFIG
    // ==========================================

    public String getEventsFolder() { return "events"; }

    // ==========================================
    //  LANGUAGE
    // ==========================================

    public String getLang(String key) {
        return language != null ? language.getString(key, key) : key;
    }

    // ==========================================
    //  RAW CONFIG ACCESS (for backward compat)
    // ==========================================

    public FileConfiguration getConfig() { return config; }

    // ==========================================
    //  INTERNAL HELPERS
    // ==========================================

    private Map<String, Object> getEmbedFromConfig(FileConfiguration cfg, String path) {
        Map<String, Object> embed = new HashMap<>();
        ConfigurationSection section = cfg.getConfigurationSection(path);
        if (section == null) return embed;

        embed.put("title", section.getString("title", ""));
        embed.put("description", section.getString("description", ""));
        embed.put("color", section.getInt("color", 0x5865F2));
        embed.put("thumbnail", section.getString("thumbnail", ""));
        embed.put("image", section.getString("image", ""));
        embed.put("author", section.getString("author", ""));
        embed.put("authorIcon", section.getString("author_icon", ""));
        embed.put("footer", section.getString("footer", ""));
        embed.put("footerIcon", section.getString("footer_icon", ""));
        embed.put("timestamp", section.getBoolean("timestamp", false));

        List<Map<String, Object>> fields = new ArrayList<>();
        List<?> fieldsList = section.getList("fields");
        if (fieldsList != null) {
            for (Object obj : fieldsList) {
                if (obj instanceof Map<?, ?> rawMap) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> fieldMap = (Map<String, Object>) rawMap;
                    Map<String, Object> field = new HashMap<>();
                    field.put("name", String.valueOf(fieldMap.getOrDefault("name", "")));
                    field.put("value", String.valueOf(fieldMap.getOrDefault("value", "")));
                    field.put("inline", fieldMap.getOrDefault("inline", false));
                    fields.add(field);
                }
            }
        }
        embed.put("fields", fields);
        return embed;
    }
}
