package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.stats.PlayerStatsManager;
import com.makrozai.eligiusconnector.util.Scheduler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.makrozai.eligiusconnector.util.ColorUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;
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
                Map<String, Object> embedConfig = plugin.getConfigAdapter().getVerifyCodeEmbed();
                Map<String, String> replacements = java.util.Map.of("code", code);

                MessageEmbed embed = plugin.getDiscordManager().buildEmbed(embedConfig, replacements, null);
                if (embed == null) {
                    plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                    return;
                }
                event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
            } catch (Exception e) {
                plugin.getLogger().warning("Verify button error: " + e.getMessage());
                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
            }
        });
    }

    private void handleWhereAmI(ButtonInteractionEvent event) {
        event.deferReply(true).queue();
        long discordId = Long.parseLong(event.getUser().getId());
        findLinkedUuid(event, uuid -> Scheduler.runSync(plugin, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_online");
                return;
            }
            Scheduler.runForEntity(plugin, player, () -> {
                try {
                    ConfigurationSection cfg = plugin.getConfigAdapter().getProfileConfig()
                            .getConfigurationSection("whereami");
                    if (cfg == null) {
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                        return;
                    }
                    Map<String, String> data = computePlayerData(player, null, discordId);
                    data.put("player_biome", player.getLocation().getBlock().getBiome().name()
                            .toLowerCase().replace("_", " "));
                    data.put("player_block", getBlockFaceName(player));

                    MessageEmbed embed = buildEmbedFromTemplate(player,
                            cfg.getConfigurationSection("embed"), data, new Color(0x2ECC71));
                    if (embed == null) {
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                        return;
                    }
                    event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
                } catch (Exception e) {
                    plugin.getLogger().warning("WhereAmI button error: " + e.getMessage());
                    plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                }
            });
        }));
    }

    private void handleInventory(ButtonInteractionEvent event) {
        event.deferReply(true).queue();
        findLinkedUuid(event, uuid -> Scheduler.runSync(plugin, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_online");
                return;
            }
            Scheduler.runForEntity(plugin, player, () -> {
                try {
                    ConfigurationSection cfg = plugin.getConfigAdapter().getProfileConfig()
                            .getConfigurationSection("inventory");
                    if (cfg == null) {
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                        return;
                    }
                    ConfigurationSection opts = cfg.getConfigurationSection("options");
                    Map<String, String> data = computeInventoryData(player, opts);
                    MessageEmbed embed = buildEmbedFromTemplate(player,
                            cfg.getConfigurationSection("embed"), data, new Color(0xF39C12));
                    if (embed == null) {
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                        return;
                    }
                    event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
                } catch (Exception e) {
                    plugin.getLogger().warning("Inventory button error: " + e.getMessage());
                    plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                }
            });
        }));
    }

    private void handleProfile(ButtonInteractionEvent event) {
        event.deferReply(true).queue();
        long discordId = Long.parseLong(event.getUser().getId());
        findLinkedUuid(event, uuid -> CompletableFuture.supplyAsync(() -> new ProfileData(
                plugin.getStatsManager().getStats(uuid),
                plugin.getDatabaseManager().getMinecraftName(discordId)))
                .whenComplete((profileData, error) -> {
                    if (error != null) {
                        plugin.getLogger().warning("Failed to load profile data: " + error.getMessage());
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                        return;
                    }
                    Scheduler.runSync(plugin, () -> {
                        ConfigurationSection cfg = plugin.getConfigAdapter().getProfileConfig()
                                .getConfigurationSection("profile");
                        if (cfg == null) {
                            plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                            return;
                        }
                        Player player = Bukkit.getPlayer(uuid);

                        if (player != null && player.isOnline()) {
                            Scheduler.runForEntity(plugin, player, () -> {
                                Map<String, String> data = computePlayerData(player, profileData.stats(), discordId);
                                MessageEmbed embed = buildEmbedFromTemplate(player,
                                        cfg.getConfigurationSection("embed"), data, new Color(0x5865F2));
                                if (embed != null) event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
                                else plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                            });
                        } else {
                            ConfigurationSection offlineCfg = cfg.getConfigurationSection("offline");
                            if (offlineCfg == null) {
                                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_online");
                                return;
                            }
                            Map<String, String> data;
                            if (profileData.stats() != null) {
                                data = computeOfflineData(profileData.stats());
                            } else {
                                data = new HashMap<>();
                                data.put("player_name", profileData.name() != null ? profileData.name() : "Unknown");
                                data.put("player_uuid", uuid.toString());
                                data.put("player_kills", "0");
                                data.put("player_deaths", "0");
                                data.put("player_kd", "0.00");
                                data.put("player_playtime", "0h 0m");
                                data.put("player_first_join", "");
                            }
                            MessageEmbed embed = buildEmbedFromTemplate(null, offlineCfg, data, Color.GRAY);
                            if (embed != null) event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
                            else plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                        }
                    });
                }));
    }

    private void handleMoney(ButtonInteractionEvent event) {
        event.deferReply(true).queue();
        long discordId = Long.parseLong(event.getUser().getId());
        findLinkedUuid(event, uuid -> CompletableFuture.supplyAsync(() ->
                plugin.getDatabaseManager().getMinecraftName(discordId))
                .whenComplete((name, error) -> {
                    if (error != null) {
                        plugin.getLogger().warning("Money button DB error: " + error.getMessage());
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                        return;
                    }
                    try {
                        Class.forName("net.milkbowl.vault.economy.Economy");
                    } catch (ClassNotFoundException e) {
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "economy_disabled");
                        return;
                    }
                    Scheduler.runSync(plugin, () -> {
                        var rsp = plugin.getServer().getServicesManager().getRegistration(
                                net.milkbowl.vault.economy.Economy.class);
                        if (rsp == null) {
                            plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "economy_disabled");
                            return;
                        }
                        Player player = Bukkit.getPlayer(uuid);
                        Runnable sendBalance = () -> {
                            try {
                                String playerName = name != null ? name : player != null ? player.getName() : "Unknown";
                                double balance = rsp.getProvider().getBalance(Bukkit.getOfflinePlayer(uuid));
                                Map<String, String> data = new HashMap<>();
                                data.put("player_name", playerName);
                                data.put("balance", String.format("%.2f", balance));
                                ConfigurationSection cfg = plugin.getConfigAdapter().getProfileConfig()
                                        .getConfigurationSection("money.embed");
                                MessageEmbed embed = buildEmbedFromTemplate(player, cfg, data, new Color(0xF1C40F));
                                if (embed != null) event.getHook().sendMessageEmbeds(embed).setEphemeral(true).queue();
                                else plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                            } catch (Exception e) {
                                plugin.getLogger().warning("Money button error: " + e.getMessage());
                                plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                            }
                        };
                        if (player == null) sendBalance.run();
                        else Scheduler.runForEntity(plugin, player, sendBalance);
                    });
                }));
    }

    private void findLinkedUuid(ButtonInteractionEvent event, Consumer<UUID> action) {
        long discordId = Long.parseLong(event.getUser().getId());
        CompletableFuture.supplyAsync(() -> plugin.getDatabaseManager().getMinecraftUuid(discordId))
                .whenComplete((uuid, error) -> {
                    if (error != null) {
                        plugin.getLogger().warning("Failed to load linked account: " + error.getMessage());
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "internal_error");
                    } else if (uuid == null) {
                        plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_linked");
                    } else {
                        action.accept(uuid);
                    }
                });
    }

    private record ProfileData(PlayerStatsManager.PlayerStats stats, String name) {}

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
        return plugin.msg("directions." + dir.toLowerCase());
    }

    private String getBlockFaceName(Player player) {
        // Issue #14: usar getFacing() del jugador, no getFace del bloque consigo mismo
        BlockFace face = player.getFacing();
        return switch (face) {
            case UP -> plugin.msg("blocks.up");
            case DOWN -> plugin.msg("blocks.down");
            case NORTH -> plugin.msg("blocks.north");
            case SOUTH -> plugin.msg("blocks.south");
            case EAST -> plugin.msg("blocks.east");
            case WEST -> plugin.msg("blocks.west");
            default -> plugin.msg("blocks.side");
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

    // ═══════════════════════════════════════════════════════
    //  Template-based embed builders (customizable via YAML)
    // ═══════════════════════════════════════════════════════

    private Map<String, String> computePlayerData(Player player, PlayerStatsManager.PlayerStats stats, Long linkedId) {
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

        if (linkedId != null) {
            data.put("player_linked_status", plugin.msg("profile.linked_status")
                    .replace("{discord}", "<@" + linkedId + ">"));
            data.put("player_linked_discord", "<@" + linkedId + ">");
        } else {
            data.put("player_linked_status", plugin.msg("profile.unlinked_status"));
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

    private Map<String, String> computeInventoryData(Player player, ConfigurationSection opts) {
        Map<String, String> data = new HashMap<>();
        data.put("player_name", player.getName());

        ItemStack[] contents = player.getInventory().getContents();
        Map<String, List<String>> categorized = categorizeInventory(contents);
        int maxPerCat = opts != null ? opts.getInt("max_items_per_category", 10) : 10;
        boolean hideEmpty = opts != null ? opts.getBoolean("empty_category_hidden", true) : true;

        for (var entry : categorized.entrySet()) {
            List<String> items = entry.getValue();
            if (hideEmpty && items.isEmpty()) {
                data.put("inv_" + entry.getKey(), plugin.msg("profile.inv_empty"));
                continue;
            }
            String display = items.stream().limit(maxPerCat)
                    .collect(Collectors.joining("\n"));
            if (items.size() > maxPerCat) {
                display += "\n" + plugin.msg("profile.inv_more")
                        .replace("{count}", String.valueOf(items.size() - maxPerCat));
            }
            data.put("inv_" + entry.getKey(), display.isEmpty()
                    ? plugin.msg("profile.inv_empty") : display);
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

        embed.setColor(ColorUtils.parseColor(template.getString("color", ""), defaultColor.getRGB()));

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
            text = text.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return plugin.applyPlaceholders(player, text);
    }



    private String translateGamemode(org.bukkit.GameMode mode) {
        return switch (mode) {
            case SURVIVAL -> plugin.msg("general.gamemode.survival");
            case CREATIVE -> plugin.msg("general.gamemode.creative");
            case ADVENTURE -> plugin.msg("general.gamemode.adventure");
            case SPECTATOR -> plugin.msg("general.gamemode.spectator");
            default -> plugin.msg("general.gamemode.unknown");
        };
    }

    // ponytail: approximate Minecraft XP formula for progress bar
    private int getTotalXpForLevel(int level) {
        if (level <= 16) return level * level + 6 * level;
        if (level <= 31) return (int)(2.5 * level * level - 40.5 * level + 360);
        return (int)(4.5 * level * level - 162.5 * level + 2220);
    }
}
