package com.makrozai.eligiusconnector.events;

import com.google.gson.*;
import com.makrozai.eligiusconnector.EligiusConnector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class EventStatePersistence {

    private final EligiusConnector plugin;
    private final File stateFile;
    private final JsonObject state;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    private static final String KEY_ACTIVE_EVENTS = "active_events";
    private static final String KEY_AUTO_SPAWN = "auto_spawn";

    public EventStatePersistence(EligiusConnector plugin) {
        this.plugin = plugin;
        this.stateFile = new File(plugin.getDataFolder(), "events/state.json");
        this.state = loadState();
    }

    public void saveActiveEvent(String eventId, Map<String, Object> data) {
        JsonObject events = state.getAsJsonObject(KEY_ACTIVE_EVENTS);
        if (events == null) {
            events = new JsonObject();
            state.add(KEY_ACTIVE_EVENTS, events);
        }
        events.add(eventId, gson.toJsonTree(data));
    }

    public void removeActiveEvent(String eventId) {
        JsonObject events = state.getAsJsonObject(KEY_ACTIVE_EVENTS);
        if (events != null) {
            events.remove(eventId);
        }
    }

    @SuppressWarnings("unchecked")
    public Map<String, Object> getActiveEvent(String eventId) {
        JsonObject events = state.getAsJsonObject(KEY_ACTIVE_EVENTS);
        if (events == null || !events.has(eventId)) {
            return null;
        }
        return gson.fromJson(events.get(eventId), Map.class);
    }

    @SuppressWarnings("unchecked")
    public Map<String, Map<String, Object>> getAllActiveEvents() {
        Map<String, Map<String, Object>> result = new LinkedHashMap<>();
        JsonObject events = state.getAsJsonObject(KEY_ACTIVE_EVENTS);
        if (events == null) {
            return result;
        }
        for (Map.Entry<String, JsonElement> entry : events.entrySet()) {
            Map<String, Object> data = gson.fromJson(entry.getValue(), Map.class);
            if (data != null) {
                result.put(entry.getKey(), data);
            }
        }
        return result;
    }

    public void saveLastAutoSpawn(String eventId, long timestamp) {
        JsonObject autoSpawn = state.getAsJsonObject(KEY_AUTO_SPAWN);
        if (autoSpawn == null) {
            autoSpawn = new JsonObject();
            state.add(KEY_AUTO_SPAWN, autoSpawn);
        }
        autoSpawn.addProperty(eventId, timestamp);
    }

    public long getLastAutoSpawn(String eventId) {
        JsonObject autoSpawn = state.getAsJsonObject(KEY_AUTO_SPAWN);
        if (autoSpawn == null || !autoSpawn.has(eventId)) {
            return 0L;
        }
        return autoSpawn.get(eventId).getAsLong();
    }

    public void save() {
        stateFile.getParentFile().mkdirs();
        try (Writer writer = new OutputStreamWriter(new FileOutputStream(stateFile), StandardCharsets.UTF_8)) {
            gson.toJson(state, writer);
        } catch (IOException e) {
            plugin.getLogger().warning("Failed to save event state: " + e.getMessage());
        }
    }

    public void load() {
        JsonObject loaded = loadState();
        state.entrySet().clear();
        state.entrySet().addAll(loaded.entrySet());
    }

    public void clear() {
        state.entrySet().clear();
    }

    private JsonObject loadState() {
        if (!stateFile.exists()) {
            return new JsonObject();
        }
        try (Reader reader = new InputStreamReader(new FileInputStream(stateFile), StandardCharsets.UTF_8)) {
            JsonElement element = JsonParser.parseReader(reader);
            if (element != null && element.isJsonObject()) {
                return element.getAsJsonObject();
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Failed to load event state: " + e.getMessage());
        }
        return new JsonObject();
    }
}
