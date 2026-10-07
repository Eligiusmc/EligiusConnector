package com.makrozai.eligiusconnector.tasks;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Objects;

public class NicknameSyncTask {

    private final EligiusConnector plugin;
    private BukkitTask task;

    public NicknameSyncTask(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void start() {
        task = Scheduler.runTimerAsync(plugin, this::syncAll, 20L,
                plugin.getConfigAdapter().getNicknameInterval() * 20L);
    }

    public void syncAll() {
        if (!plugin.getDiscordManager().isConnected()) return;
        Guild guild = plugin.getDiscordManager().getGuild();
        if (guild == null) return;
        String format = plugin.getConfigAdapter().getNicknameFormat();
        Scheduler.runSync(plugin, () -> Bukkit.getOnlinePlayers().forEach(player ->
                Scheduler.runForEntity(plugin, player, () -> syncPlayer(player, guild, format))));
    }

    public void syncPlayer(Player player) {
        if (!plugin.getDiscordManager().isConnected()) return;
        if (!plugin.getConfigAdapter().isNicknameSyncEnabled()) return;

        Scheduler.runForEntity(plugin, player, () -> {
            Guild guild = plugin.getDiscordManager().getGuild();
            if (guild == null) return;
            syncPlayer(player, guild, plugin.getConfigAdapter().getNicknameFormat());
        });
    }

    private void syncPlayer(Player player, Guild guild, String format) {
        String nickname = resolveNickname(player, format);
        if (nickname == null || nickname.isEmpty()) return;

        if (nickname.length() > 32) {
            nickname = nickname.substring(0, 32);
        }

        final String finalNickname = nickname;
        java.util.UUID uuid = player.getUniqueId();
        Scheduler.runAsync(plugin, () -> {
            Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);
            if (discordId == null) return;
            guild.retrieveMemberById(discordId).queue(member -> {
                if (member.isOwner()) return;

                String currentNick = member.getNickname();
                if (currentNick == null) currentNick = member.getEffectiveName();
                if (!Objects.equals(currentNick, finalNickname)) {
                    plugin.getDiscordManager().modifyNickname(guild, member, finalNickname);
                }
            }, error -> plugin.getLogger().warning("[NickSync] Member not found: " + discordId));
        });
    }

    private String resolveNickname(Player player, String format) {
        return plugin.getPlaceholderResolver().resolve(format, player);
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
