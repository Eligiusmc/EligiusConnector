package com.makrozai.eligiusconnector.tasks;

import com.makrozai.eligiusconnector.EligiusConnector;
import me.clip.placeholderapi.PlaceholderAPI;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.Map;

public class NicknameSyncTask {

    private final EligiusConnector plugin;
    private BukkitTask task;
    private final boolean placeholderApiAvailable;

    public NicknameSyncTask(EligiusConnector plugin) {
        this.plugin = plugin;
        this.placeholderApiAvailable = isPlaceholderApiAvailable();
    }

    public void start() {
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, this::syncAll, 20L,
                plugin.getConfigAdapter().getNicknameCycleSeconds() * 20L);
    }

    public void syncAll() {
        if (!plugin.getDiscordManager().isConnected()) return;

        Guild guild = plugin.getDiscordManager().getGuild();
        if (guild == null) return;

        String format = plugin.getConfigAdapter().getNicknameFormat();

        for (Player player : Bukkit.getOnlinePlayers()) {
            Long discordId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());
            if (discordId == null) continue;

            String nickname = resolveNickname(player, format);
            if (nickname == null || nickname.isEmpty()) continue;

            // Discord nickname limit is 32 characters
            if (nickname.length() > 32) {
                nickname = nickname.substring(0, 32);
            }

            final String finalNickname = nickname;
            guild.retrieveMemberById(discordId).queue(member -> {
                if (!member.getNickname().equals(finalNickname)) {
                    guild.modifyNickname(member, finalNickname).queue(
                            success -> {},
                            error -> plugin.getLogger().warning("Failed to update nickname for " + member.getUser().getName() + ": " + error.getMessage())
                    );
                }
            }, error -> {});
        }
    }

    public void syncPlayer(Player player) {
        if (!plugin.getDiscordManager().isConnected()) return;
        if (!plugin.getConfigAdapter().isNicknameSyncEnabled()) return;

        Long discordId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());
        if (discordId == null) return;

        Guild guild = plugin.getDiscordManager().getGuild();
        if (guild == null) return;

        String format = plugin.getConfigAdapter().getNicknameFormat();
        String nickname = resolveNickname(player, format);
        if (nickname == null || nickname.isEmpty()) return;

        if (nickname.length() > 32) {
            nickname = nickname.substring(0, 32);
        }

        final String finalNickname = nickname;
        guild.retrieveMemberById(discordId).queue(member -> {
            if (!member.getNickname().equals(finalNickname)) {
                guild.modifyNickname(member, finalNickname).queue(
                        success -> {},
                        error -> plugin.getLogger().warning("Failed to update nickname: " + error.getMessage())
                );
            }
        }, error -> {});
    }

    private String resolveNickname(Player player, String format) {
        String result = format;

        // Replace basic placeholders
        result = result.replace("%player_name%", player.getName());
        result = result.replace("%player_displayname%", player.getDisplayName());
        result = result.replace("%player_world%", player.getWorld().getName());

        // Use PlaceholderAPI if available
        if (placeholderApiAvailable) {
            try {
                result = PlaceholderAPI.setPlaceholders(player, result);
            } catch (Exception e) {
                // Fallback to basic placeholders
            }
        }

        return result;
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    private boolean isPlaceholderApiAvailable() {
        try {
            Class.forName("me.clip.placeholderapi.PlaceholderAPI");
            return Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
