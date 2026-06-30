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

        String folderName = plugin.getConfigAdapter().getEventsFolder();
        File eventsFolder = new File(plugin.getDataFolder(), folderName);
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
        String type = config.getString("type", "command_chain");
        String channel = config.getString("channel", "");

        List<String> startCommands = config.getStringList("start_commands");
        List<String> endCommands = config.getStringList("end_commands");

        List<EventCondition> endConditions = new ArrayList<>();
        List<?> conditionsList = config.getList("end_conditions");
        if (conditionsList != null) {
            for (Object obj : conditionsList) {
                if (obj instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) obj;
                    String condType = String.valueOf(map.getOrDefault("type", ""));
                    String entity = String.valueOf(map.getOrDefault("entity", ""));

                    int minutes = 0;
                    if (map.containsKey("timeout")) {
                        try { minutes = Integer.parseInt(String.valueOf(map.get("timeout"))); } catch (NumberFormatException ignored) {}
                    } else if (map.containsKey("minutes")) {
                        try { minutes = Integer.parseInt(String.valueOf(map.get("minutes"))); } catch (NumberFormatException ignored) {}
                    }

                    endConditions.add(new EventCondition(condType, entity, minutes));
                }
            }
        }

        EventRewards rewards = parseRewards(config.getConfigurationSection("rewards"));

        Map<String, Object> discordStart = parseDiscord(config.getConfigurationSection("discord.start"));
        Map<String, Object> discordEnd = parseDiscord(config.getConfigurationSection("discord.end"));

        Map<String, Object> spawnConfig = new HashMap<>();
        ConfigurationSection spawnSection = config.getConfigurationSection("spawn");
        if (spawnSection != null) {
            spawnConfig.put("mob", spawnSection.getString("mob", ""));
            spawnConfig.put("custom_name", spawnSection.getString("custom_name", ""));
            spawnConfig.put("health", spawnSection.getInt("health", 20));
        }

        Map<String, Object> eventConfig = new HashMap<>();
        ConfigurationSection configSection = config.getConfigurationSection("config");
        if (configSection != null) {
            for (String key : configSection.getKeys(false)) {
                eventConfig.put(key, configSection.get(key));
            }
        }

        return new GameEvent(id, name, description, enabled, type, channel,
                startCommands, endCommands, endConditions, rewards,
                discordStart, discordEnd, spawnConfig, eventConfig);
    }

    private EventRewards parseRewards(ConfigurationSection section) {
        Map<String, EventRewards.RewardGroup> groups = new HashMap<>();
        if (section == null) return new EventRewards(groups);

        for (String key : section.getKeys(false)) {
            ConfigurationSection groupSection = section.getConfigurationSection(key);
            if (groupSection != null) {
                List<String> commands = groupSection.getStringList("commands");
                String message = groupSection.getString("message", "");
                groups.put(key, new EventRewards.RewardGroup(commands, message));
            }
        }

        return new EventRewards(groups);
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
            plugin.getDiscordManager().sendEmbed(event.getChannel(), event.getDiscordStart(), new HashMap<>());
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
            plugin.getDiscordManager().sendEmbed(event.getChannel(), event.getDiscordEnd(), new HashMap<>());
        }
    }

    public void reload() {
        activeEvents.clear();
        loadEvents();
    }
}
