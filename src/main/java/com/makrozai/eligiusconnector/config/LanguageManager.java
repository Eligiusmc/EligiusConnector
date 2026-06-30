package com.makrozai.eligiusconnector.config;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;

public class LanguageManager {

    private final EligiusConnector plugin;
    private FileConfiguration lang;

    public LanguageManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void load() {
        lang = plugin.getConfigAdapter().getLanguage();
    }

    public String get(String key) {
        return lang != null ? lang.getString(key, key) : key;
    }

    public String get(String key, Map<String, String> replacements) {
        String value = get(key);
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            value = value.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return value;
    }

    public String getCommand(String command, String subKey) {
        return get("commands." + command + "." + subKey);
    }

    public String getCommand(String command, String subKey, Map<String, String> replacements) {
        return get("commands." + command + "." + subKey, replacements);
    }
}
