package com.makrozai.eligiusconnector.events;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EventManager {

    private final EligiusConnector plugin;
    private final Map<String, GameEvent> events = new ConcurrentHashMap<>();
    private final Map<String, GameEvent> activeEvents = new ConcurrentHashMap<>();

    public EventManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void loadEvents() {
        events.clear();

        File eventsFolder = new File(plugin.getDataFolder(), "events");
        if (!eventsFolder.exists()) {
            eventsFolder.mkdirs();
            // Copy default events from JAR
            copyDefaultEvents(eventsFolder);
        }

        File[] files = eventsFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return;

        for (File file : files) {
            try {
                YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                GameEvent event = parseEvent(config, file.getName());
                if (event != null && event.isEnabled()) {
                    events.put(event.getId(), event);
                    plugin.getLogger().info("Loaded event: " + event.getName());
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to load event: " + file.getName() + " - " + e.getMessage());
            }
        }

        plugin.getLogger().info("Loaded " + events.size() + " events");
    }

    private void copyDefaultEvents(File eventsFolder) {
        String[] defaultEvents = {"boss_wither.yml", "boss_enderdragon.yml", "pvp_tournament.yml", "treasure_hunt.yml"};
        for (String eventFile : defaultEvents) {
            plugin.saveResource("events/" + eventFile, false);
        }
    }

    private GameEvent parseEvent(YamlConfiguration config, String fileName) {
        String id = config.getString("id", fileName.replace(".yml", ""));
        String name = config.getString("name", id);
        String description = config.getString("description", "");
        boolean enabled = config.getBoolean("enabled", true);
        String channel = config.getString("channel", "");

        List<String> startCommands = config.getStringList("start_commands");
        List<String> endCommands = config.getStringList("end_commands");

        Map<String, Object> discordStart = parseDiscord(config.getConfigurationSection("discord.start"));
        Map<String, Object> discordEnd = parseDiscord(config.getConfigurationSection("discord.end"));

        return new GameEvent(id, name, description, enabled, channel, startCommands, endCommands,
                discordStart, discordEnd);
    }

    private Map<String, Object> parseDiscord(ConfigurationSection section) {
        Map<String, Object> embed = new HashMap<>();
        if (section == null) return embed;

        embed.put("title", section.getString("title", ""));
        embed.put("description", section.getString("description", ""));
        embed.put("color", section.getInt("color", 0x5865F2));
        embed.put("thumbnail", section.getString("thumbnail", ""));

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

    public GameEvent getEvent(String id) { return events.get(id); }
    public Collection<GameEvent> getAllEvents() { return events.values(); }
    public Map<String, GameEvent> getActiveEvents() { return activeEvents; }

    public void startEvent(String eventId) {
        GameEvent event = events.get(eventId);
        if (event == null || !event.isEnabled()) return;

        event.setActive(true);
        event.setStartTime(System.currentTimeMillis());
        activeEvents.put(eventId, event);

        // Execute start commands
        for (String cmd : event.getStartCommands()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
        }

        // Send Discord notification
        if (event.getChannel() != null && !event.getChannel().isEmpty()) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("name", event.getName());
            replacements.put("description", event.getDescription());
            plugin.getDiscordManager().sendEmbed(event.getChannel(), event.getDiscordStart(), replacements);
        }
    }

    public void stopEvent(String eventId) {
        GameEvent event = activeEvents.remove(eventId);
        if (event == null) return;

        event.setActive(false);

        // Execute end commands
        for (String cmd : event.getEndCommands()) {
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
        }

        // Send Discord notification
        if (event.getChannel() != null && !event.getChannel().isEmpty()) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("name", event.getName());
            replacements.put("description", event.getDescription());
            plugin.getDiscordManager().sendEmbed(event.getChannel(), event.getDiscordEnd(), replacements);
        }
    }

    public void reload() {
        activeEvents.clear();
        loadEvents();
    }
}
