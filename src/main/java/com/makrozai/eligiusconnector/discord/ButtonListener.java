package com.makrozai.eligiusconnector.discord;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.stats.PlayerStatsManager;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

    private static final Map<String, String> DIRECTION_NAMES = Map.of(
            "N", "Norte",
            "NE", "Noreste",
            "E", "Este",
            "SE", "Sureste",
            "S", "Sur",
            "SW", "Suroeste",
            "W", "Oeste",
            "NW", "Noroeste"
    );

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
                embed.addField("📋 Pasos", plugin.msg("keys.discord.verify.code_steps").replace("{code}", code), false);

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

                Location loc = player.getLocation();
                Block block = loc.getBlock();
                String direction = getCardinalDirection(player);
                String biome = block.getBiome().name().toLowerCase().replace("_", " ");
                String blockFace = getBlockFaceName(loc);

                Map<String, Object> config = plugin.getConfigAdapter().getWhereamiEmbed();

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(plugin.applyPlaceholders(player, getStringOrDefault(config, "title", "📍 Ubicación").replace("{player}", player.getName())));
                embed.setColor(getColorOrDefault(config, 0x2ECC71));

                String thumbnail = getStringOrDefault(config, "thumbnail", "");
                if (!thumbnail.isEmpty()) {
                    embed.setThumbnail(plugin.applyPlaceholders(player, thumbnail.replace("{player}", player.getName())));
                }

                String info = """
                        %s      %s
                        %s          %d
                        %s          %d
                        %s          %d
                        %s  %s
                        %s      %s
                        %s     %s
                        """.formatted(
                        plugin.msg("keys.discord.whereami.world"), loc.getWorld().getName(),
                        plugin.msg("keys.discord.whereami.x"), loc.getBlockX(),
                        plugin.msg("keys.discord.whereami.y"), loc.getBlockY(),
                        plugin.msg("keys.discord.whereami.z"), loc.getBlockZ(),
                        plugin.msg("keys.discord.whereami.direction"), direction,
                        plugin.msg("keys.discord.whereami.biome"), capitalizeWords(biome),
                        plugin.msg("keys.discord.whereami.block"), blockFace
                );

                embed.addField("**" + plugin.msg("keys.discord.whereami.info") + "**", "```" + info + "```", false);

                if (Boolean.TRUE.equals(config.getOrDefault("show_tp_command", true))) {
                    embed.setFooter(plugin.msg("keys.discord.whereami.tp_footer")
                            .replace("{x}", String.valueOf(loc.getBlockX()))
                            .replace("{y}", String.valueOf(loc.getBlockY()))
                            .replace("{z}", String.valueOf(loc.getBlockZ())));
                }

                embed.setTimestamp(java.time.Instant.now());
                event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
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

                ItemStack[] contents = player.getInventory().getContents();
                Map<String, List<String>> categorized = categorizeInventory(contents);

                Map<String, Object> config = plugin.getConfigAdapter().getInventoryEmbed();
                Map<String, String> categoryLabels = getCategoryLabels(config);

                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(plugin.applyPlaceholders(player, getStringOrDefault(config, "title", "🎒 Inventario").replace("{player}", player.getName())));
                embed.setColor(getColorOrDefault(config, 0xF39C12));

                String thumbnail = getStringOrDefault(config, "thumbnail", "");
                if (!thumbnail.isEmpty()) {
                    embed.setThumbnail(plugin.applyPlaceholders(player, thumbnail.replace("{player}", player.getName())));
                }

                int maxPerCategory = 10;
                boolean hideEmpty = Boolean.TRUE.equals(config.getOrDefault("empty_category_hidden", true));

                for (var entry : categorized.entrySet()) {
                    List<String> items = entry.getValue();
                    if (hideEmpty && items.isEmpty()) continue;

                    String emoji = switch (entry.getKey()) {
                        case "weapons" -> "⚔️";
                        case "armor" -> "🛡️";
                        case "tools" -> "⛏️";
                        case "food" -> "🍎";
                        case "potions" -> "🧪";
                        case "blocks" -> "🧱";
                        default -> "📦";
                    };

                    String label = categoryLabels.getOrDefault(entry.getKey(), entry.getKey());
                    String displayItems = items.stream()
                            .limit(maxPerCategory)
                            .collect(Collectors.joining("\n"));

                    String suffix = items.size() > maxPerCategory
                            ? "\n" + plugin.msg("keys.discord.inventory.more").replace("{count}", String.valueOf(items.size() - maxPerCategory))
                            : "";

                    embed.addField(emoji + " " + label, displayItems.isEmpty() ? plugin.msg("keys.discord.inventory.empty") : displayItems + suffix, false);
                }

                int usedSlots = 0;
                for (ItemStack item : contents) {
                    if (item != null && item.getType() != Material.AIR) usedSlots++;
                }

                String footer = getStringOrDefault(config, "footer", plugin.msg("keys.discord.inventory.footer"));
                embed.setFooter(footer.replace("{used}", String.valueOf(usedSlots)).replace("{total}", String.valueOf(contents.length)));
                embed.setTimestamp(java.time.Instant.now());

                event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
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

        Bukkit.getScheduler().runTask(plugin, () -> {
            try {
                if (uuid == null) {
                    plugin.getDiscordManager().sendErrorEmbed(event.getHook(), "not_linked");
                    return;
                }

                Map<String, Object> config = plugin.getConfigAdapter().getProfileEmbed();
                EmbedBuilder embed = new EmbedBuilder();

                Player player = Bukkit.getPlayer(uuid);
                PlayerStatsManager.PlayerStats stats = plugin.getStatsManager().getStats(uuid);

                if (player != null && player.isOnline()) {
                    embed.setTitle(plugin.applyPlaceholders(player, getStringOrDefault(config, "title", plugin.msg("keys.discord.profile.title")).replace("{player}", player.getName())));
                    embed.setColor(getColorOrDefault(config, 0x5865F2));
                    embed.setThumbnail(plugin.applyPlaceholders(player, "https://minotar.net/avatar/" + player.getName() + "/128"));

                    Long linkedId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());

                    embed.addField(plugin.msg("keys.discord.profile.info_label"),
                            "**Nombre:** " + player.getName() + "\n**UUID:** " + player.getUniqueId(), false);

                    embed.addField(plugin.msg("keys.discord.profile.linked_label"),
                            linkedId != null ? plugin.msg("keys.discord.profile.linked_status").replace("{discord}", "<@" + linkedId + ">")
                                    : plugin.msg("keys.discord.profile.unlinked_status"),
                            false);

                    int kills = stats != null ? stats.getKills() : 0;
                    int deaths = stats != null ? stats.getDeaths() : 0;
                    double kd = deaths == 0 ? kills : (double) kills / deaths;
                    embed.addField(plugin.msg("keys.discord.profile.combat_label"),
                            "**Kills:** " + kills + "\n**Deaths:** " + deaths + "\n**K/D:** " + String.format("%.2f", kd),
                            false);

                    String playtime = stats != null ? formatPlaytime(stats.getTotalPlaytime()) : "0h 0m";
                    embed.addField(plugin.msg("keys.discord.profile.playtime_label"), "**" + playtime + "**", false);

                    String healthBar = buildProgressBar((int) player.getHealth(), 20);
                    embed.addField(plugin.msg("keys.discord.profile.health_label"),
                            healthBar + " " + (int) player.getHealth() + "/20",
                            false);

                    String foodBar = buildProgressBar(player.getFoodLevel(), 20);
                    embed.addField(plugin.msg("keys.discord.profile.food_label"),
                            foodBar + " " + player.getFoodLevel() + "/20",
                            false);

                    embed.addField(plugin.msg("keys.discord.profile.xp_label"), "**" + player.getLevel() + " nivel(es)**", false);

                    embed.addField(plugin.msg("keys.discord.profile.level_label"), "**" + player.getLevel() + "**", true);
                    embed.addField(plugin.msg("keys.discord.profile.world_label"), "**" + player.getWorld().getName() + "**", true);
                    embed.addField(plugin.msg("keys.discord.profile.direction_label"), "**" + getCardinalDirection(player) + "**", true);

                    Location loc = player.getLocation();
                    embed.addField(plugin.msg("keys.discord.profile.coords_label"),
                            "**X:** " + loc.getBlockX() + " **Y:** " + loc.getBlockY() + " **Z:** " + loc.getBlockZ(),
                            true);

                    String gamemode = switch (player.getGameMode()) {
                        case SURVIVAL -> "Supervivencia";
                        case CREATIVE -> "Creativo";
                        case ADVENTURE -> "Aventura";
                        case SPECTATOR -> "Espectador";
                        default -> player.getGameMode().name();
                    };
                    embed.addField(plugin.msg("keys.discord.profile.gamemode_label"), "**" + gamemode + "**", true);
                    embed.addField(plugin.msg("keys.discord.profile.ping_label"), "**" + player.getPing() + " ms**", true);

                } else {
                    embed.setTitle(plugin.applyPlaceholders(null, getStringOrDefault(config, "title", plugin.msg("keys.discord.profile.title")).replace("{player}", "Jugador offline")));
                    embed.setColor(Color.GRAY);

                    if (stats != null) {
                        embed.addField(plugin.msg("keys.discord.profile.info_label"),
                                "**Nombre:** " + stats.getPlayerName() + "\n**UUID:** " + stats.getUuid(), false);

                        String playtime = formatPlaytime(stats.getTotalPlaytime());
                        embed.addField(plugin.msg("keys.discord.profile.playtime_label"), "**" + playtime + "**", false);
                        embed.addField(plugin.msg("keys.discord.profile.combat_label"),
                                "**Kills:** " + stats.getKills() + "\n**Deaths:** " + stats.getDeaths(),
                                false);
                    } else {
                        embed.setDescription(plugin.msg("keys.discord.profile.offline_desc"));
                    }
                }

                String footer = getStringOrDefault(config, "footer", plugin.msg("keys.discord.profile.footer"));
                embed.setFooter(footer);
                embed.setTimestamp(java.time.Instant.now());

                event.getHook().sendMessageEmbeds(embed.build()).setEphemeral(true).queue();
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

        String name = DIRECTION_NAMES.getOrDefault(dir, dir);
        return name + " (" + dir + ")";
    }

    private String getBlockFaceName(Location loc) {
        BlockFace face = loc.getBlock().getFace(loc.getBlock());
        return switch (face) {
            case UP -> "Arriba (TOP)";
            case DOWN -> "Abajo (BOTTOM)";
            case NORTH -> "Norte";
            case SOUTH -> "Sur";
            case EAST -> "Este";
            case WEST -> "Oeste";
            default -> "Lado";
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
}
