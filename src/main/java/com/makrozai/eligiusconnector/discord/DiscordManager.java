package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;

import java.util.EnumSet;
import java.util.List;
import java.util.logging.Level;

public class DiscordManager {

    private final EligiusConnector plugin;
    private JDA jda;
    private boolean connected = false;

    public DiscordManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        String token = plugin.getConfigManager().getBotToken();
        if (token == null || token.isEmpty()) {
            plugin.getLogger().severe("Bot token not configured! Discord features disabled.");
            return;
        }

        try {
            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(EnumSet.of(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.MESSAGE_CONTENT
                    ))
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setStatus(OnlineStatus.ONLINE);

            // Set activity
            List<String> statuses = plugin.getConfigManager().getConfig().getStringList("DiscordGameStatus");
            if (!statuses.isEmpty()) {
                builder.setActivity(Activity.playing(statuses.get(0)));
            }

            jda = builder.build();
            jda.awaitReady();
            connected = true;

            // Register listeners
            jda.addEventListener(new DiscordListener(plugin));

            plugin.getLogger().info("Discord bot connected successfully!");
            plugin.getLogger().info("Bot name: " + jda.getSelfUser().getName());
            plugin.getLogger().info("Guild: " + getGuild().getName());

        } catch (InterruptedException e) {
            plugin.getLogger().severe("Discord connection interrupted!");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to initialize Discord", e);
        }
    }

    public void shutdown() {
        if (jda != null) {
            jda.shutdown();
            try {
                if (!jda.awaitShutdown(5, java.util.concurrent.TimeUnit.SECONDS)) {
                    jda.shutdownNow();
                }
            } catch (InterruptedException e) {
                jda.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
        connected = false;
    }

    public JDA getJDA() {
        return jda;
    }

    public boolean isConnected() {
        return connected && jda != null;
    }

    public Guild getGuild() {
        if (!isConnected()) return null;
        String serverId = plugin.getConfigManager().getServerId();
        return jda.getGuildById(serverId);
    }

    public TextChannel getChannel(String channelId) {
        if (!isConnected()) return null;
        return jda.getTextChannelById(channelId);
    }

    public TextChannel getGlobalChannel() {
        return getChannel(plugin.getConfigManager().getGlobalChannelId());
    }

    public TextChannel getAdminChannel() {
        return getChannel(plugin.getConfigManager().getAdminChannelId());
    }

    public TextChannel getConsoleChannel() {
        return getChannel(plugin.getConfigManager().getConsoleChannelId());
    }

    public void sendMessage(String channelId, String message) {
        TextChannel channel = getChannel(channelId);
        if (channel != null) {
            channel.sendMessage(message).queue(
                    success -> {},
                    error -> plugin.getLogger().warning("Failed to send message: " + error.getMessage())
            );
        }
    }

    public void sendGlobalMessage(String message) {
        sendMessage(plugin.getConfigManager().getGlobalChannelId(), message);
    }

    public void sendAdminMessage(String message) {
        sendMessage(plugin.getConfigManager().getAdminChannelId(), message);
    }

    public void sendConsoleMessage(String message) {
        sendMessage(plugin.getConfigManager().getConsoleChannelId(), message);
    }
}
