package com.makrozai.eligiusconnector.events.handlers;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.events.EventCondition;
import com.makrozai.eligiusconnector.events.EventResult;
import com.makrozai.eligiusconnector.events.EventRewards;
import com.makrozai.eligiusconnector.events.GameEvent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class PvPTournamentHandler implements com.makrozai.eligiusconnector.events.EventHandler, Listener {

    private final EligiusConnector plugin;
    private final List<Match> matches = new CopyOnWriteArrayList<>();
    private final List<UUID> eliminated = new CopyOnWriteArrayList<>();
    private final List<UUID> allParticipants = new CopyOnWriteArrayList<>();
    private int currentRound = 0;
    private BukkitRunnable tickTask;
    private final Map<UUID, Location> savedLocations = new HashMap<>();

    public PvPTournamentHandler(EligiusConnector plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onStart(GameEvent event) {
        Map<String, Object> config = event.getConfig();

        List<UUID> players = new ArrayList<>();
        String worldName = String.valueOf(config.getOrDefault("world", "world"));
        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        world.getPlayers().forEach(p -> {
            savedLocations.put(p.getUniqueId(), p.getLocation().clone());
            players.add(p.getUniqueId());
        });

        allParticipants.clear();
        allParticipants.addAll(players);
        eliminated.clear();
        matches.clear();
        currentRound = 0;

        Collections.shuffle(players, new Random());
        createMatches(players);

        teleportToArena(event);
        startTickTask(event);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("players", String.valueOf(players.size()));
        replacements.put("round", "1");

        String channel = event.getChannel();
        if (channel != null && !channel.isEmpty()) {
            plugin.getDiscordManager().sendEmbed(channel, event.getDiscordStart(), replacements);
        }
    }

    private void createMatches(List<UUID> players) {
        matches.clear();
        for (int i = 0; i < players.size() - 1; i += 2) {
            Match match = new Match(List.of(players.get(i), players.get(i + 1)));
            matches.add(match);
        }
        if (players.size() % 2 == 1) {
            UUID bye = players.getLast();
            Match autoWin = new Match(List.of(bye));
            autoWin.setWinner(bye);
            matches.add(autoWin);
        }
    }

    private void teleportToArena(GameEvent event) {
        Map<String, Object> config = event.getConfig();
        String worldName = String.valueOf(config.getOrDefault("world", "world"));
        double x = parseDouble(config.getOrDefault("arena_x", 0));
        double y = parseDouble(config.getOrDefault("arena_y", 64));
        double z = parseDouble(config.getOrDefault("arena_z", 0));
        double spread = parseDouble(config.getOrDefault("spread", 10));

        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        int index = 0;
        for (Match match : matches) {
            if (match.getPlayers().size() < 2) continue;
            for (UUID playerId : match.getPlayers()) {
                Player player = Bukkit.getPlayer(playerId);
                if (player == null) continue;

                double offsetX = (index % 2 == 0 ? -1 : 1) * spread / 2;
                double offsetZ = (index / 2) * spread;
                Location spawn = new Location(world, x + offsetX, y, z + offsetZ);
                player.teleport(spawn);
                index++;
            }
        }
    }

    private void startTickTask(GameEvent event) {
        tickTask = new BukkitRunnable() {
            @Override
            public void run() {
                onTick(event);
            }
        };
        tickTask.runTaskTimer(plugin, 20L, 20L);
    }

    @Override
    public void onTick(GameEvent event) {
        boolean allResolved = true;
        List<UUID> winners = new ArrayList<>();

        for (Match match : matches) {
            if (match.isResolved()) {
                match.getWinner().ifPresent(winners::add);
                continue;
            }

            List<UUID> alive = match.getPlayers().stream()
                    .filter(id -> !eliminated.contains(id))
                    .filter(id -> {
                        Player p = Bukkit.getPlayer(id);
                        return p != null && p.isOnline() && !p.isDead();
                    })
                    .toList();

            if (alive.size() == 1) {
                match.setWinner(alive.getFirst());
                winners.add(alive.getFirst());
            } else if (alive.isEmpty()) {
                match.setWinner(match.getPlayers().getFirst());
                winners.add(match.getPlayers().getFirst());
            } else {
                allResolved = false;
            }
        }

        if (!allResolved) return;

        if (winners.size() <= 1) {
            EventResult result = EventResult.success();
            if (!winners.isEmpty()) {
                result.setWinner(winners.getFirst());
                List<UUID> remaining = new ArrayList<>(allParticipants);
                remaining.removeAll(eliminated);
                remaining.removeAll(winners);
                if (!remaining.isEmpty()) {
                    result.setRunnerUp(remaining.getFirst());
                }
            }
            onEnd(event, result);
            return;
        }

        currentRound++;
        matches.clear();
        createMatches(winners);
        teleportToArena(event);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("players", String.valueOf(winners.size()));
        replacements.put("round", String.valueOf(currentRound + 1));

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

        distributeRewards(event, result);
        teleportBack();
        cleanup(event);
    }

    private void distributeRewards(GameEvent event, EventResult result) {
        EventRewards rewards = event.getRewards();
        if (rewards == null) return;

        result.getWinner().ifPresent(winnerId -> {
            Player winner = Bukkit.getPlayer(winnerId);
            if (winner == null) return;
            EventRewards.RewardGroup group = rewards.getGroups().get("winner");
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

        result.getRunnerUp().ifPresent(runnerId -> {
            Player runner = Bukkit.getPlayer(runnerId);
            if (runner == null) return;
            EventRewards.RewardGroup group = rewards.getGroups().get("runner_up");
            if (group != null) {
                for (String cmd : group.getCommands()) {
                    plugin.getServer().dispatchCommand(
                            plugin.getServer().getConsoleSender(),
                            cmd.replace("%player%", runner.getName())
                    );
                }
                if (!group.getMessage().isEmpty()) {
                    runner.sendMessage(ChatColor.translateAlternateColorCodes('&', group.getMessage()));
                }
            }
        });

        for (UUID eliminatedId : eliminated) {
            Player player = Bukkit.getPlayer(eliminatedId);
            if (player == null) continue;
            EventRewards.RewardGroup group = rewards.getGroups().get("participation");
            if (group != null) {
                for (String cmd : group.getCommands()) {
                    plugin.getServer().dispatchCommand(
                            plugin.getServer().getConsoleSender(),
                            cmd.replace("%player%", player.getName())
                    );
                }
                if (!group.getMessage().isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', group.getMessage()));
                }
            }
        }
    }

    private void teleportBack() {
        for (Map.Entry<UUID, Location> entry : savedLocations.entrySet()) {
            Player player = Bukkit.getPlayer(entry.getKey());
            if (player != null && player.isOnline()) {
                player.teleport(entry.getValue());
            }
        }
        savedLocations.clear();
    }

    @org.bukkit.event.EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        UUID deadId = event.getEntity().getUniqueId();
        if (!allParticipants.contains(deadId)) return;
        if (eliminated.contains(deadId)) return;

        Player killer = event.getEntity().getKiller();
        if (killer != null && allParticipants.contains(killer.getUniqueId())) {
            eliminated.add(deadId);
        } else {
            eliminated.add(deadId);
        }
    }

    @org.bukkit.event.EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerId = event.getPlayer().getUniqueId();
        if (!allParticipants.contains(playerId)) return;
        if (eliminated.contains(playerId)) return;

        eliminated.add(playerId);
    }

    @Override
    public boolean evaluateCondition(GameEvent event, EventCondition condition) {
        return switch (condition.getType()) {
            case "last_player_standing" -> {
                long alive = allParticipants.stream()
                        .filter(id -> !eliminated.contains(id))
                        .filter(id -> {
                            Player p = Bukkit.getPlayer(id);
                            return p != null && p.isOnline() && !p.isDead();
                        })
                        .count();
                yield alive <= 1;
            }
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
        matches.clear();
        eliminated.clear();
        allParticipants.clear();
        savedLocations.clear();
        currentRound = 0;
    }

    @Override
    public List<UUID> getParticipants(GameEvent event) {
        return List.copyOf(allParticipants);
    }

    private double parseDouble(Object value) {
        if (value instanceof Number num) return num.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public static class Match {
        private final List<UUID> players;
        private UUID winner;

        public Match(List<UUID> players) {
            this.players = new ArrayList<>(players);
        }

        public List<UUID> getPlayers() { return Collections.unmodifiableList(players); }
        public Optional<UUID> getWinner() { return Optional.ofNullable(winner); }
        public void setWinner(UUID winner) { this.winner = winner; }
        public boolean isResolved() { return winner != null; }
    }
}
