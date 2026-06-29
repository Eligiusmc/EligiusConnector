package com.makrozai.eligiusconnector.listeners;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerListener implements Listener {

    private final EligiusConnector plugin;

    public PlayerListener(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Send to Discord asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String format = plugin.getConfigManager().getChatConfig().getString("MinecraftToDiscordFormat", "[MC] %player_displayname%: {message}");
            String formattedMessage = format
                    .replace("%player_displayname%", player.getDisplayName())
                    .replace("%player_name%", player.getName())
                    .replace("{message}", message);

            plugin.getDiscordManager().sendGlobalMessage(formattedMessage);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Check if player is linked
        Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);
        if (discordId != null) {
            // Log to audit
            plugin.getDatabaseManager().logAudit(
                    "info",
                    "player_join",
                    player.getName(),
                    "minecraft",
                    "Discord ID: " + discordId,
                    null
            );
        }

        // Send notification to Discord asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String joinMessage = plugin.getConfigManager().getNotificationsConfig().getString("Events.PlayerJoin.Message", ":arrow_right: **%player_displayname%** se ha conectado.");
            if (joinMessage != null && !joinMessage.isEmpty()) {
                joinMessage = joinMessage
                        .replace("%player_displayname%", player.getDisplayName())
                        .replace("%player_name%", player.getName())
                        .replace("%player_world%", player.getWorld().getName())
                        .replace("%playerlist_online%", String.valueOf(org.bukkit.Bukkit.getOnlinePlayers().size()));

                String channelId = plugin.getConfigManager().getNotificationsConfig().getString("Events.PlayerJoin.ChannelId", "");
                if (!channelId.isEmpty()) {
                    plugin.getDiscordManager().sendMessage(channelId, joinMessage);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        // Log to audit
        Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);
        if (discordId != null) {
            plugin.getDatabaseManager().logAudit(
                    "info",
                    "player_quit",
                    player.getName(),
                    "minecraft",
                    "Discord ID: " + discordId,
                    null
            );
        }

        // Send notification to Discord asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String quitMessage = plugin.getConfigManager().getNotificationsConfig().getString("Events.PlayerLeave.Message", ":arrow_left: **%player_displayname%** se ha desconectado.");
            if (quitMessage != null && !quitMessage.isEmpty()) {
                quitMessage = quitMessage
                        .replace("%player_displayname%", player.getDisplayName())
                        .replace("%player_name%", player.getName());

                String channelId = plugin.getConfigManager().getNotificationsConfig().getString("Events.PlayerLeave.ChannelId", "");
                if (!channelId.isEmpty()) {
                    plugin.getDiscordManager().sendMessage(channelId, quitMessage);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        // Send notification to Discord asynchronously
        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String discordMessage = plugin.getConfigManager().getNotificationsConfig().getString("Events.PlayerDeath.Message", ":skull: **%player_displayname%** ha muerto.");
            if (discordMessage != null && !discordMessage.isEmpty()) {
                discordMessage = discordMessage
                        .replace("%player_displayname%", player.getDisplayName())
                        .replace("%player_name%", player.getName())
                        .replace("%player_health%", String.valueOf((int) player.getHealth()))
                        .replace("%player_food%", String.valueOf(player.getFoodLevel()));

                String channelId = plugin.getConfigManager().getNotificationsConfig().getString("Events.PlayerDeath.ChannelId", "");
                if (!channelId.isEmpty()) {
                    plugin.getDiscordManager().sendMessage(channelId, discordMessage);
                }
            }
        });
    }
}
