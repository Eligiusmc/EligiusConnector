package com.makrozai.eligiusconnector.counters;

import org.bukkit.configuration.ConfigurationSection;

import java.util.HashMap;
import java.util.Map;

public record CounterConfig(
        String id,
        String type,
        String channelId,
        String format,
        int intervalSeconds,
        String stateKey,
        Map<String, String> states,
        boolean enabled
) {

    public static CounterConfig fromSection(String id, ConfigurationSection section) {
        String type = section.getString("type", "periodic");
        String channelId = section.getString("channel", "");
        String format = section.getString("format", "");
        int interval = section.getInt("interval", section.getInt("update_interval_seconds", 30));
        String stateKey = section.getString("state_key");
        boolean enabled = section.getBoolean("enabled", true);

        Map<String, String> states = new HashMap<>();
        ConfigurationSection statesSection = section.getConfigurationSection("states");
        if (statesSection != null) {
            for (String key : statesSection.getKeys(false)) {
                states.put(key, statesSection.getString(key, ""));
            }
        }

        return new CounterConfig(id, type, channelId, format, interval, stateKey, states, enabled);
    }

    public boolean isPeriodic() {
        return "periodic".equalsIgnoreCase(type);
    }

    public boolean isState() {
        return "state".equalsIgnoreCase(type);
    }
}
