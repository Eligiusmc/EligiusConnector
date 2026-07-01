package com.makrozai.eligiusconnector.counters;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;
import org.bukkit.scheduler.BukkitTask;

public class PeriodicCounter {

    private final EligiusConnector plugin;
    private final CounterConfig config;
    private BukkitTask task;

    public PeriodicCounter(EligiusConnector plugin, CounterConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void start() {
        long ticks = Math.max(config.intervalSeconds(), 1) * 20L;
        task = Scheduler.runTimerAsync(plugin, this::update, 60L, ticks);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void update() {
        if (!plugin.getDiscordManager().isConnected()) return;
        String resolved = plugin.getPlaceholderResolver().resolve(config.format());
        plugin.getDiscordManager().updateChannelName(config.channelId(), resolved);
    }

    public CounterConfig getConfig() {
        return config;
    }
}
