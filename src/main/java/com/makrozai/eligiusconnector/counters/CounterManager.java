package com.makrozai.eligiusconnector.counters;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CounterManager {

    private final EligiusConnector plugin;
    private final StateManager stateManager;
    private final Map<String, PeriodicCounter> periodicCounters = new ConcurrentHashMap<>();
    private final Map<String, StateCounter> stateCounters = new ConcurrentHashMap<>();

    public CounterManager(EligiusConnector plugin) {
        this.plugin = plugin;
        this.stateManager = new StateManager(plugin);
    }

    public void loadAll(FileConfiguration countersConfig) {
        ConfigurationSection section = countersConfig.getConfigurationSection("counters");
        if (section == null) return;

        for (String id : section.getKeys(false)) {
            ConfigurationSection cs = section.getConfigurationSection(id);
            if (cs == null) continue;

            CounterConfig config = CounterConfig.fromSection(id, cs);
            if (!config.enabled()) continue;

            if (config.isPeriodic()) {
                periodicCounters.put(id, new PeriodicCounter(plugin, config));
            } else if (config.isState()) {
                stateCounters.put(id, new StateCounter(plugin, config, stateManager));
            }
        }
    }

    public void startAll() {
        periodicCounters.values().forEach(PeriodicCounter::start);
        stateCounters.values().forEach(StateCounter::start);
    }

    public void stopAll() {
        periodicCounters.values().forEach(PeriodicCounter::stop);
        stateCounters.values().forEach(StateCounter::stop);
        periodicCounters.clear();
        stateCounters.clear();
    }

    public void onServerStart() {
        stateManager.setState("server_online", "online");
        // ponytail: slight delay so state counter update finishes before status embed
        stateCounters.values().forEach(c -> Scheduler.runLaterAsync(plugin, c::update, 20L));
    }

    public void onServerStop() {
        stateManager.setState("server_online", "offline");
        // Issue #10: rename sincrono para que shutdownNow no lo cancele
        stateCounters.values().forEach(StateCounter::updateSync);
    }

    public void updateOnlineNow() {
        periodicCounters.values().stream()
                .filter(c -> "online_players".equals(c.getConfig().id()))
                .forEach(PeriodicCounter::update);
    }

    public void updateDiscordCounters() {
        periodicCounters.values().forEach(PeriodicCounter::update);
        stateCounters.values().forEach(StateCounter::update);
    }

    public void reload(FileConfiguration config) {
        stopAll();
        loadAll(config);
        startAll();
    }

}
