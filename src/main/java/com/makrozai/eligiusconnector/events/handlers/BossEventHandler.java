package com.makrozai.eligiusconnector.events.handlers;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.events.EventCondition;
import com.makrozai.eligiusconnector.events.EventResult;
import com.makrozai.eligiusconnector.events.EventRewards;
import com.makrozai.eligiusconnector.events.GameEvent;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BossEventHandler implements com.makrozai.eligiusconnector.events.EventHandler, Listener {

    private final EligiusConnector plugin;
    private int mobEntityId = -1;
    private UUID bossUUID;
    private BossBar bossBar;
    private final Map<UUID, Integer> damageMap = new ConcurrentHashMap<>();
    private final List<UUID> participants = new CopyOnWriteArrayList<>();
    private BukkitRunnable tickTask;

    public BossEventHandler(EligiusConnector plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public void onStart(GameEvent event) {
        Map<String, Object> spawnConfig = event.getSpawnConfig();
        Map<String, Object> config = event.getConfig();

        String mobType = String.valueOf(spawnConfig.getOrDefault("mob", "WITHER"));
        String customName = String.valueOf(spawnConfig.getOrDefault("custom_name", "Boss"));
        double health = parseDouble(spawnConfig.getOrDefault("health", 300));
        String worldName = String.valueOf(config.getOrDefault("world", "world"));
        double x = parseDouble(config.getOrDefault("x", 0));
        double y = parseDouble(config.getOrDefault("y", 64));
        double z = parseDouble(config.getOrDefault("z", 0));

        World world = Bukkit.getWorld(worldName);
        if (world == null) return;

        Location spawnLoc = new Location(world, x, y, z);
        EntityType entityType = parseEntityType(mobType);

        LivingEntity boss = (LivingEntity) world.spawnEntity(spawnLoc, entityType);
        boss.setCustomName(ChatColor.RED + "" + ChatColor.BOLD + customName);
        boss.setCustomNameVisible(true);

        if (boss.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null) {
            boss.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
            boss.setHealth(health);
        }

        mobEntityId = boss.getEntityId();
        bossUUID = boss.getUniqueId();
        damageMap.clear();
        participants.clear();

        bossBar = Bukkit.createBossBar(
                ChatColor.RED + customName,
                BarColor.RED,
                BarStyle.SEGMENTED_20,
                BarFlag.CREATE_FOG
        );
        bossBar.setProgress(1.0);

        for (Player player : world.getPlayers()) {
            bossBar.addPlayer(player);
        }

        startTickTask(event);
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
        Entity boss = getBossEntity();
        if (boss == null || !boss.isValid()) {
            tickTask.cancel();
            return;
        }

        LivingEntity livingBoss = (LivingEntity) boss;
        double maxHealth = livingBoss.getAttribute(Attribute.GENERIC_MAX_HEALTH) != null
                ? livingBoss.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue()
                : 300;
        double currentHealth = livingBoss.getHealth();
        bossBar.setProgress(Math.max(0, currentHealth / maxHealth));

        String title = ChatColor.RED + "" + ChatColor.BOLD + String.valueOf(
                event.getSpawnConfig().getOrDefault("custom_name", "Boss")
        ) + ChatColor.GRAY + " - " + ChatColor.WHITE +
                String.format("%.0f", currentHealth) + "/" + String.format("%.0f", maxHealth) + " HP";
        bossBar.setTitle(title);

        Map<String, String> replacements = new HashMap<>();
        replacements.put("health", String.format("%.0f", currentHealth));
        replacements.put("max_health", String.format("%.0f", maxHealth));
        replacements.put("participants", String.valueOf(participants.size()));

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

        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }

        Entity boss = getBossEntity();
        if (boss != null && boss.isValid()) {
            boss.remove();
        }

        distributeRewards(event, result);
        cleanup(event);
    }

    private void distributeRewards(GameEvent event, EventResult result) {
        EventRewards rewards = event.getRewards();
        if (rewards == null) return;

        Map<UUID, Integer> sorted = damageMap.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .collect(HashMap::new, (m, e) -> m.put(e.getKey(), e.getValue()), HashMap::putAll);

        int rank = 0;
        for (UUID playerId : sorted.keySet()) {
            Player player = Bukkit.getPlayer(playerId);
            if (player == null || !player.isOnline()) continue;

            rank++;
            String group = switch (rank) {
                case 1 -> "winner";
                case 2 -> "runner_up";
                case 3 -> "third_place";
                default -> "participation";
            };

            EventRewards.RewardGroup rewardGroup = rewards.getGroups().get(group);
            if (rewardGroup != null) {
                for (String cmd : rewardGroup.getCommands()) {
                    String formatted = cmd.replace("%player%", player.getName());
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), formatted);
                }
                if (!rewardGroup.getMessage().isEmpty()) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', rewardGroup.getMessage()));
                }
            }
        }
    }

    @org.bukkit.event.EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player && event.getEntity().getEntityId() == mobEntityId) {
            if (!participants.contains(player.getUniqueId())) {
                participants.add(player.getUniqueId());
            }
            damageMap.merge(player.getUniqueId(), (int) event.getFinalDamage(), Integer::sum);
        }
    }

    @org.bukkit.event.EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getEntityId() == mobEntityId) {
            mobEntityId = -1;
            bossUUID = null;

            Player killer = event.getEntity().getKiller();
            if (killer != null) {
                if (!participants.contains(killer.getUniqueId())) {
                    participants.add(killer.getUniqueId());
                }
            }
        }
    }

    @Override
    public boolean evaluateCondition(GameEvent event, EventCondition condition) {
        return switch (condition.getType()) {
            case "mob_killed" -> mobEntityId == -1;
            case "timeout" -> {
                long elapsed = System.currentTimeMillis() - event.getStartTime();
                yield elapsed >= condition.getMinutes() * 60_000L;
            }
            case "all_players_dead" -> {
                Entity boss = getBossEntity();
                if (boss == null) yield true;
                World world = boss.getWorld();
                long alive = world.getPlayers().stream()
                        .filter(p -> p.getGameMode() != GameMode.CREATIVE)
                        .filter(p -> !p.isDead())
                        .count();
                yield alive == 0;
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
        if (bossBar != null) {
            bossBar.removeAll();
            bossBar = null;
        }
        damageMap.clear();
        participants.clear();
        mobEntityId = -1;
        bossUUID = null;
    }

    @Override
    public List<UUID> getParticipants(GameEvent event) {
        return List.copyOf(participants);
    }

    private Entity getBossEntity() {
        if (bossUUID == null) return null;
        return Bukkit.getEntity(bossUUID);
    }

    private EntityType parseEntityType(String name) {
        try {
            return EntityType.valueOf(name.toUpperCase().replace(" ", "_"));
        } catch (IllegalArgumentException e) {
            return EntityType.WITHER;
        }
    }

    private double parseDouble(Object value) {
        if (value instanceof Number num) return num.doubleValue();
        try {
            return Double.parseDouble(String.valueOf(value));
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
