package com.makrozai.eligiusconnector.tasks;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class ServerStatusCounterTask {

    private final EligiusConnector plugin;
    private BukkitTask task;

    public ServerStatusCounterTask(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void start() {
        updateStatus(true);
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!plugin.getDiscordManager().isConnected()) return;
            int online = Bukkit.getOnlinePlayers().size();
            String format = plugin.getConfigAdapter().getServerStatusFormatOnline();
            String newName = format.replace("{count}", String.valueOf(online));
            plugin.getDiscordManager().updateChannelName(
                    plugin.getConfigAdapter().getServerStatusChannel(), newName);
        }, 0L, plugin.getConfigAdapter().getServerStatusInterval() * 20L);
    }

    public void updateStatus(boolean online) {
        if (!plugin.getDiscordManager().isConnected()) return;
        String format = online
                ? plugin.getConfigAdapter().getServerStatusFormatOnline()
                : plugin.getConfigAdapter().getServerStatusFormatOffline();
        int count = online ? Bukkit.getOnlinePlayers().size() : 0;
        String newName = format.replace("{count}", String.valueOf(count));
        plugin.getDiscordManager().updateChannelName(
                plugin.getConfigAdapter().getServerStatusChannel(), newName);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
