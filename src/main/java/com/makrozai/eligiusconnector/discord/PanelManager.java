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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PanelManager {

    private final EligiusConnector plugin;

    private static final Map<String, ButtonStyle> DEFAULT_STYLES = new LinkedHashMap<>();

    static {
        DEFAULT_STYLES.put("verify", ButtonStyle.PRIMARY);
        DEFAULT_STYLES.put("profile", ButtonStyle.PRIMARY);
        DEFAULT_STYLES.put("inventory", ButtonStyle.SECONDARY);
        DEFAULT_STYLES.put("whereami", ButtonStyle.SECONDARY);
        DEFAULT_STYLES.put("money", ButtonStyle.SUCCESS);
    }

    public PanelManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void initializePanels() {
        if (!plugin.getDiscordManager().isConnected()) return;

        for (var entry : getPanelIds().entrySet()) {
            String panelType = entry.getKey();
            String channelId = entry.getValue();
            if (channelId.isEmpty()) continue;

            TextChannel channel = plugin.getDiscordManager().getChannel(channelId);
            if (channel == null) {
                plugin.getLogger().warning("[Panels] Channel not found for " + panelType + ": " + channelId);
                continue;
            }

            createPanel(panelType, channel);
        }
    }

    private Map<String, String> getPanelIds() {
        Map<String, String> panels = new LinkedHashMap<>();

        ConfigurationSection verifyCfg = plugin.getConfigAdapter().getVerifyConfig();
        if (hasButtons(verifyCfg)) {
            panels.put("verify", verifyCfg.getString("channel", ""));
        }

        ConfigurationSection profileCfg = plugin.getConfigAdapter().getProfileConfig();
        if (hasButtons(profileCfg)) {
            panels.put("profile", profileCfg.getString("channel", ""));
        }

        return panels;
    }

    private boolean hasButtons(ConfigurationSection cfg) {
        if (cfg == null) return false;
        ConfigurationSection panel = cfg.getConfigurationSection("panel");
        if (panel == null) return false;
        ConfigurationSection buttons = panel.getConfigurationSection("buttons");
        if (buttons == null) return false;
        for (String key : buttons.getKeys(false)) {
            if (buttons.getBoolean(key)) return true;
        }
        return false;
    }

    private void createPanel(String panelType, TextChannel channel) {
        plugin.getDiscordManager().clearChannel(channel.getId(), () -> {
            MessageEmbed embed = buildPanelEmbed(panelType);
            if (embed == null) return;

            List<Button> buttons = buildPanelButtons(panelType);

            channel.sendMessageEmbeds(embed).addActionRow(buttons).queue(
                    msg -> plugin.getLogger().info("[Panels] Created " + panelType + " panel"),
                    error -> plugin.getLogger().warning("[Panels] Failed to create " + panelType + " panel: " + error.getMessage())
            );
        });
    }

    public void deletePanel(String panelType) {
        String channelId = getChannelId(panelType);
        if (channelId.isEmpty()) return;

        TextChannel channel = plugin.getDiscordManager().getChannel(channelId);
        if (channel == null) return;

        plugin.getDiscordManager().clearChannel(channelId);
    }

    public void reloadPanels() {
        for (var entry : getPanelIds().entrySet()) {
            String channelId = entry.getValue();
            if (channelId.isEmpty()) continue;

            TextChannel channel = plugin.getDiscordManager().getChannel(channelId);
            if (channel == null) continue;

            createPanel(entry.getKey(), channel);
        }
    }

    private String getChannelId(String panelType) {
        ConfigurationSection cfg = switch (panelType) {
            case "verify" -> plugin.getConfigAdapter().getVerifyConfig();
            case "profile" -> plugin.getConfigAdapter().getProfileConfig();
            default -> null;
        };
        return cfg != null ? cfg.getString("channel", "") : "";
    }

    private ConfigurationSection getPanelSection(String panelType) {
        ConfigurationSection cfg = switch (panelType) {
            case "verify" -> plugin.getConfigAdapter().getVerifyConfig();
            case "profile" -> plugin.getConfigAdapter().getProfileConfig();
            default -> null;
        };
        return cfg != null ? cfg.getConfigurationSection("panel") : null;
    }

    private MessageEmbed buildPanelEmbed(String panelType) {
        ConfigurationSection panel = getPanelSection(panelType);
        if (panel == null) return null;

        ConfigurationSection embedCfg = panel.getConfigurationSection("embed");
        if (embedCfg == null) return null;

        EmbedBuilder embed = new EmbedBuilder();

        String title = embedCfg.getString("title", "");
        if (!title.isEmpty()) embed.setTitle(title);

        String description = embedCfg.getString("description", "");
        if (!description.isEmpty()) embed.setDescription(description);

        embed.setColor(parseColor(embedCfg.getString("color", ""), 0x5865F2));

        String thumbnail = embedCfg.getString("thumbnail", "");
        if (!thumbnail.isEmpty()) embed.setThumbnail(thumbnail);

        String image = embedCfg.getString("image", "");
        if (!image.isEmpty()) embed.setImage(image);

        String author = embedCfg.getString("author", "");
        String authorIcon = embedCfg.getString("author_icon", "");
        if (!author.isEmpty()) {
            embed.setAuthor(author, null, authorIcon.isEmpty() ? null : authorIcon);
        }

        String footer = embedCfg.getString("footer", "");
        String footerIcon = embedCfg.getString("footer_icon", "");
        if (!footer.isEmpty()) {
            embed.setFooter(footer, footerIcon.isEmpty() ? null : footerIcon);
        }

        if (embedCfg.getBoolean("timestamp", false)) {
            embed.setTimestamp(java.time.Instant.now());
        }

        List<?> fieldsList = embedCfg.getList("fields");
        if (fieldsList != null) {
            for (Object obj : fieldsList) {
                if (obj instanceof Map<?, ?> map) {
                    Object nameObj = map.get("name");
                    Object valueObj = map.get("value");
                    String name = nameObj != null ? String.valueOf(nameObj) : "";
                    String value = valueObj != null ? String.valueOf(valueObj) : "";
                    boolean inline = Boolean.TRUE.equals(map.get("inline"));
                    if (!name.isEmpty() && !value.isEmpty()) {
                        embed.addField(name, value, inline);
                    }
                }
            }
        }

        try {
            return embed.build();
        } catch (Exception e) {
            plugin.getLogger().warning("[Panels] Failed to build embed for " + panelType + ": " + e.getMessage());
            return null;
        }
    }

    private List<Button> buildPanelButtons(String panelType) {
        List<Button> buttons = new ArrayList<>();
        ConfigurationSection panel = getPanelSection(panelType);
        if (panel == null) return buttons;

        ConfigurationSection buttonCfg = panel.getConfigurationSection("buttons");
        if (buttonCfg == null) return buttons;

        for (String buttonId : buttonCfg.getKeys(false)) {
            if (!buttonCfg.getBoolean(buttonId)) continue;

            String label = plugin.msg("keys.discord.panel." + panelType + ".buttons." + buttonId + ".label");
            String emojiStr = plugin.msg("keys.discord.panel." + panelType + ".buttons." + buttonId + ".emoji");
            ButtonStyle style = DEFAULT_STYLES.getOrDefault(buttonId, ButtonStyle.SECONDARY);

            Button button = Button.of(style, buttonId, label);
            if (!emojiStr.isEmpty()) {
                button = button.withEmoji(Emoji.fromFormatted(emojiStr));
            }
            buttons.add(button);
        }

        return buttons;
    }

    private Color parseColor(String colorStr, int defaultColor) {
        if (colorStr == null || colorStr.isEmpty()) return new Color(defaultColor);
        try {
            if (colorStr.startsWith("0x") || colorStr.startsWith("0X")) {
                return new Color(Integer.parseInt(colorStr.substring(2), 16));
            } else if (colorStr.startsWith("#")) {
                return new Color(Integer.parseInt(colorStr.substring(1), 16));
            } else {
                return new Color(Integer.parseInt(colorStr));
            }
        } catch (NumberFormatException e) {
            return new Color(defaultColor);
        }
    }
}
