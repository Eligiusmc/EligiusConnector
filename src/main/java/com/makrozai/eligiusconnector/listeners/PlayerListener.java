package com.makrozai.eligiusconnector.listeners;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.chat.ChatFilterEngine;
import com.makrozai.eligiusconnector.util.Scheduler;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerListener implements Listener {

    private final EligiusConnector plugin;
    public PlayerListener(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        // Issue #13: filtrar antes de reenviar a Discord
        ChatFilterEngine.FilterResult result = plugin.getChatFilterEngine().filter(message, player);
        if (result.blocked()) {
            event.setCancelled(true);
            return;
        }

        if (!plugin.getConfigAdapter().isChatEnabled() || !plugin.getConfigAdapter().isMcToDiscordEnabled()) return;
        UUID uuid = player.getUniqueId();
        Scheduler.runForEntity(plugin, player, () -> {
            String playerName = player.getName();
            String fallbackAvatar = plugin.applyPlaceholders(player,
                    plugin.getConfigAdapter().getWebhookAvatar()
                            .replace("{player}", playerName)
                            .replace("{player_name}", playerName));
            Scheduler.runAsync(plugin, () -> {
                String avatarUrl = fallbackAvatar;
                Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);
                if (discordId != null && plugin.getDiscordManager().isConnected()) {
                    net.dv8tion.jda.api.entities.Guild guild = plugin.getDiscordManager().getGuild();
                    net.dv8tion.jda.api.entities.Member member = guild != null ? guild.getMemberById(discordId) : null;
                    if (member != null && member.getUser().getAvatarUrl() != null) {
                        avatarUrl = member.getUser().getAvatarUrl();
                    }
                }
                plugin.getDiscordManager().sendChatMessage(playerName, message, avatarUrl);
            });
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();
        int online = Bukkit.getOnlinePlayers().size();
        int max = Bukkit.getMaxPlayers();

        Scheduler.runAsync(plugin, () -> {
            boolean firstJoin = !plugin.getDatabaseManager().hasPlayedBefore(uuid);
            plugin.getStatsManager().updateJoin(uuid, playerName);
            if (!plugin.getConfigAdapter().isJoinEnabled()) return;

            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", playerName);
            replacements.put("uuid", uuid.toString());
            replacements.put("online", String.valueOf(online));
            replacements.put("max", String.valueOf(max));
            replacements.put("first_join", String.valueOf(firstJoin));

            Scheduler.runForEntity(plugin, player,
                    () -> plugin.getDiscordManager().sendJoinEmbed(replacements, player));

            // Update online counter
            if (plugin.getCounterManager() != null) {
                plugin.getCounterManager().updateOnlineNow();
            }

            // Log audit
            Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);
            if (discordId != null) {
                plugin.getDatabaseManager().logAudit("info", "player_join", playerName, "minecraft", "Discord: " + discordId, null);

                // Sync nickname on join
                if (plugin.getNicknameSyncTask() != null) {
                    plugin.getNicknameSyncTask().syncPlayer(player);
                }
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        String playerName = player.getName();
        int online = Math.max(0, Bukkit.getOnlinePlayers().size() - 1);
        int max = Bukkit.getMaxPlayers();

        if (plugin.getConfigAdapter().isLeaveEnabled()) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", playerName);
            replacements.put("online", String.valueOf(online));
            replacements.put("max", String.valueOf(max));
            plugin.getDiscordManager().sendLeaveEmbed(replacements, player);
        }

        if (plugin.getCounterManager() != null) plugin.getCounterManager().updateOnlineNow();

        Scheduler.runAsync(plugin, () -> {
            Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);
            if (discordId != null) {
                plugin.getDatabaseManager().logAudit("info", "player_quit", playerName, "minecraft", "Discord: " + discordId, null);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        if (plugin.getConfigAdapter().isDeathsEnabled()) {
            String deathMsg = event.getDeathMessage();
            if (deathMsg == null) deathMsg = player.getName() + " died";
            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", player.getName());
            replacements.put("death_message", deathMsg);
            replacements.put("health", String.valueOf((int) player.getHealth()));
            replacements.put("food", String.valueOf(player.getFoodLevel()));
            replacements.put("world", player.getWorld().getName());
            plugin.getDiscordManager().sendDeathEmbed(replacements, player);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAdvancement(org.bukkit.event.player.PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        String key = event.getAdvancement().getKey().getKey();

        // ponytail: filter recipe advancements (spigot-safe, getDisplay() is Paper-only)
        if (key.startsWith("recipes/")) return;
        String finalName = capitalizeWords(key.replace("/", " > ").replace("_", " "));

        if (plugin.getConfigAdapter().isAdvancementsEnabled()) {
            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", player.getName());
            replacements.put("advancement", finalName);
            Scheduler.runForEntity(plugin, player,
                    () -> plugin.getDiscordManager().sendAdvancementEmbed(replacements, player));
        }
    }

    private String capitalizeWords(String input) {
        StringBuilder result = new StringBuilder();
        boolean capitalizeNext = true;
        for (char c : input.toCharArray()) {
            if (c == ' ' || c == '>' || c == ':') {
                capitalizeNext = true;
                result.append(c);
            } else if (capitalizeNext) {
                result.append(Character.toUpperCase(c));
                capitalizeNext = false;
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }
}
