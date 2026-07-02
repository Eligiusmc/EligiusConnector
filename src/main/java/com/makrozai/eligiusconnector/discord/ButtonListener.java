package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.stats.PlayerStatsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class ButtonListener extends ListenerAdapter {

    private final EligiusConnector plugin;

    public ButtonListener(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onButtonInteraction(ButtonInteractionEvent event) {
        String[] parts = event.getComponentId().split(":");
        String action = parts[0];

        switch (action) {
            case "verify" -> handleVerify(event);
            case "whereami" -> handleWhereAmI(event);
            case "inventory" -> handleInventory(event);
            case "money" -> handleMoney(event);
            case "profile" -> handleProfile(event);
            default -> plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "unknown_action");
        }
    }

    private void handleVerify(ButtonInteractionEvent event) {
        event.deferReply(true).queue();
        CompletableFuture.runAsync(() -> {
            try {
                long discordId = Long.parseLong(event.getUser().getId());
                UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

                if (uuid != null) {
                    plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "already_linked");
                    return;
                }

                String code = plugin.generateVerifyCode(discordId);
                Map<String, Object> embedConfig = plugin.getConfigAdapter().getVerifyWelcomeEmbed();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(getStringOrDefault(embedConfig, "title", "🔐 Verifica tu Cuenta"));
                embed.setDescription(getStringOrDefault(embedConfig, "description", "Vincula tu cuenta de Minecraft"));
                embed.setColor(getColorOrDefault(embedConfig, 0x5865F2));

                String thumbnail = getStringOrDefault(embedConfig, "thumbnail", "");
                if (!thumbnail.isEmpty()) {
                    embed.setThumbnail(plugin.applyPlaceholders(null, thumbnail.replace("{player}", event.getUser().getName())));
                }

                String author = getStringOrDefault(embedConfig, "author", "");
                if (!author.isEmpty()) {
                    String authorIcon = getStringOrDefault(embedConfig, "author_icon", "");
                    embed.setAuthor(author, null, authorIcon.isEmpty() ? null : authorIcon);
                }

                embed.addField(plugin.msg("keys.discord.verify.code_title"), "`" + code + "`", false);
                embed.addBlankField(false);
                embed.addField(plugin.msg("keys.discord.verify.steps_label"), plugin.msg("keys.discord.verify.code_steps").replace("{code}", code), false);

                String footer = getStringOrDefault(embedConfig, "footer", "");
                if (!footer.isEmpty()) {
                    embed.setFooter(footer);
                }

                if (Boolean.TRUE.equals(embedConfig.get("timestamp"))) {
                    embed.setTimestamp(java.time.Instant.now());
                }

                event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
            } catch (Exception e) {
                plugin.getLogger().warning("Verify button error: " + e.getMessage());
                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
            }
        });
    }

    private void handleWhereAmI(ButtonInteractionEvent event) {
        event.deferReply(true).queue();
        long discordId = Long.parseLong(event.getUser().getId());
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

        if (uuid == null) {
            plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_linked");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) {
                    plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_online");
                    return;
                }

                ConfigurationSection cfg = plugin.getConfigAdapter().getProfileConfig()
                        .getConfigurationSection("whereami");
                Map<String, String> data = computePlayerData(player, null);
                data.put("player_biome", player.getLocation().getBlock().getBiome().name()
                        .toLowerCase().replace("_", " "));
                data.put("player_block", getBlockFaceName(player.getLocation()));
                data.put("whereami_tp_command", cfg.getBoolean("show_tp_command", true)
                        ? "/tp @s " + player.getLocation().getBlockX() + " "
                          + player.getLocation().getBlockY() + " "
                          + player.getLocation().getBlockZ()
                        : "");

                MessageEmbed embed = buildEmbedFromTemplate(player,
                        cfg.getConfigurationSection("embed"), data,
                        new Color(0x2ECC71));
                event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
            } catch (Exception e) {
                plugin.getLogger().warning("WhereAmI button error: " + e.getMessage());
                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
            }
        });
    }

    private void handleInventory(ButtonInteractionEvent event) {
        event.deferReply(true).queue();
        long discordId = Long.parseLong(event.getUser().getId());
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

        if (uuid == null) {
            plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_linked");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                Player player = Bukkit.getPlayer(uuid);
                if (player == null || !player.isOnline()) {
                    plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_online");
                    return;
                }

                ConfigurationSection cfg = plugin.getConfigAdapter().getProfileConfig()
                        .getConfigurationSection("inventory");
                Map<String, String> data = computeInventoryData(player, cfg);
                MessageEmbed embed = buildEmbedFromTemplate(player,
                        cfg.getConfigurationSection("embed"), data,
                        new Color(0xF39C12));
                event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
            } catch (Exception e) {
                plugin.getLogger().warning("Inventory button error: " + e.getMessage());
                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
            }
        });
    }

    private void handleProfile(ButtonInteractionEvent event) {
        event.deferReply(true).queue();
        long discordId = Long.parseLong(event.getUser().getId());
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

        if (uuid == null) {
            plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_linked");
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                ConfigurationSection cfg = plugin.getConfigAdapter().getProfileConfig()
                        .getConfigurationSection("profile");
                Player player = Bukkit.getPlayer(uuid);
                PlayerStatsManager.PlayerStats stats = plugin.getStatsManager().getStats(uuid);

                if (player != null && player.isOnline()) {
                    Map<String, String> data = computePlayerData(player, stats);
                    MessageEmbed embed = buildEmbedFromTemplate(player,
                            cfg.getConfigurationSection("embed"), data,
                            new Color(0x5865F2));
                    event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
                } else {
                    ConfigurationSection offlineCfg = cfg.getConfigurationSection("offline");
                    if (offlineCfg != null && stats != null) {
                        Map<String, String> data = computeOfflineData(stats);
                        MessageEmbed embed = buildEmbedFromTemplate(null,
                                offlineCfg, data, Color.GRAY);
                        event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
                    } else {
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_online");
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Profile button error: " + e.getMessage());
                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
            }
        });
    }

    private void handleMoney(ButtonInteractionEvent event) {
        event.deferReply(true).queue();
        CompletableFuture.runAsync(() -> {
            try {
                long discordId = Long.parseLong(event.getUser().getId());
                UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

                if (uuid == null) {
                    plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_linked");
                    return;
                }

                try {
                    Class.forName("net.milkbowl.vault.economy.Economy");
                    var rsp = plugin.getServer().getServicesManager().getRegistration(
                            net.milkbowl.vault.economy.Economy.class);
                    if (rsp != null) {
                        net.milkbowl.vault.economy.Economy econ = rsp.getProvider();
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null && player.isOnline()) {
                            double balance = econ.getBalance(player);
                            EmbedBuilder embed = new EmbedBuilder();
                            embed.setTitle(plugin.msg("keys.discord.money.title"));
                            embed.setDescription(plugin.msg("keys.discord.money.balance") + String.format("%.2f", balance));
                            embed.setThumbnail("https://minotar.net/avatar/" + player.getName() + "/128");
                            embed.setColor(Color.YELLOW);
                            embed.setTimestamp(java.time.Instant.now());
                            event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
                        } else {
                            plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_online");
                        }
                    } else {
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "economy_disabled");
                    }
                } catch (ClassNotFoundException e) {
                    plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "economy_disabled");
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Money button error: " + e.getMessage());
                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
            }
        });
    }

    // ===================== Helper Methods =====================

    private Map<String, List<String>> categorizeInventory(ItemStack[] items) {
        Map<String, List<String>> categories = new HashMap<>();
        categories.put("weapons", new ArrayList<>());
        categories.put("armor", new ArrayList<>());
        categories.put("tools", new ArrayList<>());
        categories.put("food", new ArrayList<>());
        categories.put("potions", new ArrayList<>());
        categories.put("blocks", new ArrayList<>());
        categories.put("misc", new ArrayList<>());

        for (ItemStack item : items) {
            if (item == null || item.getType() == Material.AIR) continue;

            String category = categorizeMaterial(item.getType());
            String entry = formatMaterialName(item.getType()) + " x" + item.getAmount();
            categories.get(category).add(entry);
        }

        return categories;
    }

    private String categorizeMaterial(Material mat) {
        String name = mat.name();

        if (name.endsWith("_SWORD") || name.equals("BOW") || name.equals("CROSSBOW")
                || name.equals("TRIDENT") || name.equals("MACE")) {
            return "weapons";
        }
        if (name.endsWith("_HELMET") || name.endsWith("_CHESTPLATE") || name.endsWith("_LEGGINGS")
                || name.endsWith("_BOOTS") || name.equals("SHIELD") || name.equals("ELYTRA")) {
            return "armor";
        }
        if (name.endsWith("_PICKAXE") || name.endsWith("_AXE") || name.endsWith("_SHOVEL")
                || name.endsWith("_HOE") || name.equals("SHEARS") || name.equals("FLINT_AND_STEEL")
                || name.equals("FISHING_ROD")) {
            return "tools";
        }
        if (name.contains("POTION") || name.equals("TIPPED_ARROW") || name.equals("DRAGON_BREATH")) {
            return "potions";
        }
        if (mat.isEdible() || name.equals("CAKE") || name.equals("COOKIE")
                || name.equals("GOLDEN_APPLE") || name.equals("GOLDEN_CARROT")) {
            return "food";
        }
        if (mat.isBlock()) {
            return "blocks";
        }
        return "misc";
    }

    private String buildProgressBar(int current, int max) {
        if (max <= 0) return "░░░░░░░░░░";
        int filled = Math.min((int) ((double) current / max * 10), 10);
        int empty = 10 - filled;
        return "█".repeat(filled) + "░".repeat(empty);
    }

    private String getCardinalDirection(Player player) {
        float yaw = player.getLocation().getYaw();
        if (yaw < 0) yaw += 360;

        String dir = switch ((int) ((yaw + 22.5) % 360 / 45) % 8) {
            case 0 -> "S";
            case 1 -> "SW";
            case 2 -> "W";
            case 3 -> "NW";
            case 4 -> "N";
            case 5 -> "NE";
            case 6 -> "E";
            case 7 -> "SE";
            default -> "S";
        };

        String name = getDirectionName(dir);
        return name + " (" + dir + ")";
    }

    private String getDirectionName(String dir) {
        return plugin.msg("keys.discord.whereami.directions." + dir.toLowerCase());
    }

    private String getBlockFaceName(Location loc) {
        BlockFace face = loc.getBlock().getFace(loc.getBlock());
        return switch (face) {
            case UP -> plugin.msg("keys.discord.whereami.block_face.up");
            case DOWN -> plugin.msg("keys.discord.whereami.block_face.down");
            case NORTH -> plugin.msg("keys.discord.whereami.block_face.north");
            case SOUTH -> plugin.msg("keys.discord.whereami.block_face.south");
            case EAST -> plugin.msg("keys.discord.whereami.block_face.east");
            case WEST -> plugin.msg("keys.discord.whereami.block_face.west");
            default -> plugin.msg("keys.discord.whereami.block_face.side");
        };
    }

    private String formatMaterialName(Material mat) {
        String[] words = mat.name().split("_");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            if (!sb.isEmpty()) sb.append(" ");
            sb.append(word.charAt(0)).append(word.substring(1).toLowerCase());
        }
        return sb.toString();
    }

    private String formatPlaytime(long seconds) {
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        return hours + "h " + minutes + "m";
    }

    private String capitalizeWords(String input) {
        StringBuilder sb = new StringBuilder();
        for (String word : input.split(" ")) {
            if (!sb.isEmpty()) sb.append(" ");
            if (!word.isEmpty()) {
                sb.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
            }
        }
        return sb.toString();
    }

    private String getStringOrDefault(Map<String, Object> map, String key, String defaultValue) {
        if (map == null) return defaultValue;
        Object value = map.get(key);
        if (value == null) return defaultValue;
        String str = String.valueOf(value);
        return str.isEmpty() ? defaultValue : str;
    }

    private int getColorOrDefault(Map<String, Object> map, int defaultColor) {
        Object value = map.get("color");
        if (value instanceof Number num) return num.intValue();
        return defaultColor;
    }

    private Map<String, String> getCategoryLabels(Map<String, Object> config) {
        Map<String, String> labels = new HashMap<>();
        if (config.containsKey("categories") && config.get("categories") instanceof Map<?, ?> catMap) {
            catMap.forEach((k, v) -> labels.put(String.valueOf(k), String.valueOf(v)));
        }
        return labels;
    }

    // ═══════════════════════════════════════════════════════
    //  Template-based embed builders (customizable via YAML)
    // ═══════════════════════════════════════════════════════

    private Map<String, String> computePlayerData(Player player, PlayerStatsManager.PlayerStats stats) {
        Map<String, String> data = new HashMap<>();
        data.put("player_name", player.getName());
        data.put("player_uuid", player.getUniqueId().toString());
        data.put("player_health", String.valueOf((int) player.getHealth()));
        data.put("player_max_health", "20");
        data.put("player_health_bar", buildProgressBar((int) player.getHealth(), 20));
        data.put("player_food", String.valueOf(player.getFoodLevel()));
        data.put("player_max_food", "20");
        data.put("player_food_bar", buildProgressBar(player.getFoodLevel(), 20));
        data.put("player_level", String.valueOf(player.getLevel()));
        int baseXp = getTotalXpForLevel(player.getLevel());
        int nextXp = getTotalXpForLevel(player.getLevel() + 1);
        data.put("player_xp_bar", buildProgressBar(
                baseXp + (int)(player.getExpToLevel() * (nextXp - baseXp)),
                nextXp));
        data.put("player_world", player.getWorld().getName());
        data.put("player_gamemode", translateGamemode(player.getGameMode()));
        data.put("player_ping", String.valueOf(player.getPing()));
        data.put("player_direction", getCardinalDirection(player));
        Location loc = player.getLocation();
        data.put("player_x", String.valueOf(loc.getBlockX()));
        data.put("player_y", String.valueOf(loc.getBlockY()));
        data.put("player_z", String.valueOf(loc.getBlockZ()));

        if (stats != null) {
            data.put("player_kills", String.valueOf(stats.getKills()));
            data.put("player_deaths", String.valueOf(stats.getDeaths()));
            double kd = stats.getDeaths() == 0 ? stats.getKills()
                    : (double) stats.getKills() / stats.getDeaths();
            data.put("player_kd", String.format("%.2f", kd));
            data.put("player_playtime", formatPlaytime(stats.getTotalPlaytime()));
            data.put("player_first_join", stats.getFirstJoin() != null
                    ? stats.getFirstJoin().toString() : "");
        } else {
            data.put("player_kills", "0");
            data.put("player_deaths", "0");
            data.put("player_kd", "0.00");
            data.put("player_playtime", "0h 0m");
            data.put("player_first_join", "");
        }

        Long linkedId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());
        if (linkedId != null) {
            data.put("player_linked_status", plugin.msg("keys.discord.profile.linked_status")
                    .replace("{discord}", "<@" + linkedId + ">"));
            data.put("player_linked_discord", "<@" + linkedId + ">");
        } else {
            data.put("player_linked_status", plugin.msg("keys.discord.profile.unlinked_status"));
            data.put("player_linked_discord", "");
        }

        return data;
    }

    private Map<String, String> computeOfflineData(PlayerStatsManager.PlayerStats stats) {
        Map<String, String> data = new HashMap<>();
        data.put("player_name", stats.getPlayerName());
        data.put("player_uuid", stats.getUuid().toString());
        data.put("player_kills", String.valueOf(stats.getKills()));
        data.put("player_deaths", String.valueOf(stats.getDeaths()));
        double kd = stats.getDeaths() == 0 ? stats.getKills()
                : (double) stats.getKills() / stats.getDeaths();
        data.put("player_kd", String.format("%.2f", kd));
        data.put("player_playtime", formatPlaytime(stats.getTotalPlaytime()));
        data.put("player_first_join", stats.getFirstJoin() != null
                ? stats.getFirstJoin().toString() : "");
        return data;
    }

    private Map<String, String> computeInventoryData(Player player, ConfigurationSection cfg) {
        Map<String, String> data = new HashMap<>();
        data.put("player_name", player.getName());

        ItemStack[] contents = player.getInventory().getContents();
        Map<String, List<String>> categorized = categorizeInventory(contents);
        int maxPerCat = cfg.getInt("max_items_per_category", 10);
        boolean hideEmpty = cfg.getBoolean("empty_category_hidden", true);

        for (var entry : categorized.entrySet()) {
            List<String> items = entry.getValue();
            if (hideEmpty && items.isEmpty()) {
                data.put("inv_" + entry.getKey(), plugin.msg("keys.discord.inventory.empty"));
                continue;
            }
            String display = items.stream().limit(maxPerCat)
                    .collect(Collectors.joining("\n"));
            if (items.size() > maxPerCat) {
                display += "\n" + plugin.msg("keys.discord.inventory.more")
                        .replace("{count}", String.valueOf(items.size() - maxPerCat));
            }
            data.put("inv_" + entry.getKey(), display.isEmpty()
                    ? plugin.msg("keys.discord.inventory.empty") : display);
        }

        int usedSlots = 0;
        for (ItemStack item : contents) {
            if (item != null && item.getType() != Material.AIR) usedSlots++;
        }
        data.put("inv_used_slots", String.valueOf(usedSlots));
        data.put("inv_total_slots", String.valueOf(contents.length));

        return data;
    }

    private MessageEmbed buildEmbedFromTemplate(Player player, ConfigurationSection template,
            Map<String, String> repl, Color defaultColor) {
        if (template == null) return null;
        EmbedBuilder embed = new EmbedBuilder();

        String title = resolvePlaceholders(template.getString("title", ""), repl, player);
        if (!title.isEmpty()) embed.setTitle(title);

        String desc = resolvePlaceholders(template.getString("description", ""), repl, player);
        if (!desc.isEmpty()) embed.setDescription(desc);

        embed.setColor(parseColor(template.getString("color", ""), defaultColor.getRGB()));

        String thumbnail = resolvePlaceholders(template.getString("thumbnail", ""), repl, player);
        if (!thumbnail.isEmpty()) embed.setThumbnail(thumbnail);

        String image = resolvePlaceholders(template.getString("image", ""), repl, player);
        if (!image.isEmpty()) embed.setImage(image);

        String author = resolvePlaceholders(template.getString("author", ""), repl, player);
        if (!author.isEmpty()) embed.setAuthor(author);

        String footer = resolvePlaceholders(template.getString("footer", ""), repl, player);
        if (!footer.isEmpty()) embed.setFooter(footer);

        if (template.getBoolean("timestamp", false)) {
            embed.setTimestamp(java.time.Instant.now());
        }

        List<?> fieldsList = template.getList("fields");
        if (fieldsList != null) {
            for (Object obj : fieldsList) {
                if (obj instanceof Map<?, ?> map) {
                    Object nameObj = map.get("name");
                    Object valueObj = map.get("value");
                    String name = resolvePlaceholders(
                            nameObj != null ? String.valueOf(nameObj) : "", repl, player);
                    String value = resolvePlaceholders(
                            valueObj != null ? String.valueOf(valueObj) : "", repl, player);
                    boolean inline = Boolean.TRUE.equals(map.get("inline"));
                    if (!name.isEmpty() && !value.isEmpty()) {
                        embed.addField(name, value, inline);
                    }
                }
            }
        }

        return embed.build();
    }

    private String resolvePlaceholders(String text, Map<String, String> repl, Player player) {
        if (text == null) return "";
        for (var entry : repl.entrySet()) {
            text = text.replace("%" + entry.getKey() + "%", entry.getValue());
        }
        return plugin.applyPlaceholders(player, text);
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

    private String translateGamemode(org.bukkit.GameMode mode) {
        return switch (mode) {
            case SURVIVAL -> plugin.msg("keys.general.gamemode.survival");
            case CREATIVE -> plugin.msg("keys.general.gamemode.creative");
            case ADVENTURE -> plugin.msg("keys.general.gamemode.adventure");
            case SPECTATOR -> plugin.msg("keys.general.gamemode.spectator");
            default -> plugin.msg("keys.general.gamemode.unknown");
        };
    }

    // ponytail: approximate Minecraft XP formula for progress bar
    private int getTotalXpForLevel(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int)(2.5 * level * level - 40.5 * level + 360);
        return (int)(4.5 * level * level - 162.5 * level + 2220);
    }
}
