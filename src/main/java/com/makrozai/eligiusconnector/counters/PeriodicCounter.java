package com.makrozai.eligiusconnector.counters;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;
import org.bukkit.scheduler.BukkitTask;

public class PeriodicCounter {

    private final EligiusConnector plugin;
    private final CounterConfig config;
    private BukkitTask timerTask;
    private BukkitTask debounceTask;
    private long lastUpdateMs;

    public PeriodicCounter(EligiusConnector plugin, CounterConfig config) {
        this.plugin = plugin;
        this.config = config;
    }

    public void start() {
        long ticks = Math.max(config.intervalSeconds(), 1) * 20L;
        timerTask = Scheduler.runTimerAsync(plugin, this::doUpdate, 60L, ticks);
    }

    public void stop() {
        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        if (debounceTask != null) {
            debounceTask.cancel();
            debounceTask = null;
        }
    }

    // Debounced manual update — aggregates rapid join/leave events into 1 call
    public void update() {
        if (debounceTask != null) debounceTask.cancel();
        debounceTask = Scheduler.runLaterAsync(plugin, () -> {
            debounceTask = null;
            doUpdate();
        }, 40L);
    }

    private void doUpdate() {
        if (!plugin.getDiscordManager().isConnected()) return;
        // Cooldown: max 1 PATCH per 120s per counter (Discord allows 2 per 10min)
        long now = System.currentTimeMillis();
        if (now - lastUpdateMs < 120_000L) return;
        lastUpdateMs = now;

        String resolved = plugin.getPlaceholderResolver().resolve(config.format());
        plugin.getDiscordManager().updateChannelName(config.channelId(), resolved);
    }

    public CounterConfig getConfig() {
        return config;
    }
}
