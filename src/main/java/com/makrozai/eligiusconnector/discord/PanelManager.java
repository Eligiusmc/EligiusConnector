package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import net.dv8tion.jda.api.interactions.components.buttons.ButtonStyle;
import org.bukkit.configuration.ConfigurationSection;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PanelManager {

    private final EligiusConnector plugin;
    private static final List<String> PANEL_TYPES = List.of("verify", "birthday", "profile");

    public PanelManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void initializePanels() {
        if (!plugin.getDiscordManager().isConnected()) return;

        for (String panelType : PANEL_TYPES) {
            ConfigurationSection panelConfig = getPanelConfig(panelType);
            if (panelConfig == null || !panelConfig.getBoolean("enabled", true)) continue;

            String channelId = panelConfig.getString("channel_id", "");
            if (channelId.isEmpty()) {
                plugin.getLogger().warning("[Panels] channel_id not set for " + panelType);
                continue;
            }

            TextChannel channel = plugin.getDiscordManager().getChannel(channelId);
            if (channel == null) {
                plugin.getLogger().warning("[Panels] Channel not found: " + channelId);
                continue;
            }

            createPanel(panelType, channel, panelConfig);
        }
    }

    private void createPanel(String panelType, TextChannel channel, ConfigurationSection panelConfig) {
        plugin.getDiscordManager().clearChannel(channel.getId(), () -> {
            MessageEmbed embed = buildPanelEmbed(panelType);
            if (embed == null) return;

            List<Button> buttons = buildPanelButtons(panelConfig);

            channel.sendMessageEmbeds(embed).addActionRow(buttons).queue(
                    msg -> {
                        saveMessageId(panelType, msg.getId());
                        plugin.getLogger().info("[Panels] Created " + panelType + " panel (ID: " + msg.getId() + ")");
                    },
                    error -> plugin.getLogger().warning("[Panels] Failed to create " + panelType + " panel: " + error.getMessage())
            );
        });
    }

    public void deletePanel(String panelType) {
        String messageId = getConfigMessageId(panelType);
        if (messageId == null || messageId.isEmpty()) return;

        ConfigurationSection panelConfig = getPanelConfig(panelType);
        if (panelConfig == null) return;

        String channelId = panelConfig.getString("channel_id", "");
        if (channelId.isEmpty()) return;

        TextChannel channel = plugin.getDiscordManager().getChannel(channelId);
        if (channel == null) return;

        channel.deleteMessageById(messageId).queue(
                success -> saveMessageId(panelType, ""),
                error -> {}
        );
    }

    public void reloadPanels() {
        for (String panelType : PANEL_TYPES) {
            ConfigurationSection panelConfig = getPanelConfig(panelType);
            if (panelConfig == null || !panelConfig.getBoolean("enabled", true)) continue;

            String channelId = panelConfig.getString("channel_id", "");
            if (channelId.isEmpty()) continue;

            TextChannel channel = plugin.getDiscordManager().getChannel(channelId);
            if (channel == null) continue;

            createPanel(panelType, channel, panelConfig);
        }
    }

    private MessageEmbed buildPanelEmbed(String panelType) {
        EmbedBuilder embed = new EmbedBuilder();

        switch (panelType) {
            case "verify" -> {
                ConfigurationSection config = plugin.getConfigAdapter().getVerifyConfig();
                Map<String, Object> welcomeEmbed = config != null ? config.getConfigurationSection("welcome_embed").getValues(false) : null;

                embed.setTitle(plugin.msg("keys.discord.panel.verify.title"));
                embed.setDescription(plugin.msg("keys.discord.panel.verify.desc"));
                embed.setColor(getColor(welcomeEmbed, 0x5865F2));

                String thumbnail = welcomeEmbed != null ? String.valueOf(welcomeEmbed.getOrDefault("thumbnail", "")) : "";
                if (!thumbnail.isEmpty()) embed.setThumbnail(thumbnail);

                String author = welcomeEmbed != null ? String.valueOf(welcomeEmbed.getOrDefault("author", "")) : "";
                String authorIcon = welcomeEmbed != null ? String.valueOf(welcomeEmbed.getOrDefault("author_icon", "")) : "";
                if (!author.isEmpty()) embed.setAuthor(author, null, authorIcon.isEmpty() ? null : authorIcon);

                embed.setFooter(plugin.msg("keys.discord.panel.verify.footer"));

                embed.addField(
                        plugin.msg("keys.discord.panel.verify.field_howto_name"),
                        plugin.msg("keys.discord.panel.verify.field_howto_value"),
                        false
                );
                embed.addField(
                        plugin.msg("keys.discord.panel.verify.field_benefits_name"),
                        plugin.msg("keys.discord.panel.verify.field_benefits_value"),
                        false
                );
            }
            case "birthday" -> {
                embed.setTitle(plugin.msg("keys.discord.panel.birthday.title"));
                embed.setDescription(plugin.msg("keys.discord.panel.birthday.desc"));
                embed.setColor(new Color(0xFEE75C));

                embed.addField(
                        plugin.msg("keys.discord.panel.birthday.field_date_name"),
                        plugin.msg("keys.discord.panel.birthday.field_date_value"),
                        true
                );
                embed.addField(
                        plugin.msg("keys.discord.panel.birthday.field_reward_name"),
                        plugin.msg("keys.discord.panel.birthday.field_reward_value"),
                        true
                );
            }
            case "profile" -> {
                embed.setTitle(plugin.msg("keys.discord.panel.profile.title"));
                embed.setDescription(plugin.msg("keys.discord.panel.profile.desc"));
                embed.setColor(new Color(0x57F287));

                embed.addField(
                        plugin.msg("keys.discord.panel.profile.field_profile_name"),
                        plugin.msg("keys.discord.panel.profile.field_profile_value"),
                        true
                );
                embed.addField(
                        plugin.msg("keys.discord.panel.profile.field_inventory_name"),
                        plugin.msg("keys.discord.panel.profile.field_inventory_value"),
                        true
                );
                embed.addField(
                        plugin.msg("keys.discord.panel.profile.field_location_name"),
                        plugin.msg("keys.discord.panel.profile.field_location_value"),
                        true
                );
                embed.addField(
                        plugin.msg("keys.discord.panel.profile.field_money_name"),
                        plugin.msg("keys.discord.panel.profile.field_money_value"),
                        true
                );
            }
        }

        return embed.build();
    }

    private List<Button> buildPanelButtons(ConfigurationSection panelConfig) {
        List<Button> buttons = new ArrayList<>();
        List<?> buttonList = panelConfig.getList("buttons");
        if (buttonList == null) return buttons;

        for (Object obj : buttonList) {
            if (obj instanceof Map<?, ?> map) {
                String id = String.valueOf(map.get("id") != null ? map.get("id") : "");
                String label = String.valueOf(map.get("label") != null ? map.get("label") : "");
                String emoji = String.valueOf(map.get("emoji") != null ? map.get("emoji") : "");
                String styleStr = String.valueOf(map.get("style") != null ? map.get("style") : "SECONDARY").toUpperCase();

                if (id.isEmpty() || label.isEmpty()) continue;

                ButtonStyle style = switch (styleStr) {
                    case "PRIMARY" -> ButtonStyle.PRIMARY;
                    case "SUCCESS" -> ButtonStyle.SUCCESS;
                    case "DANGER" -> ButtonStyle.DANGER;
                    default -> ButtonStyle.SECONDARY;
                };

                Button button = Button.of(style, id, label);
                if (!emoji.isEmpty()) {
                    button = button.withEmoji(Emoji.fromFormatted(emoji));
                }
                buttons.add(button);
            }
        }

        return buttons;
    }

    private ConfigurationSection getPanelConfig(String panelType) {
        switch (panelType) {
            case "verify": return plugin.getConfigAdapter().getVerifyConfig().getConfigurationSection("panel");
            case "birthday": return plugin.getConfigAdapter().getBirthdayConfig().getConfigurationSection("panel");
            case "profile": return plugin.getConfigAdapter().getProfileConfig().getConfigurationSection("panel");
            default: return null;
        }
    }

    private String getConfigMessageId(String panelType) {
        return plugin.getConfigAdapter().getPanelMessageId(panelType);
    }

    public void saveMessageId(String panelType, String messageId) {
        plugin.getConfigAdapter().setPanelMessageId(panelType, messageId);
    }

    private Color getColor(Map<String, Object> config, int def) {
        if (config == null) return new Color(def);
        Object val = config.get("color");
        if (val instanceof Number num) return new Color(num.intValue());
        return new Color(def);
    }
}
