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
import java.util.concurrent.ConcurrentHashMap;

public class LanguageManager {

    private static final List<String> AVAILABLE_LANGUAGES = List.of("es", "en", "fr", "de", "pt", "ru");
    private static final String DEFAULT_LANGUAGE = "en";

    private final EligiusConnector plugin;
    private final Map<String, FileConfiguration> languages = new ConcurrentHashMap<>();
    private String defaultLanguage;

    public LanguageManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void load() {
        this.defaultLanguage = plugin.getConfigAdapter().getLanguageCode();
        loadAllLanguages();
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

    // ==========================================
    //  PUBLIC API
    // ==========================================

    public String get(String key) {
        return getMessage(defaultLanguage, key);
    }

    public String get(String key, Map<String, String> replacements) {
        String value = get(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return value;
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

    private String translateColorCodes(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }
}
