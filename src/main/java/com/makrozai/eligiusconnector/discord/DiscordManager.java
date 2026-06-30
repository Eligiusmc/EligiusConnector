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

            // Delete all bot messages, then run callback
            java.util.concurrent.atomic.AtomicInteger remaining = new java.util.concurrent.atomic.AtomicInteger(botMessages.size());
            for (net.dv8tion.jda.api.entities.Message msg : botMessages) {
                msg.delete().queue(
                        success -> {
                            if (remaining.decrementAndGet() == 0 && onComplete != null) {
                                onComplete.run();
                            }
                        },
                        error -> {
                            if (remaining.decrementAndGet() == 0 && onComplete != null) {
                                onComplete.run();
                            }
                        }
                );
            }
        }, error -> {
            if (onComplete != null) onComplete.run();
        });
    }

    // Send embed messages
    public void sendEmbed(String channelId, Map<String, Object> embedConfig, Map<String, String> replacements) {
        TextChannel channel = getChannel(channelId);
        if (channel == null || embedConfig == null || embedConfig.isEmpty()) return;

        try {
            MessageEmbed embed = buildEmbed(embedConfig, replacements != null ? replacements : new java.util.HashMap<>());
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
    public MessageEmbed buildEmbed(Map<String, Object> embedConfig, Map<String, String> replacements) {
        if (embedConfig == null) return null;

        EmbedBuilder embed = new EmbedBuilder();

        String title = replacePlaceholders((String) embedConfig.get("title"), replacements);
        String description = replacePlaceholders((String) embedConfig.get("description"), replacements);

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

        String thumbnail = replacePlaceholders((String) embedConfig.get("thumbnail"), replacements);
        if (thumbnail != null && !thumbnail.isEmpty()) embed.setThumbnail(thumbnail);

        String image = replacePlaceholders((String) embedConfig.get("image"), replacements);
        if (image != null && !image.isEmpty()) embed.setImage(image);

        String author = replacePlaceholders((String) embedConfig.get("author"), replacements);
        String authorIcon = replacePlaceholders((String) embedConfig.get("authorIcon"), replacements);
        if (author != null && !author.isEmpty()) {
            if (authorIcon != null && !authorIcon.isEmpty()) {
                embed.setAuthor(author, null, authorIcon);
            } else {
                embed.setAuthor(author);
            }
        }

        String footer = replacePlaceholders((String) embedConfig.get("footer"), replacements);
        String footerIcon = replacePlaceholders((String) embedConfig.get("footerIcon"), replacements);
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
                String name = replacePlaceholders((String) field.get("name"), replacements);
                String value = replacePlaceholders((String) field.get("value"), replacements);
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
        TextChannel channel = getChannel(channelId);
        if (channel != null && !channel.getName().equals(newName)) {
            channel.getManager().setName(newName).queue(
                    success -> {},
                    error -> plugin.getLogger().warning("Failed to update channel name: " + error.getMessage())
            );
        }
    }

    // Convenience methods
    public void sendStatusEmbed(Map<String, String> replacements) {
        String channelId = plugin.getConfigAdapter().getStatusChannelId();
        clearChannel(channelId, () -> sendEmbed(channelId, plugin.getConfigAdapter().getStatusOnEmbed(), replacements));
    }

    public void sendStatusOffEmbed() {
        String channelId = plugin.getConfigAdapter().getStatusChannelId();
        clearChannel(channelId, () -> sendEmbed(channelId, plugin.getConfigAdapter().getStatusOffEmbed(), new java.util.HashMap<>()));
    }

    public void sendJoinEmbed(Map<String, String> replacements) {
        sendEmbed(plugin.getConfigAdapter().getJoinsChannelId(), plugin.getConfigAdapter().getJoinEmbed(), replacements);
    }

    public void sendLeaveEmbed(Map<String, String> replacements) {
        sendEmbed(plugin.getConfigAdapter().getJoinsChannelId(), plugin.getConfigAdapter().getLeaveEmbed(), replacements);
    }

    public void sendDeathEmbed(Map<String, String> replacements) {
        sendEmbed(plugin.getConfigAdapter().getDeathsChannelId(), plugin.getConfigAdapter().getDeathEmbed(), replacements);
    }

    public void sendAdvancementEmbed(Map<String, String> replacements) {
        sendEmbed(plugin.getConfigAdapter().getMissionsChannelId(), plugin.getConfigAdapter().getAdvancementEmbed(), replacements);
    }

    public void sendPermissionDenied(String channelId, String reason) {
        Map<String, Object> embedConfig = new java.util.HashMap<>();
        embedConfig.put("title", "❌ Permission Denied");
        embedConfig.put("description", reason);
        embedConfig.put("color", 0xED4245);
        sendEmbed(channelId, embedConfig, new java.util.HashMap<>());
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

    // Modify member nickname via JDA (owner must be skipped beforehand)
    public void modifyNickname(Guild guild, net.dv8tion.jda.api.entities.Member member, String nickname) {
        guild.modifyNickname(member, nickname).queue(
                success -> plugin.getLogger().info("[NickSync] " + member.getUser().getName() + " -> \"" + nickname + "\""),
                error -> plugin.getLogger().warning("[NickSync] Failed for " + member.getUser().getName() + ": " + error.getMessage())
        );
    }
}
