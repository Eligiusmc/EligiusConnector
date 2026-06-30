package com.makrozai.eligiusconnector.config;

import com.makrozai.eligiusconnector.EligiusConnector;
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
    private FileConfiguration messages;
    private FileConfiguration channels;
    private FileConfiguration language;

    public ConfigAdapter(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void loadAll() {
        config = loadConfig("config.yml");
        messages = loadConfig("messages.yml");
        channels = loadConfig("channels.yml");
        loadLanguage();
    }

    public void reloadAll() {
        config = reloadConfig("config.yml");
        messages = reloadConfig("messages.yml");
        channels = reloadConfig("channels.yml");
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

    // Main config getters
    public FileConfiguration getConfig() { return config; }
    public FileConfiguration getMessages() { return messages; }
    public FileConfiguration getChannels() { return channels; }
    public FileConfiguration getLanguage() { return language; }

    // Bot config
    public String getBotToken() {
        String token = config.getString("bot.token", "");
        if (token == null || token.isEmpty()) return "";
        if (token.contains("${")) {
            String envVar = token.substring(token.indexOf("{") + 1, token.indexOf("}"));
            String envValue = System.getenv(envVar);
            return envValue != null ? envValue : token;
        }
        return token;
    }

    public String getServerId() { return config.getString("bot.server_id", ""); }
    public String getBotStatus() { return config.getString("bot.status", "ONLINE"); }
    public List<String> getGameStatus() { return config.getStringList("bot.game_status"); }
    public int getUpdateInterval() { return config.getInt("bot.update_interval_minutes", 1); }

    // Database config
    public String getDatabaseType() { return config.getString("database.type", "sqlite"); }
    public String getMySQLHost() { return config.getString("database.mysql.host", "localhost"); }
    public int getMySQLPort() { return config.getInt("database.mysql.port", 3306); }
    public String getMySQLDatabase() { return config.getString("database.mysql.database", "eligiusconnector"); }
    public String getMySQLUsername() { return config.getString("database.mysql.username", "root"); }
    public String getMySQLPassword() { return config.getString("database.mysql.password", ""); }
    public int getMySQLMaxPoolSize() { return config.getInt("database.mysql.max_pool_size", 10); }
    public boolean isRedisEnabled() { return config.getBoolean("database.redis.enabled", false); }
    public String getRedisHost() { return config.getString("database.redis.host", "localhost"); }
    public int getRedisPort() { return config.getInt("database.redis.port", 6379); }
    public String getRedisPassword() { return config.getString("database.redis.password", ""); }
    public int getRedisDatabase() { return config.getInt("database.redis.database", 0); }

    // Module toggles
    public boolean isModuleEnabled(String module) {
        return config.getBoolean("modules." + module + ".enabled", true);
    }

    public boolean isChatBridgeEnabled() { return isModuleEnabled("chat_bridge"); }
    public boolean isMcToDiscordEnabled() { return config.getBoolean("modules.chat_bridge.mc_to_discord", true); }
    public boolean isDiscordToMcEnabled() { return config.getBoolean("modules.chat_bridge.discord_to_mc", true); }
    public boolean isUseWebhooks() { return config.getBoolean("modules.chat_bridge.use_webhooks", true); }
    public String getWebhookName() { return config.getString("modules.chat_bridge.webhook_name", "Minecraft"); }
    public boolean isFilterEnabled() { return config.getBoolean("modules.chat_bridge.filter.enabled", true); }
    public List<String> getBlockedWords() { return config.getStringList("modules.chat_bridge.filter.blocked_words"); }
    public boolean isAntiSpamEnabled() { return config.getBoolean("modules.chat_bridge.filter.anti_spam.enabled", true); }
    public int getMaxMessages() { return config.getInt("modules.chat_bridge.filter.anti_spam.max_messages", 5); }
    public int getTimeWindowSeconds() { return config.getInt("modules.chat_bridge.filter.anti_spam.time_window_seconds", 10); }

    public boolean isStatusEnabled() { return isModuleEnabled("status"); }
    public boolean isOnlineCounterEnabled() { return isModuleEnabled("online_counter"); }
    public boolean isJoinLeaveEnabled() { return isModuleEnabled("join_leave"); }
    public boolean isDeathsEnabled() { return isModuleEnabled("deaths"); }
    public boolean isAdvancementsEnabled() { return isModuleEnabled("advancements"); }
    public boolean isBossEventsEnabled() { return isModuleEnabled("boss_events"); }
    public boolean isConsoleEnabled() { return isModuleEnabled("console"); }
    public boolean isConsoleShowAllLogs() { return config.getBoolean("modules.console.show_all_logs", true); }
    public int getConsoleRefreshRate() { return config.getInt("modules.console.refresh_rate_seconds", 2); }
    public boolean isVerifyEnabled() { return isModuleEnabled("verify"); }
    public int getCodeExpiryMinutes() { return config.getInt("modules.verify.code_expiry_minutes", 5); }
    public boolean isBirthdayEnabled() { return isModuleEnabled("birthday"); }
    public String getBirthdayDateFormat() { return config.getString("modules.birthday.date_format", "dd/MM/yyyy"); }
    public List<String> getBirthdayRewardCommands() { return config.getStringList("modules.birthday.rewards.commands"); }
    public String getBirthdayRewardMessage() { return config.getString("modules.birthday.rewards.message", ""); }
    public String getBirthdayRewardRole() { return config.getString("modules.birthday.rewards.role", ""); }
    public boolean isEventsEnabled() { return isModuleEnabled("events"); }
    public String getEventsFolder() { return config.getString("modules.events.folder", "events"); }
    public boolean isPlaceholderApiEnabled() { return isModuleEnabled("placeholder_api"); }
    public boolean isPermissionsEnabled() { return isModuleEnabled("permissions"); }
    public boolean isUseLuckPerms() { return config.getBoolean("modules.permissions.use_luckperms", true); }
    public String getChatFormat() { return config.getString("modules.chat_bridge.format", "plain"); }
    public boolean isChatBridgeUseEmbed() { return "embed".equalsIgnoreCase(getChatFormat()); }
    public int getOnlineCounterInterval() { return config.getInt("modules.online_counter.update_interval_seconds", 30); }
    public int getAllMembersCounterInterval() { return config.getInt("modules.all_members_counter.update_interval_seconds", 300); }
    public int getBossRespawnMinutes() { return config.getInt("modules.boss_events.respawn_minutes", 30); }
    public List<String> getBossCommands() { return config.getStringList("modules.boss_events.commands"); }
    public boolean isDebug() { return config.getBoolean("debug", false); }
    public String getBirthdaysChannelId() { return channels.getString("birthdays", ""); }

    // Audit
    public boolean isAuditEnabled() { return config.getBoolean("audit.enabled", true); }
    public String getAuditLevel() { return config.getString("audit.level", "info"); }
    public int getAuditRetentionDays() { return config.getInt("audit.retention_days", 30); }

    // Channel IDs
    public String getGlobalChannelId() { return channels.getString("global", ""); }
    public String getConsoleChannelId() { return channels.getString("console", ""); }
    public String getVerifyChannelId() { return channels.getString("verify", ""); }
    public String getStatusChannelId() { return channels.getString("status", ""); }
    public String getJoinsChannelId() { return channels.getString("joins", ""); }
    public String getDeathsChannelId() { return channels.getString("deaths", ""); }
    public String getMissionsChannelId() { return channels.getString("missions", ""); }
    public String getBossEventsChannelId() { return channels.getString("boss_events", ""); }
    public String getOnlineChannelId() { return channels.getString("online", ""); }
    public String getOnlineFormat() { return channels.getString("online_format", "🟢 online: {count}"); }
    public String getAllMembersChannelId() { return channels.getString("all_members", ""); }
    public String getAllMembersFormat() { return channels.getString("all_members_format", "👥 all-members: {count}"); }

    // Messages - Embed builders
    public Map<String, Object> getStatusOnEmbed() { return getEmbedFromMessages("status.online"); }
    public Map<String, Object> getStatusOffEmbed() { return getEmbedFromMessages("status.offline"); }
    public Map<String, Object> getJoinEmbed() { return getEmbedFromMessages("join"); }
    public Map<String, Object> getLeaveEmbed() { return getEmbedFromMessages("leave"); }
    public Map<String, Object> getDeathEmbed() { return getEmbedFromMessages("death"); }
    public Map<String, Object> getAdvancementEmbed() { return getEmbedFromMessages("advancement"); }

    // Messages - Text
    public String getChatMcToDiscord() { return messages.getString("chat.mc_to_discord", "**[MC]** {player}: {message}"); }
    public String getChatDiscordToMc() { return messages.getString("chat.discord_to_mc", "[DC] {player}: {message}"); }
    public String getWebhookAvatar() { return messages.getString("chat.webhook_avatar", "https://minotar.net/avatar/{player}/128"); }

    public String getVerifyCodeMessage() { return messages.getString("verify.code_message", ""); }
    public String getVerifySuccessMc() { return messages.getString("verify.success_mc", ""); }
    public String getVerifySuccessDiscord() { return messages.getString("verify.success_discord", ""); }
    public String getVerifyAlreadyLinked() { return messages.getString("verify.already_linked", ""); }
    public String getVerifyInvalidCode() { return messages.getString("verify.invalid_code", ""); }

    public Map<String, Object> getVerifyWelcomeEmbed() { return getEmbedFromMessages("verify.welcome"); }

    public String getBirthdaySuccess() { return messages.getString("birthday.success", ""); }
    public String getBirthdayAlreadySet() { return messages.getString("birthday.already_set", ""); }
    public String getBirthdayToday() { return messages.getString("birthday.today", ""); }
    public Map<String, Object> getBirthdaySetupEmbed() { return getEmbedFromMessages("birthday.setup"); }

    public Map<String, Object> getBossSpawnEmbed() { return getEmbedFromMessages("events.boss_spawn"); }
    public Map<String, Object> getBossDeathEmbed() { return getEmbedFromMessages("events.boss_death"); }
    public Map<String, Object> getBirthdayEmbed() { return getEmbedFromMessages("birthday.notification"); }

    public Map<String, Object> getProfileEmbed() { return getEmbedFromMessages("profile"); }
    public Map<String, Object> getInventoryEmbed() { return getEmbedFromMessages("inventory"); }
    public Map<String, Object> getWhereamiEmbed() { return getEmbedFromMessages("whereami"); }

    public String getConsolePermissionDenied() { return messages.getString("console.permission_denied", ""); }
    public String getConsoleCommandBlacklisted() { return messages.getString("console.command_blacklisted", ""); }

    public String getEventsStarted() { return messages.getString("events.started", ""); }
    public String getEventsStopped() { return messages.getString("events.stopped", ""); }
    public String getEventsNoPermission() { return messages.getString("events.no_permission", ""); }
    public String getEventsListTitle() { return messages.getString("events.list_title", ""); }
    public String getEventsNoEvents() { return messages.getString("events.no_events", ""); }

    // Language
    public String getLang(String key) {
        return language.getString(key, key);
    }

    private Map<String, Object> getEmbedFromMessages(String path) {
        Map<String, Object> embed = new HashMap<>();
        ConfigurationSection section = messages.getConfigurationSection(path);
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
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> fieldMap = (Map<String, Object>) obj;
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
