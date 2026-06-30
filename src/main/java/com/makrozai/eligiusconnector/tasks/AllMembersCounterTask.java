package com.makrozai.eligiusconnector.tasks;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitTask;

public class AllMembersCounterTask {

    private final EligiusConnector plugin;
    private BukkitTask task;

    public AllMembersCounterTask(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            if (!plugin.getDiscordManager().isConnected()) return;
            var guild = plugin.getDiscordManager().getGuild();
            if (guild == null) return;
            int members = guild.getMemberCount();
            String format = plugin.getConfigAdapter().getAllMembersFormat();
            String newName = format.replace("{count}", String.valueOf(members));
            plugin.getDiscordManager().updateChannelName(
                    plugin.getConfigAdapter().getAllMembersChannelId(), newName);
        }, 0L, plugin.getConfigAdapter().getAllMembersCounterInterval() * 20L);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
