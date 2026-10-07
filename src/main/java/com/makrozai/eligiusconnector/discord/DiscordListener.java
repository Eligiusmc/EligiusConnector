package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageEmbed;
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

        String channelId = plugin.getConfigAdapter().getChatChannelId();
        if (event.getChannel().getId().equals(channelId)) {
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
            sendErrorToChannel(event.getChannel().asTextChannel(), "no_permission");
            return;
        }

        if (message == null || message.isBlank()) return;
        java.util.List<String> blacklist = plugin.getConfigAdapter().getConsoleCommandBlacklist();
        if (isBlacklisted(message, blacklist)) {
            sendErrorToChannel(event.getChannel().asTextChannel(), "command_blacklisted");
            return;
        }

        final String toRun = message.trim().replaceAll("\\s+", " ");
        Bukkit.getScheduler().runTask(plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), toRun));
        plugin.getDatabaseManager().logAudit("info", "console_command", event.getAuthor().getName(), "discord", message, null);
    }

    /**
     * Resuelve el nombre efectivo del comando (strip de {@code /}, espacios extra y
     * namespace {@code plugin:}) y lo compara contra la blacklist ignorando mayúsculas.
     * {@link org.bukkit.Bukkit#dispatchCommand} normaliza igual, así que la validación
     * debe mirar exactamente lo que el dispatcher va a ejecutar.
     */
    static boolean isBlacklisted(String message, java.util.List<String> blacklist) {
        if (message == null || blacklist == null || blacklist.isEmpty()) return false;
        String token = message.trim().replaceAll("\\s+", " ").split(" ")[0].toLowerCase(java.util.Locale.ROOT);
        if (token.startsWith("/")) token = token.substring(1);
        String bare = token.contains(":") ? token.substring(token.indexOf(':') + 1) : token;
        for (String entry : blacklist) {
            if (entry == null) continue;
            String e = entry.trim().toLowerCase(java.util.Locale.ROOT);
            if (e.equals(token) || e.equals(bare)) return true;
        }
        return false;
    }

    private void sendErrorToChannel(TextChannel channel, String errorKey) {
        MessageEmbed embed = new EmbedBuilder()
                .setTitle(plugin.msg("error." + errorKey + ".title"))
                .setDescription(plugin.msg("error." + errorKey + ".description"))
                .setColor(errorKey.equals("no_permission") || errorKey.equals("command_blacklisted") ? 0xED4245 : 0xFEE75C)
                .setTimestamp(java.time.Instant.now())
                .build();
        channel.sendMessageEmbeds(embed).queue();
    }
}
