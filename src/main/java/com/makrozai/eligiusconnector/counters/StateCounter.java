package com.makrozai.eligiusconnector.counters;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;

public class StateCounter implements StateManager.StateListener {

    private final EligiusConnector plugin;
    private final CounterConfig config;
    private final StateManager stateManager;

    public StateCounter(EligiusConnector plugin, CounterConfig config, StateManager stateManager) {
        this.plugin = plugin;
        this.config = config;
        this.stateManager = stateManager;
    }

    public void start() {
        stateManager.subscribe(config.stateKey(), this);
        Scheduler.runLaterAsync(plugin, this::update, 60L);
    }

    public void stop() {
        stateManager.unsubscribe(config.stateKey(), this);
    }

    @Override
    public void onStateChanged(String key, String oldValue, String newValue) {
        update();
    }

    public void update() {
        if (!plugin.getDiscordManager().isConnected()) return;
        String currentState = stateManager.getState(config.stateKey());
        String channelName = config.states().getOrDefault(currentState, config.format());
        if (channelName.isEmpty()) return;
        plugin.getDiscordManager().updateChannelName(config.channelId(), channelName);
    }

    public CounterConfig getConfig() {
        return config;
    }
}
