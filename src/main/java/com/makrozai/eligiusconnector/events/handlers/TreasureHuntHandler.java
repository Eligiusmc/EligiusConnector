package com.makrozai.eligiusconnector.events.handlers;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.events.EventCondition;
import com.makrozai.eligiusconnector.events.EventResult;
import com.makrozai.eligiusconnector.events.EventRewards;
import com.makrozai.eligiusconnector.events.GameEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class TreasureHuntHandler implements com.makrozai.eligiusconnector.events.EventHandler {

    private final EligiusConnector plugin;
    private Location currentTreasure;
    private int hintsGiven = 0;
    private final Map<UUID, Double> closestDistances = new ConcurrentHashMap<>();
    private final List<UUID> participants = new CopyOnWriteArrayList<>();
    private UUID finderUUID;
    private BukkitRunnable tickTask;
    private BukkitRunnable hintTask;

    public TreasureHuntHandler(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onStart(GameEvent event) {
        Map<String, Object> config = event.getConfig();

        String worldName = String.valueOf(config.getOrDefault("world", "world"));
        double minX = parseDouble(config.getOrDefault("min_x", -100));
        double maxX = parseDouble(config.getOrDefault("max_x", 100));
        double minZ = parseDouble(config.getOrDefault("min_z", -100));
        double maxZ = parseDouble(config.getOrDefault("max_z", 100));
        double y = parseDouble(config.getOrDefault("y", 64));
        int hintInterval = parseInt(config.getOrDefault("hint_interval", 60));
        int maxHints = parseInt(config.getOrDefault("max_hints", 5));
        double findRadius = parseDouble(config.getOrDefault("find_radius", 3.0));

        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        Random random = new Random();
        double treasureX = minX + random.nextDouble() * (maxX - minX);
        double treasureZ = minZ + random.nextDouble() * (maxZ - minZ);
        currentTreasure = new Location(world, treasureX, y, treasureZ);

        hintsGiven = 0;
        closestDistances.clear();
        participants.clear();
        finderUUID = null;

        startTickTask(event);
        startHintTask(event, hintInterval, maxHints);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("hints", String.valueOf(maxHints));
        replacements.put("radius", String.valueOf(findRadius));

        String channel = event.getChannel();
        if (channel != null && !channel.isEmpty()) {
            plugin.getDiscordManager().sendEmbed(channel, event.getDiscordStart(), replacements);
        }
    }

    private void startTickTask(GameEvent event) {
        tickTask = new BukkitRunnable() {
            @Override
            public void run() {
                onTick(event);
            }
        };
        tickTask.runTaskTimer(plugin, 10L, 10L);
    }

    private void startHintTask(GameEvent event, int intervalSeconds, int maxHints) {
        hintTask = new BukkitRunnable() {
            @Override
            public void run() {
                if (hintsGiven >= maxHints) {
                    cancel();
                    return;
                }
                broadcastHint(event);
            }
        };
        hintTask.runTaskTimer(plugin, intervalSeconds * 20L, intervalSeconds * 20L);
    }

    @Override
    public void onTick(GameEvent event) {
        if (currentTreasure == null || finderUUID != null) return;

        Map<String, Object> config = event.getConfig();
        double findRadius = parseDouble(config.getOrDefault("find_radius", 3.0));
        double warnRadius = parseDouble(config.getOrDefault("warn_radius", 10.0));

        for (Player player : currentTreasure.getWorld().getPlayers()) {
            UUID playerId = player.getUniqueId();
            double distance = player.getLocation().distance(currentTreasure);

            closestDistances.merge(playerId, distance, Math::min);

            if (!participants.contains(playerId)) {
                participants.add(playerId);
            }

            if (distance <= findRadius) {
                finderUUID = playerId;
                EventResult result = EventResult.success();
                result.setWinner(playerId);

                List<Map.Entry<UUID, Double>> top = closestDistances.entrySet().stream()
                        .sorted(Map.Entry.comparingByValue())
                        .limit(5)
                        .toList();
                result.setTopSearchers(top.stream().map(Map.Entry::getKey).toList());

                onEnd(event, result);
                return;
            }

            if (distance <= warnRadius) {
                sendProximityFeedback(player, distance, warnRadius);
            }
        }
    }

    private void sendProximityFeedback(Player player, double distance, double maxRadius) {
        double intensity = 1.0 - (distance / maxRadius);

        if (intensity > 0.8) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);
            player.spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation().add(0, 1, 0), 10);
        } else if (intensity > 0.5) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 0.8f, 1.5f);
            player.spawnParticle(Particle.COMPOSTER, player.getLocation().add(0, 1, 0), 5);
        } else if (intensity > 0.2) {
            player.playSound(player.getLocation(), Sound.BLOCK_AMETHYST_BLOCK_CHIME, 0.6f, 1.0f);
        }
    }

    private void broadcastHint(GameEvent event) {
        if (currentTreasure == null) return;

        Map<String, Object> config = event.getConfig();
        int maxHints = parseInt(config.getOrDefault("max_hints", 5));

        hintsGiven++;

        String direction;
        if (closestDistances.isEmpty()) {
            direction = "El tesoro está en algún lugar del mundo...";
        } else {
            UUID closest = closestDistances.entrySet().stream()
                    .min(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse(null);

            if (closest != null) {
                Player closestPlayer = Bukkit.getPlayer(closest);
                if (closestPlayer != null) {
                    double dx = currentTreasure.getX() - closestPlayer.getLocation().getX();
                    double dz = currentTreasure.getZ() - closestPlayer.getLocation().getZ();

                    if (Math.abs(dx) > Math.abs(dz)) {
                        direction = dx > 0 ? "¡El tesoro está al ESTE!" : "¡El tesoro está al OESTE!";
                    } else {
                        direction = dz > 0 ? "¡El tesoro está al SUR!" : "¡El tesoro está al NORTE!";
                    }
                } else {
                    direction = "Pista " + hintsGiven + "/" + maxHints;
                }
            } else {
                direction = "Pista " + hintsGiven + "/" + maxHints;
            }
        }

        String message = ChatColor.GOLD + "" + ChatColor.BOLD + "[TESORO] " +
                ChatColor.YELLOW + direction +
                ChatColor.GRAY + " (" + hintsGiven + "/" + maxHints + " pistas)";

        Bukkit.broadcastMessage(message);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("hint", direction);
        replacements.put("hints_given", String.valueOf(hintsGiven));
        replacements.put("max_hints", String.valueOf(maxHints));

        String channel = event.getChannel();
        if (channel != null && !channel.isEmpty()) {
            plugin.getDiscordManager().sendEmbed(channel, event.getDiscordStart(), replacements);
        }
    }

    @Override
    public void onEnd(GameEvent event, EventResult result) {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
        if (hintTask != null) {
            hintTask.cancel();
            hintTask = null;
        }

        distributeRewards(event, result);
        cleanup(event);
    }

    private void distributeRewards(GameEvent event, EventResult result) {
        EventRewards rewards = event.getRewards();
        if (rewards == null) return;

        result.getWinner().ifPresent(winnerId -> {
            Player winner = Bukkit.getPlayer(winnerId);
            if (winner == null) return;
            EventRewards.RewardGroup group = rewards.getGroups().get("finder");
            if (group == null) group = rewards.getGroups().get("winner");
            if (group != null) {
                for (String cmd : group.getCommands()) {
                    plugin.getServer().dispatchCommand(
                            plugin.getServer().getConsoleSender(),
                            cmd.replace("%player%", winner.getName())
                    );
                }
                if (!group.getMessage().isEmpty()) {
                    winner.sendMessage(ChatColor.translateAlternateColorCodes('&', group.getMessage()));
                }
            }
        });

        List<UUID> topSearchers = result.getTopSearchers(5);
        EventRewards.RewardGroup searcherGroup = rewards.getGroups().get("searcher");
        if (searcherGroup != null) {
            for (UUID searcherId : topSearchers) {
                if (result.getWinner().isPresent() && searcherId.equals(result.getWinner().get())) continue;
                Player searcher = Bukkit.getPlayer(searcherId);
                if (searcher == null) continue;
                for (String cmd : searcherGroup.getCommands()) {
                    plugin.getServer().dispatchCommand(
                            plugin.getServer().getConsoleSender(),
                            cmd.replace("%player%", searcher.getName())
                    );
                }
                if (!searcherGroup.getMessage().isEmpty()) {
                    searcher.sendMessage(ChatColor.translateAlternateColorCodes('&', searcherGroup.getMessage()));
                }
            }
        }
    }

    @Override
    public boolean evaluateCondition(GameEvent event, EventCondition condition) {
        return switch (condition.getType()) {
            case "treasure_found" -> finderUUID != null;
            case "timeout" -> {
                long elapsed = System.currentTimeMillis() - event.getStartTime();
                yield elapsed >= condition.getMinutes() * 60_000L;
            }
            default -> false;
        };
    }

    @Override
    public void cleanup(GameEvent event) {
        if (tickTask != null) {
            tickTask.cancel();
            tickTask = null;
        }
        if (hintTask != null) {
            hintTask.cancel();
            hintTask = null;
        }
        currentTreasure = null;
        closestDistances.clear();
        participants.clear();
        finderUUID = null;
        hintsGiven = 0;
    }

    @Override
    public List<UUID> getParticipants(GameEvent event) {
        return List.copyOf(participants);
    }

    private double parseDouble(Object value) {
        if (value instanceof Number num) return num.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private int parseInt(Object value) {
        if (value instanceof Number num) return num.intValue();
        try {
            return Integer.parseInt(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
