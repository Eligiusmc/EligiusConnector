package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DiscordListener extends ListenerAdapter {

    private final EligiusConnector plugin;
    private final Pattern MINECRAFT_NAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,16}$");

    public DiscordListener(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Ignore bot messages
        if (event.getAuthor().isBot()) return;

        // Ignore messages from console channel
        String consoleChannelId = plugin.getConfigManager().getConsoleChannelId();
        if (event.getChannel().getId().equals(consoleChannelId)) {
            handleConsoleMessage(event);
            return;
        }

        // Handle chat bridge
        String globalChannelId = plugin.getConfigManager().getGlobalChannelId();
        if (event.getChannel().getId().equals(globalChannelId)) {
            handleChatBridge(event);
            return;
        }
    }

    private void handleChatBridge(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        String discordName = event.getAuthor().getName();

        // Check if the user is linked
        UUID minecraftUuid = plugin.getDatabaseManager().getMinecraftUuid(event.getAuthor().getIdLong());
        String prefix = discordName;

        if (minecraftUuid != null) {
            // Get Minecraft name
            String minecraftName = plugin.getDatabaseManager().getMinecraftName(event.getAuthor().getIdLong());
            if (minecraftName != null) {
                prefix = minecraftName;
            }
        }

        // Format message
        String format = plugin.getConfigManager().getChatConfig().getString("DiscordToMinecraftFormat", "[DC] %username%: {message}");
        String formattedMessage = format
                .replace("%username%", discordName)
                .replace("%player_name%", prefix)
                .replace("{message}", message);

        // Send to Minecraft
        Bukkit.getScheduler().runTask(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(formattedMessage);
            }
        });
    }

    private void handleConsoleMessage(MessageReceivedEvent event) {
        String message = event.getMessage().getContentRaw();
        Member member = event.getMember();

        // Check permissions
        if (member == null || !member.hasPermission(net.dv8tion.jda.api.Permission.MANAGE_SERVER)) {
            event.getChannel().sendMessage("You don't have permission to execute console commands.").queue();
            return;
        }

        // Check blacklist
        String command = message.split(" ")[0].toLowerCase();
        java.util.List<String> blacklist = plugin.getConfigManager().getConfig().getStringList("ConsoleCommand.Blacklist");
        if (blacklist.contains(command)) {
            event.getChannel().sendMessage("This command is blacklisted: " + command).queue();
            return;
        }

        // Execute command
        Bukkit.getScheduler().runTask(plugin, () -> {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), message);
        });

        // Log to audit
        plugin.getDatabaseManager().logAudit(
                "info",
                "console_command",
                event.getAuthor().getName(),
                "discord",
                message,
                null
        );
    }

    public void sendMinecraftMessageToDiscord(String playerName, String message) {
        String globalChannelId = plugin.getConfigManager().getGlobalChannelId();
        TextChannel channel = plugin.getDiscordManager().getChannel(globalChannelId);

        if (channel != null) {
            // Format message
            String format = plugin.getConfigManager().getChatConfig().getString("MinecraftToDiscordFormat", "[MC] %player_displayname%: {message}");
            String formattedMessage = format
                    .replace("%player_displayname%", playerName)
                    .replace("%player_name%", playerName)
                    .replace("{message}", message);

            channel.sendMessage(formattedMessage).queue();
        }
    }
}
