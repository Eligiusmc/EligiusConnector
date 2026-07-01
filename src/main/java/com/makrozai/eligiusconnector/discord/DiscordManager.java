package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import com.makrozai.eligiusconnector.util.StartupLogger;
import org.bukkit.entity.Player;

import java.awt.Color;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DiscordManager {

    private final EligiusConnector plugin;
    private JDA jda;
    private boolean connected = false;

    public DiscordManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        String token = plugin.getConfigAdapter().getBotToken();
        if (token == null || token.isEmpty()) {
            StartupLogger.printError("Bot token not configured!");
            return;
        }

        StartupLogger.printStep("Connecting to Discord...");

        try {
            JDABuilder builder = JDABuilder.createDefault(token)
                    .enableIntents(EnumSet.of(
                            GatewayIntent.GUILD_MESSAGES,
                            GatewayIntent.GUILD_MEMBERS,
                            GatewayIntent.MESSAGE_CONTENT
                    ))
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .setStatus(OnlineStatus.ONLINE);

            List<String> statuses = plugin.getConfigAdapter().getGameStatus();
            if (statuses != null && !statuses.isEmpty()) {
                builder.setActivity(Activity.playing(statuses.get(0)));
            }

            jda = builder.build();
            jda.awaitReady();
            connected = true;

            jda.addEventListener(new DiscordListener(plugin));
            jda.addEventListener(new ButtonListener(plugin));

            StartupLogger.printStep("Discord bot connected:", jda.getSelfUser().getName());
            if (getGuild() != null) {
                StartupLogger.printStep("Guild:", getGuild().getName());
            }

        } catch (InterruptedException e) {
            StartupLogger.printError("Discord connection interrupted!");
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            StartupLogger.printError("Failed to initialize Discord: " + e.getMessage());
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

    public JDA getJDA() { return jda; }
    public boolean isConnected() { return connected && jda != null; }

    public Guild getGuild() {
        if (!isConnected()) return null;
        return jda.getGuildById(plugin.getConfigAdapter().getServerId());
    }

    public TextChannel getChannel(String channelId) {
        if (!isConnected() || channelId == null || channelId.isEmpty()) return null;
        return jda.getTextChannelById(channelId);
    }

    // Send text messages
    public void sendMessage(String channelId, String message) {
        TextChannel channel = getChannel(channelId);
        if (channel != null) {
            channel.sendMessage(message).queue(
                    success -> {},
                    error -> plugin.getLogger().warning("Failed to send message: " + error.getMessage())
            );
        }
    }

    // Send temporary message (auto-delete after delaySeconds)
    public void sendTempMessage(String channelId, String message, int delaySeconds) {
        TextChannel channel = getChannel(channelId);
        if (channel != null) {
            channel.sendMessage(message).queue(msg ->
                    msg.delete().queueAfter(delaySeconds, TimeUnit.SECONDS,
                            success -> {},
                            error -> plugin.getLogger().warning("Failed to delete temp message: " + error.getMessage())
                    ),
                    error -> plugin.getLogger().warning("Failed to send temp message: " + error.getMessage())
            );
        }
    }

    // Clear all bot messages in a channel
    public void clearChannel(String channelId) {
        clearChannel(channelId, null);
    }

    public void clearChannel(String channelId, Runnable onComplete) {
        if (!isConnected()) {
            if (onComplete != null) onComplete.run();
            return;
        }
        TextChannel channel = getChannel(channelId);
        if (channel == null) {
            if (onComplete != null) onComplete.run();
            return;
        }

        channel.getHistory().retrievePast(100).queue(messages -> {
            if (!isConnected()) {
                if (onComplete != null) onComplete.run();
                return;
            }
            List<net.dv8tion.jda.api.entities.Message> botMessages = messages.stream()
                    .filter(msg -> msg.getAuthor().equals(jda.getSelfUser()))
                    .toList();

            if (botMessages.isEmpty()) {
                if (onComplete != null) onComplete.run();
                return;
            }

            // ponytail: bulk delete (purgeMessages), then callback
            channel.purgeMessages(botMessages);
            if (onComplete != null) onComplete.run();
        }, error -> {
            if (onComplete != null) onComplete.run();
        });
    }

    // Send embed messages
    public void sendEmbed(String channelId, Map<String, Object> embedConfig, Map<String, String> replacements) {
        sendEmbed(channelId, embedConfig, replacements, null);
    }

    public void sendEmbed(String channelId, Map<String, Object> embedConfig, Map<String, String> replacements, Player player) {
        TextChannel channel = getChannel(channelId);
        if (channel == null || embedConfig == null || embedConfig.isEmpty()) return;

        try {
            MessageEmbed embed = buildEmbed(embedConfig, replacements != null ? replacements : new java.util.HashMap<>(), player);
            if (embed != null) {
                channel.sendMessageEmbeds(embed).queue(
                        success -> {},
                        error -> plugin.getLogger().warning("Failed to send embed: " + error.getMessage())
                );
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error building embed: " + e.getMessage());
        }
    }

    // Build MessageEmbed from config map
    public MessageEmbed buildEmbed(Map<String, Object> embedConfig, Map<String, String> replacements, Player player) {
        if (embedConfig == null) return null;

        EmbedBuilder embed = new EmbedBuilder();

        String title = applyPapi(replacePlaceholders((String) embedConfig.get("title"), replacements), player);
        String description = applyPapi(replacePlaceholders((String) embedConfig.get("description"), replacements), player);

        if (title != null && !title.isEmpty()) embed.setTitle(title);
        if (description != null && !description.isEmpty()) embed.setDescription(description);

        // Handle color
        Object colorObj = embedConfig.get("color");
        if (colorObj != null) {
            try {
                int colorValue;
                if (colorObj instanceof Integer) {
                    colorValue = (Integer) colorObj;
                } else if (colorObj instanceof Long) {
                    colorValue = ((Long) colorObj).intValue();
                } else if (colorObj instanceof String) {
                    String colorStr = ((String) colorObj).trim();
                    if (colorStr.startsWith("0x") || colorStr.startsWith("0X")) {
                        colorValue = Integer.parseInt(colorStr.substring(2), 16);
                    } else if (colorStr.startsWith("#")) {
                        colorValue = Integer.parseInt(colorStr.substring(1), 16);
                    } else {
                        colorValue = Integer.parseInt(colorStr);
                    }
                } else {
                    colorValue = 0x5865F2;
                }
                embed.setColor(new Color(colorValue));
            } catch (NumberFormatException e) {
                embed.setColor(new Color(0x5865F2));
            }
        }

        String thumbnail = applyPapi(replacePlaceholders((String) embedConfig.get("thumbnail"), replacements), player);
        if (thumbnail != null && !thumbnail.isEmpty()) embed.setThumbnail(thumbnail);

        String image = applyPapi(replacePlaceholders((String) embedConfig.get("image"), replacements), player);
        if (image != null && !image.isEmpty()) embed.setImage(image);

        String author = applyPapi(replacePlaceholders((String) embedConfig.get("author"), replacements), player);
        String authorIcon = applyPapi(replacePlaceholders((String) embedConfig.get("authorIcon"), replacements), player);
        if (author != null && !author.isEmpty()) {
            if (authorIcon != null && !authorIcon.isEmpty()) {
                embed.setAuthor(author, null, authorIcon);
            } else {
                embed.setAuthor(author);
            }
        }

        String footer = applyPapi(replacePlaceholders((String) embedConfig.get("footer"), replacements), player);
        String footerIcon = applyPapi(replacePlaceholders((String) embedConfig.get("footerIcon"), replacements), player);
        if (footer != null && !footer.isEmpty()) {
            if (footerIcon != null && !footerIcon.isEmpty()) {
                embed.setFooter(footer, footerIcon);
            } else {
                embed.setFooter(footer);
            }
        }

        // Timestamp
        Object timestamp = embedConfig.get("timestamp");
        if (Boolean.TRUE.equals(timestamp)) {
            embed.setTimestamp(java.time.Instant.now());
        }

        // Fields
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> fields = (List<Map<String, Object>>) embedConfig.get("fields");
        if (fields != null) {
            for (Map<String, Object> field : fields) {
                String name = applyPapi(replacePlaceholders((String) field.get("name"), replacements), player);
                String value = applyPapi(replacePlaceholders((String) field.get("value"), replacements), player);
                Boolean inline = (Boolean) field.get("inline");
                if (name != null && value != null && !name.isEmpty() && !value.isEmpty()) {
                    embed.addField(name, value, inline != null && inline);
                }
            }
        }

        try {
            return embed.build();
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid embed: " + e.getMessage());
            return null;
        }
    }

    // Update channel name
    public void updateChannelName(String channelId, String newName) {
        if (newName == null || newName.isEmpty()) return;
        TextChannel channel = getChannel(channelId);
        if (channel != null && !channel.getName().equals(newName)) {
            channel.getManager().setName(newName).queue(
                    success -> {},
                    error -> plugin.getLogger().warning("Failed to update channel name: " + error.getMessage())
            );
        }
    }

    // Convenience methods
    public void sendStatusEmbed(Map<String, String> replacements, org.bukkit.entity.Player player) {
        String channelId = plugin.getConfigAdapter().getStatusChannelId();
        clearChannel(channelId, () -> sendEmbed(channelId, plugin.getConfigAdapter().getStatusOnEmbed(), replacements, player));
    }

    public void sendStatusOffEmbedSync() {
        String channelId = plugin.getConfigAdapter().getStatusChannelId();
        TextChannel channel = getChannel(channelId);
        if (channel == null) {
            plugin.getLogger().warning("[Status] Channel not found: " + channelId);
            return;
        }
        try {
            // ponytail: capture old bot messages first, then send embed, then best-effort delete old
            // This way embed always publishes even if delete fails
            List<net.dv8tion.jda.api.entities.Message> messages = channel.getHistory().retrievePast(100).complete();
            List<net.dv8tion.jda.api.entities.Message> toDelete = messages.stream()
                    .filter(msg -> msg.getAuthor().equals(jda.getSelfUser()))
                    .toList();

            Map<String, Object> embedConfig = plugin.getConfigAdapter().getStatusOffEmbed();
            if (embedConfig != null && !embedConfig.isEmpty()) {
                MessageEmbed embed = buildEmbed(embedConfig, new java.util.HashMap<>(), null);
                if (embed != null) {
                    channel.sendMessageEmbeds(embed).complete();
                    plugin.getLogger().info("[Status] Offline embed sent successfully");
                }
            }

            // Best-effort: delete old bot messages, don't abort on failure
            if (!toDelete.isEmpty()) {
                channel.purgeMessages(toDelete);
            }
        } catch (Exception e) {
            plugin.getLogger().warning("Error sending offline status embed: " + e.getMessage());
        }
    }

    public void sendJoinEmbed(Map<String, String> replacements, org.bukkit.entity.Player player) {
        sendEmbed(plugin.getConfigAdapter().getJoinsChannelId(), plugin.getConfigAdapter().getJoinEmbed(), replacements, player);
    }

    public void sendLeaveEmbed(Map<String, String> replacements, org.bukkit.entity.Player player) {
        sendEmbed(plugin.getConfigAdapter().getJoinsChannelId(), plugin.getConfigAdapter().getLeaveEmbed(), replacements, player);
    }

    public void sendDeathEmbed(Map<String, String> replacements, org.bukkit.entity.Player player) {
        sendEmbed(plugin.getConfigAdapter().getDeathsChannelId(), plugin.getConfigAdapter().getDeathEmbed(), replacements, player);
    }

    public void sendAdvancementEmbed(Map<String, String> replacements, org.bukkit.entity.Player player) {
        sendEmbed(plugin.getConfigAdapter().getMissionsChannelId(), plugin.getConfigAdapter().getAdvancementEmbed(), replacements, player);
    }

    public void sendPermissionDenied(String channelId, String reason) {
        Map<String, Object> embedConfig = new java.util.HashMap<>();
        embedConfig.put("title", "❌ Permission Denied");
        embedConfig.put("description", reason);
        embedConfig.put("color", 0xED4245);
        sendEmbed(channelId, embedConfig, new java.util.HashMap<>(), null);
    }

    public void sendChatMessage(String playerName, String message, String avatarUrl) {
        String channelId = plugin.getConfigAdapter().getGlobalChannelId();
        if (channelId == null || channelId.isEmpty()) return;

        if (plugin.getConfigAdapter().isWebhookEnabled() && plugin.getWebhookManager() != null) {
            plugin.getWebhookManager().sendWebhookMessage(channelId, message, playerName, avatarUrl);
        } else {
            TextChannel channel = getChannel(channelId);
            if (channel != null) {
                channel.sendMessage("**" + playerName + "**: " + message).queue();
            }
        }
    }

    private String replacePlaceholders(String text, Map<String, String> replacements) {
        if (text == null || replacements == null) return text;
        for (Map.Entry<String, String> entry : replacements.entrySet()) {
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return text;
    }

    private String applyPapi(String text, org.bukkit.entity.Player player) {
        if (text == null || text.isEmpty()) return text;
        return plugin.applyPlaceholders(player, text);
    }

    // Modify member nickname via JDA (owner must be skipped beforehand)
    public void modifyNickname(Guild guild, net.dv8tion.jda.api.entities.Member member, String nickname) {
        guild.modifyNickname(member, nickname).queue(
                success -> plugin.getLogger().info("[NickSync] " + member.getUser().getName() + " -> \"" + nickname + "\""),
                error -> plugin.getLogger().warning("[NickSync] Failed for " + member.getUser().getName() + ": " + error.getMessage())
        );
    }
}
