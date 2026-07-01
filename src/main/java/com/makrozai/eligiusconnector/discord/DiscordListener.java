package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

public class DiscordListener extends ListenerAdapter {

    private final EligiusConnector plugin;

    public DiscordListener(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String consoleChannelId = plugin.getConfigAdapter().getConsoleChannelId();
        if (event.getChannel().getId().equals(consoleChannelId)) {
            handleConsoleMessage(event);
            return;
        }

        String globalChannelId = plugin.getConfigAdapter().getGlobalChannelId();
        if (event.getChannel().getId().equals(globalChannelId)) {
            handleChatBridge(event);
        }
    }

    private void handleChatBridge(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String discordName = event.getAuthor().getName();

        UUID minecraftUuid = plugin.getDatabaseManager().getMinecraftUuid(event.getAuthor().getIdLong());
        String prefix = discordName;

        if (minecraftUuid != null) {
            String minecraftName = plugin.getDatabaseManager().getMinecraftName(event.getAuthor().getIdLong());
            if (minecraftName != null) prefix = minecraftName;
        }

        String format = plugin.getConfigAdapter().getChatFormatDiscordToMC();
        String formattedMessage = format.replace("{player}", prefix).replace("{message}", message);

        final String finalMessage = formattedMessage;
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(finalMessage);
            }
        });
    }

    private void handleConsoleMessage(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        Member member = event.getMember();

        if (member == null || !member.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_SERVER)) {
            plugin.getDiscordManager().sendPermissionDenied(
                    plugin.getConfigAdapter().getConsoleChannelId(),
                    "You don't have permission to execute console commands."
            );
            return;
        }

        if (message == null || message.isBlank()) return;
        String command = message.split(" ")[0].toLowerCase();
        java.util.List<String> blacklist = plugin.getConfigAdapter().getConsoleBlacklist();
        if (blacklist.contains(command)) {
            plugin.getDiscordManager().sendPermissionDenied(
                    plugin.getConfigAdapter().getConsoleChannelId(),
                    "This command is blacklisted: `" + command + "`"
            );
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message));
        plugin.getDatabaseManager().logAudit("info", "console_command", event.getAuthor().getName(), "discord", message, null);
    }
}
