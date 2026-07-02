package com.makrozai.eligiusconnector.listeners;

import com.makrozai.eligiusconnector.EligiusConnector;
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

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();

        Scheduler.runAsync(plugin, () -> {
            if (!plugin.getConfigAdapter().isChatEnabled()) return;

            String avatarUrl = getAvatarUrl(player);

            plugin.getDiscordManager().sendChatMessage(player.getName(), message, avatarUrl);
        });
    }

    private String getAvatarUrl(Player player) {
        Long discordId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());
        if (discordId != null && plugin.getDiscordManager().isConnected()) {
            net.dv8tion.jda.api.entities.Guild guild = plugin.getDiscordManager().getGuild();
            if (guild != null) {
                net.dv8tion.jda.api.entities.Member member = guild.getMemberById(discordId);
                if (member != null) {
                    String discordAvatar = member.getUser().getAvatarUrl();
                    if (discordAvatar != null && !discordAvatar.isEmpty()) {
                        return discordAvatar;
                    }
                }
            }
        }
        return plugin.applyPlaceholders(player,
                plugin.getConfigAdapter().getWebhookAvatar()
                        .replace("{player}", player.getName())
                        .replace("{player_name}", player.getName()));
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        Scheduler.runAsync(plugin, () -> {
            if (!plugin.getConfigAdapter().isJoinLeaveEnabled()) return;

            int online = Bukkit.getOnlinePlayers().size();
            int max = Bukkit.getMaxPlayers();
            boolean firstJoin = !plugin.getDatabaseManager().hasPlayedBefore(uuid);

            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", player.getName());
            replacements.put("uuid", uuid.toString());
            replacements.put("online", String.valueOf(online));
            replacements.put("max", String.valueOf(max));
            replacements.put("first_join", String.valueOf(firstJoin));

            plugin.getDiscordManager().sendJoinEmbed(replacements, player);

            // Update online counter
            if (plugin.getCounterManager() != null) {
                plugin.getCounterManager().updateOnlineNow();
            }

            // Log audit
            Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);
            if (discordId != null) {
                plugin.getDatabaseManager().logAudit("info", "player_join", player.getName(), "minecraft", "Discord: " + discordId, null);

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

        Scheduler.runAsync(plugin, () -> {
            if (!plugin.getConfigAdapter().isJoinLeaveEnabled()) return;

            int online = Bukkit.getOnlinePlayers().size() - 1;
            if (online < 0) online = 0;
            int max = Bukkit.getMaxPlayers();

            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", player.getName());
            replacements.put("online", String.valueOf(online));
            replacements.put("max", String.valueOf(max));

            plugin.getDiscordManager().sendLeaveEmbed(replacements, player);

            // Update online counter
            if (plugin.getCounterManager() != null) {
                plugin.getCounterManager().updateOnlineNow();
            }

            Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);
            if (discordId != null) {
                plugin.getDatabaseManager().logAudit("info", "player_quit", player.getName(), "minecraft", "Discord: " + discordId, null);
            }
        });
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();

        Scheduler.runAsync(plugin, () -> {
            if (!plugin.getConfigAdapter().isDeathsEnabled()) return;

            String deathMsg = event.getDeathMessage();
            if (deathMsg == null) deathMsg = player.getName() + " died";

            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", player.getName());
            replacements.put("death_message", deathMsg);
            replacements.put("health", String.valueOf((int) player.getHealth()));
            replacements.put("food", String.valueOf(player.getFoodLevel()));
            replacements.put("world", player.getWorld().getName());

            plugin.getDiscordManager().sendDeathEmbed(replacements, player);
        });
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAdvancement(org.bukkit.event.player.PlayerAdvancementDoneEvent event) {
        Player player = event.getPlayer();
        String key = event.getAdvancement().getKey().getKey();

        // ponytail: filter recipe advancements (spigot-safe, getDisplay() is Paper-only)
        if (key.startsWith("recipes/")) return;
        String finalName = capitalizeWords(key.replace("/", " > ").replace("_", " "));

        Scheduler.runAsync(plugin, () -> {
            if (!plugin.getConfigAdapter().isAdvancementsEnabled()) return;

            Map<String, String> replacements = new HashMap<>();
            replacements.put("player", player.getName());
            replacements.put("advancement", finalName);

            plugin.getDiscordManager().sendAdvancementEmbed(replacements, player);
        });
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
