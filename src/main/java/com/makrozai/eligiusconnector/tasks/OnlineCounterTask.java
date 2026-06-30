package com.makrozai.eligiusconnector.tasks;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class OnlineCounterTask {

    private final EligiusConnector plugin;
    private BukkitTask task;

    public OnlineCounterTask(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            int online = Bukkit.getOnlinePlayers().size();
            String format = plugin.getConfigAdapter().getOnlineFormat();
            String newName = format.replace("{count}", String.valueOf(online));
            plugin.getDiscordManager().updateChannelName(
                    plugin.getConfigAdapter().getOnlineChannelId(), newName);
        }, 0L, plugin.getConfigAdapter().getOnlineCounterInterval() * 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
