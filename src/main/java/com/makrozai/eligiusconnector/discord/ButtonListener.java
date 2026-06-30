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
            case "set_birthday" -> handleSetBirthday(event);
            default -> event.reply("Acción desconocida").setEphemeral(true).queue();
        }
    }

    private void handleVerify(ButtonInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            long discordId = Long.parseLong(event.getUser().getId());
            UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

            if (uuid != null) {
                event.reply(plugin.getConfigAdapter().getVerifyAlreadyLinked()).setEphemeral(true).queue();
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
                embed.setThumbnail(thumbnail.replace("{player}", event.getUser().getName()));
            }

            String author = getStringOrDefault(embedConfig, "author", "");
            if (!author.isEmpty()) {
                String authorIcon = getStringOrDefault(embedConfig, "author_icon", "");
                embed.setAuthor(author, null, authorIcon.isEmpty() ? null : authorIcon);
            }

            embed.addField("🔐 Código de Verificación", "`" + code + "`", false);
            embed.addBlankField(false);
            embed.addField("📋 Pasos", "1. Entra al servidor de Minecraft\n2. Escribe `/verify " + code + "` en el chat", false);

            String footer = getStringOrDefault(embedConfig, "footer", "");
            if (!footer.isEmpty()) {
                embed.setFooter(footer);
            }

            if (Boolean.TRUE.equals(embedConfig.get("timestamp"))) {
                embed.setTimestamp(java.time.Instant.now());
            }

            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        });
    }

    private void handleWhereAmI(ButtonInteractionEvent event) {
        long discordId = Long.parseLong(event.getUser().getId());
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

        if (uuid == null) {
            event.reply("❌ Tu cuenta no está vinculada").setEphemeral(true).queue();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                event.reply("❌ No estás en línea").setEphemeral(true).queue();
                return;
            }

            Location loc = player.getLocation();
            Block block = loc.getBlock();
            String direction = getCardinalDirection(player);
            String biome = block.getBiome().name().toLowerCase().replace("_", " ");
            String blockFace = getBlockFaceName(loc);

            Map<String, Object> config = plugin.getConfigAdapter().getWhereamiEmbed();

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(getStringOrDefault(config, "title", "📍 Ubicación").replace("{player}", player.getName()));
            embed.setColor(getColorOrDefault(config, 0x2ECC71));

            String thumbnail = getStringOrDefault(config, "thumbnail", "");
            if (!thumbnail.isEmpty()) {
                embed.setThumbnail(thumbnail.replace("{player}", player.getName()));
            }

            String info = """
                    Mundo:      %s
                    X:          %d
                    Y:          %d
                    Z:          %d
                    Dirección:  %s
                    Bioma:      %s
                    Bloque:     %s
                    """.formatted(
                    loc.getWorld().getName(),
                    loc.getBlockX(),
                    loc.getBlockY(),
                    loc.getBlockZ(),
                    direction,
                    capitalizeWords(biome),
                    blockFace
            );

            embed.addField("**📍 Tu Ubicación**", "```" + info + "```", false);

            if (Boolean.TRUE.equals(config.getOrDefault("show_tp_command", true))) {
                embed.setFooter("/tp @s " + loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());
            }

            embed.setTimestamp(java.time.Instant.now());
            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        });
    }

    private void handleInventory(ButtonInteractionEvent event) {
        long discordId = Long.parseLong(event.getUser().getId());
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

        if (uuid == null) {
            event.reply("❌ Tu cuenta no está vinculada").setEphemeral(true).queue();
            return;
        }

        Bukkit.getScheduler().runTask(plugin, () -> {
            Player player = Bukkit.getPlayer(uuid);
            if (player == null || !player.isOnline()) {
                event.reply("❌ No estás en línea").setEphemeral(true).queue();
                return;
            }

            ItemStack[] contents = player.getInventory().getContents();
            Map<String, List<String>> categorized = categorizeInventory(contents);

            Map<String, Object> config = plugin.getConfigAdapter().getInventoryEmbed();
            Map<String, String> categoryLabels = getCategoryLabels(config);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle(getStringOrDefault(config, "title", "🎒 Inventario").replace("{player}", player.getName()));
            embed.setColor(getColorOrDefault(config, 0xF39C12));

            String thumbnail = getStringOrDefault(config, "thumbnail", "");
            if (!thumbnail.isEmpty()) {
                embed.setThumbnail(thumbnail.replace("{player}", player.getName()));
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
                        ? "\n... +" + (items.size() - maxPerCategory) + " más"
                        : "";

                embed.addField(emoji + " " + label, displayItems.isEmpty() ? "*Vacío*" : displayItems + suffix, false);
            }

            int usedSlots = 0;
            for (ItemStack item : contents) {
                if (item != null && item.getType() != Material.AIR) usedSlots++;
            }

            String footer = getStringOrDefault(config, "footer", "Espacios: {used}/{total}");
            embed.setFooter(footer.replace("{used}", String.valueOf(usedSlots)).replace("{total}", String.valueOf(contents.length)));
            embed.setTimestamp(java.time.Instant.now());

            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        });
    }

    private void handleProfile(ButtonInteractionEvent event) {
        long discordId = Long.parseLong(event.getUser().getId());
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

        Bukkit.getScheduler().runTask(plugin, () -> {
            Map<String, Object> config = plugin.getConfigAdapter().getProfileEmbed();
            EmbedBuilder embed = new EmbedBuilder();

            Player player = uuid != null ? Bukkit.getPlayer(uuid) : null;
            PlayerStatsManager.PlayerStats stats = uuid != null ? plugin.getStatsManager().getStats(uuid) : null;

            if (player != null && player.isOnline()) {
                embed.setTitle(getStringOrDefault(config, "title", "👤 Perfil").replace("{player}", player.getName()));
                embed.setColor(getColorOrDefault(config, 0x5865F2));
                embed.setThumbnail("https://minotar.net/avatar/" + player.getName() + "/128");

                String linkedDiscord = "No vinculado";
                Long linkedId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());
                if (linkedId != null) {
                    linkedDiscord = "<@" + linkedId + ">";
                }

                embed.addField("📋 Información",
                        "**Nombre:** " + player.getName() + "\n**UUID:** " + player.getUniqueId(), false);

                embed.addField("🔗 Vinculación",
                        linkedId != null ? "**Estado:** ✅ Vinculado\n**Discord:** " + linkedDiscord
                                : "**Estado:** ❌ No vinculado",
                        false);

                int kills = stats != null ? stats.getKills() : 0;
                int deaths = stats != null ? stats.getDeaths() : 0;
                double kd = deaths == 0 ? kills : (double) kills / deaths;
                embed.addField("☠️ Combate",
                        "**Kills:** " + kills + "\n**Deaths:** " + deaths + "\n**K/D:** " + String.format("%.2f", kd),
                        false);

                String playtime = stats != null ? formatPlaytime(stats.getTotalPlaytime()) : "0h 0m";
                embed.addField("⏱️ Playtime", "**" + playtime + "**", false);

                String healthBar = buildProgressBar((int) player.getHealth(), 20);
                embed.addField("❤️ Vida",
                        healthBar + " " + (int) player.getHealth() + "/20",
                        false);

                String foodBar = buildProgressBar(player.getFoodLevel(), 20);
                embed.addField("🍖 Hambre",
                        foodBar + " " + player.getFoodLevel() + "/20",
                        false);

                int totalXp = player.getLevel() * 1000 + player.getExpToLevel();
                String xpBar = buildProgressBar(totalXp - player.getExpToLevel(), totalXp);
                embed.addField("⭐ Experiencia",
                        xpBar + " " + player.getLevel() + " nivel(es)",
                        false);

                embed.addField("🎯 Nivel", "**" + player.getLevel() + "**", true);

                embed.addField("🌍 Mundo", "**" + player.getWorld().getName() + "**", true);

                embed.addField("🧭 Dirección", "**" + getCardinalDirection(player) + "**", true);

                Location loc = player.getLocation();
                embed.addField("📍 Coordenadas",
                        "**X:** " + loc.getBlockX() + " **Y:** " + loc.getBlockY() + " **Z:** " + loc.getBlockZ(),
                        true);

                String gamemode = switch (player.getGameMode()) {
                    case SURVIVAL -> "Supervivencia";
                    case CREATIVE -> "Creativo";
                    case ADVENTURE -> "Aventura";
                    case SPECTATOR -> "Espectador";
                    default -> player.getGameMode().name();
                };
                embed.addField("🎮 Gamemode", "**" + gamemode + "**", true);

                embed.addField("📡 Ping", "**" + player.getPing() + " ms**", true);

            } else {
                embed.setTitle(getStringOrDefault(config, "title", "👤 Perfil").replace("{player}", uuid != null ? "Jugador offline" : "Sin vincular"));
                embed.setColor(Color.GRAY);

                if (stats != null) {
                    embed.addField("📋 Información",
                            "**Nombre:** " + stats.getPlayerName() + "\n**UUID:** " + stats.getUuid(), false);

                    String playtime = formatPlaytime(stats.getTotalPlaytime());
                    embed.addField("⏱️ Playtime", "**" + playtime + "**", false);
                    embed.addField("☠️ Combate",
                            "**Kills:** " + stats.getKills() + "\n**Deaths:** " + stats.getDeaths(),
                            false);
                } else {
                    embed.setDescription("Jugador no encontrado o sin datos disponibles");
                }
            }

            String footer = getStringOrDefault(config, "footer", "Datos en tiempo real");
            embed.setFooter(footer);
            embed.setTimestamp(java.time.Instant.now());

            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        });
    }

    private void handleMoney(ButtonInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            long discordId = Long.parseLong(event.getUser().getId());
            UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);

            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("💰 Balance");
            embed.setColor(Color.YELLOW);

            if (uuid == null) {
                embed.setDescription("❌ Tu cuenta no está vinculada");
                event.replyEmbeds(embed.build()).setEphemeral(true).queue();
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
                        embed.setDescription("**Balance:** $" + String.format("%.2f", balance));
                        embed.setThumbnail("https://minotar.net/avatar/" + player.getName() + "/128");
                    } else {
                        embed.setDescription("❌ No estás en línea");
                    }
                } else {
                    embed.setDescription("⚠️ Economy no está configurada");
                }
            } catch (ClassNotFoundException e) {
                embed.setDescription("⚠️ Economy no está configurada");
            }

            event.replyEmbeds(embed.build()).setEphemeral(true).queue();
        });
    }

    private void handleSetBirthday(ButtonInteractionEvent event) {
        CompletableFuture.runAsync(() -> {
            long discordId = Long.parseLong(event.getUser().getId());
            String existing = plugin.getDatabaseManager().getBirthday(discordId);

            if (existing != null && !existing.isEmpty()) {
                event.reply("✅ Ya tienes un cumpleaños establecido: **" + existing + "**\nUsa `/birthday` para cambiarlo.")
                        .setEphemeral(true).queue();
                return;
            }

            event.reply("📅 Usa el comando `/birthday set dd/MM/yyyy` para establecer tu cumpleaños.")
                    .setEphemeral(true).queue();
        });
    }

    // ===================== Helper Methods =====================

    private Player getPlayerByDiscordId(String userId) {
        long discordId = Long.parseLong(userId);
        UUID uuid = plugin.getDatabaseManager().getMinecraftUuid(discordId);
        return uuid != null ? Bukkit.getPlayer(uuid) : null;
    }

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
        Object value = map.get(key);
        return value != null ? String.valueOf(value) : defaultValue;
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
