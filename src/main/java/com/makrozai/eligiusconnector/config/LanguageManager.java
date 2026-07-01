package com.makrozai.eligiusconnector.config;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class LanguageManager {

    private static final List<String> AVAILABLE_LANGUAGES = List.of("es", "en", "fr", "de", "pt", "ru");
    private static final String DEFAULT_LANGUAGE = "es";

    private final EligiusConnector plugin;
    private final Map<String, FileConfiguration> languages = new ConcurrentHashMap<>();
    private final Map<UUID, String> playerLanguages = new ConcurrentHashMap<>();
    private String defaultLanguage;

    public LanguageManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.defaultLanguage = plugin.getConfigAdapter().getLanguageCode();
        loadAllLanguages();
        loadPlayerLanguagesFromDB();
    }

    public void loadAllLanguages() {
        languages.clear();
        for (String lang : AVAILABLE_LANGUAGES) {
            loadLanguageFile(lang);
        }
    }

    private void loadLanguageFile(String langCode) {
        File langFile = new File(plugin.getDataFolder(), "lang/" + langCode + ".yml");
        if (!langFile.exists()) {
            plugin.saveResource("lang/" + langCode + ".yml", false);
        }
        FileConfiguration langConfig = YamlConfiguration.loadConfiguration(langFile);
        try (InputStream defStream = plugin.getResource("lang/" + langCode + ".yml")) {
            if (defStream != null) {
                FileConfiguration defaults = YamlConfiguration.loadConfiguration(
                        new InputStreamReader(defStream, StandardCharsets.UTF_8));
                langConfig.setDefaults(defaults);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load language defaults for " + langCode + ": " + e.getMessage());
        }
        languages.put(langCode, langConfig);
    }

    private void loadPlayerLanguagesFromDB() {
        if (plugin.getDatabaseManager() == null) return;
        try (var conn = plugin.getDatabaseManager().getConnection();
             var stmt = conn.prepareStatement("SELECT minecraft_uuid, language FROM connector_player_languages");
             var rs = stmt.executeQuery()) {
            while (rs.next()) {
                UUID uuid = UUID.fromString(rs.getString("minecraft_uuid"));
                String lang = rs.getString("language");
                playerLanguages.put(uuid, lang);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load player languages from DB: " + e.getMessage());
        }
    }

    // ==========================================
    //  PUBLIC API
    // ==========================================

    public String get(String key) {
        return getMessage(defaultLanguage, key);
    }

    public String get(UUID playerUuid, String key) {
        String lang = getPlayerLanguage(playerUuid);
        return getMessage(lang, key);
    }

    public String get(UUID playerUuid, String key, Map<String, String> replacements) {
        String value = get(playerUuid, key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return value;
    }

    public String get(String key, Map<String, String> replacements) {
        String value = get(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return value;
    }

    public String getPlayerLanguage(UUID playerUuid) {
        String lang = playerLanguages.get(playerUuid);
        if (lang != null && languages.containsKey(lang)) return lang;
        return defaultLanguage;
    }

    public void setPlayerLanguage(UUID playerUuid, String langCode) {
        if (!AVAILABLE_LANGUAGES.contains(langCode)) return;
        playerLanguages.put(playerUuid, langCode);
        savePlayerLanguage(playerUuid, langCode);
    }

    public List<String> getAvailableLanguages() {
        return AVAILABLE_LANGUAGES;
    }

    public void reload() {
        this.defaultLanguage = plugin.getConfigAdapter().getLanguageCode();
        loadAllLanguages();
    }

    // ==========================================
    //  INTERNAL
    // ==========================================

    private String getMessage(String langCode, String key) {
        FileConfiguration langConfig = languages.get(langCode);
        if (langConfig == null) langConfig = languages.get(DEFAULT_LANGUAGE);
        if (langConfig == null) return key;

        String value = langConfig.getString(key, null);
        if (value == null) {
            FileConfiguration fallback = languages.get(DEFAULT_LANGUAGE);
            if (fallback != null) {
                value = fallback.getString(key, null);
            }
        }
        return value != null ? translateColorCodes(value) : key;
    }

    private void savePlayerLanguage(UUID playerUuid, String langCode) {
        if (plugin.getDatabaseManager() == null) return;
        String sql = "INSERT OR REPLACE INTO connector_player_languages (minecraft_uuid, language) VALUES (?, ?)";
        try (var conn = plugin.getDatabaseManager().getConnection();
             var stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerUuid.toString());
            stmt.setString(2, langCode);
            stmt.executeUpdate();
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to save player language: " + e.getMessage());
        }
    }

    private String translateColorCodes(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
