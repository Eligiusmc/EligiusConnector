package com.makrozai.eligiusconnector.chat;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ChatBridgeService {

    private final EligiusConnector plugin;
    private final ChatFilterEngine filterEngine;
    private final Map<UUID, String> displayNameCache = new ConcurrentHashMap<>();

    public ChatBridgeService(EligiusConnector plugin) {
        this.plugin = plugin;
        this.filterEngine = new ChatFilterEngine(plugin);
    }

    public void processMCToDiscord(Player player, String message) {
        if (!plugin.getConfigAdapter().isMcToDiscordEnabled()) return;

        ChatFilterEngine.FilterResult result = filterEngine.filter(message, player);

        if (result.blocked()) {
            player.sendMessage("§c§lChat§7: §eTu mensaje ha sido bloqueado. §7(" + result.reason() + ")");
            return;
        }

        String format = plugin.getConfigAdapter().getChatMcToDiscord();
        String formatted = format
                .replace("{player}", player.getName())
                .replace("{message}", message);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            String avatarUrl = plugin.getConfigAdapter().getWebhookAvatar()
                    .replace("{player}", player.getName())
                    .replace("{player_name}", player.getName());

            if (plugin.getConfigAdapter().isUseWebhooks()) {
                String channelId = plugin.getConfigAdapter().getGlobalChannelId();
                String webhookName = plugin.getConfigAdapter().getWebhookName();
                plugin.getWebhookManager().sendWebhookMessage(channelId, formatted, webhookName, avatarUrl);
            } else {
                plugin.getDiscordManager().sendChatMessage(player.getName(), message, avatarUrl);
            }
        });
    }

    public void processDiscordToMC(String discordName, String message, UUID mcUuid) {
        if (!plugin.getConfigAdapter().isDiscordToMcEnabled()) return;

        String displayName = resolveDisplayName(discordName, mcUuid);
        String formatted = "§8[§9DC§8] §6" + displayName + "§7: §f" + message;

        broadcast(formatted);
    }

    public void broadcast(String message) {
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(message);
            }
        });
    }

    public ChatFilterEngine getFilterEngine() {
        return filterEngine;
    }

    private String resolveDisplayName(String discordName, UUID mcUuid) {
        if (mcUuid != null) {
            Player player = Bukkit.getPlayer(mcUuid);
            if (player != null) {
                displayNameCache.put(mcUuid, player.getName());
                return player.getName();
            }

            String cached = displayNameCache.get(mcUuid);
            if (cached != null) return cached;

            Long discordId = plugin.getDatabaseManager().getDiscordId(mcUuid);
            if (discordId != null) {
                String name = plugin.getDatabaseManager().getMinecraftName(discordId);
                if (name != null) {
                    displayNameCache.put(mcUuid, name);
                    return name;
                }
            }
        }
        return discordName;
    }
}
