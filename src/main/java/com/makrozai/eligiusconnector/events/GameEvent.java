package com.makrozai.eligiusconnector.events;

import java.util.List;
import java.util.Map;

public class GameEvent {

    private final String id;
    private final String name;
    private final String description;
    private final boolean enabled;
    private final String type;
    private final String channel;
    private final List<String> startCommands;
    private final List<String> endCommands;
    private final List<EventCondition> endConditions;
    private final EventRewards rewards;
    private final Map<String, Object> discordStart;
    private final Map<String, Object> discordEnd;
    private final Map<String, Object> spawnConfig;
    private final Map<String, Object> config;

    // Event state
    private boolean active = false;
    private long startTime = 0;

    public GameEvent(String id, String name, String description, boolean enabled, String type,
                     String channel, List<String> startCommands, List<String> endCommands,
                     List<EventCondition> endConditions, EventRewards rewards,
                     Map<String, Object> discordStart, Map<String, Object> discordEnd,
                     Map<String, Object> spawnConfig, Map<String, Object> config) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.enabled = enabled;
        this.type = type;
        this.channel = channel;
        this.startCommands = startCommands;
        this.endCommands = endCommands;
        this.endConditions = endConditions;
        this.rewards = rewards;
        this.discordStart = discordStart;
        this.discordEnd = discordEnd;
        this.spawnConfig = spawnConfig;
        this.config = config;
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public boolean isEnabled() { return enabled; }
    public String getType() { return type; }
    public String getChannel() { return channel; }
    public List<String> getStartCommands() { return startCommands; }
    public List<String> getEndCommands() { return endCommands; }
    public List<EventCondition> getEndConditions() { return endConditions; }
    public EventRewards getRewards() { return rewards; }
    public Map<String, Object> getDiscordStart() { return discordStart; }
    public Map<String, Object> getDiscordEnd() { return discordEnd; }
    public Map<String, Object> getSpawnConfig() { return spawnConfig; }
    public Map<String, Object> getConfig() { return config; }

    // State
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public long getStartTime() { return startTime; }
    public void setStartTime(long startTime) { this.startTime = startTime; }
}
