package com.makrozai.eligiusconnector.counters;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.Bukkit;
import org.bukkit.Server;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class StateManager {

    private final EligiusConnector plugin;
    private final ConcurrentHashMap<String, String> states = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, CopyOnWriteArrayList<StateListener>> listeners = new ConcurrentHashMap<>();

    public StateManager(EligiusConnector plugin) {
        this.plugin = plugin;
        registerBuiltIns();
    }

    private void registerBuiltIns() {
        Server server = plugin.getServer();
        setState("server_online", "online");
        setState("whitelist", String.valueOf(server.hasWhitelist()));
        setState("maintenance", "false");
    }

    public void setState(String key, String value) {
        String old = states.put(key, value);
        if (!Objects.equals(old, value)) {
            notifyListeners(key, old, value);
        }
    }

    public String getState(String key) {
        return states.getOrDefault(key, "");
    }

    public void toggle(String key) {
        boolean current = Boolean.parseBoolean(getState(key));
        setState(key, String.valueOf(!current));
    }

    public void subscribe(String key, StateListener listener) {
        listeners.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(listener);
    }

    public void unsubscribe(String key, StateListener listener) {
        CopyOnWriteArrayList<StateListener> list = listeners.get(key);
        if (list != null) list.remove(listener);
    }

    private void notifyListeners(String key, String oldVal, String newVal) {
        CopyOnWriteArrayList<StateListener> list = listeners.get(key);
        if (list == null) return;
        for (StateListener listener : list) {
            try {
                listener.onStateChanged(key, oldVal, newVal);
            } catch (Exception e) {
                plugin.getLogger().warning("[Counters] State listener error for " + key + ": " + e.getMessage());
            }
        }
    }

    public void reset() {
        states.clear();
        listeners.clear();
        registerBuiltIns();
    }

    @FunctionalInterface
    public interface StateListener {
        void onStateChanged(String key, String oldValue, String newValue);
    }
}
