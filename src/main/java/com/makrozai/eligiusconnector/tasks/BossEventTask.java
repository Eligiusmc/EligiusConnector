package com.makrozai.eligiusconnector.tasks;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class BossEventTask {

    private final EligiusConnector plugin;
    private BukkitTask task;
    private long lastSpawnTime = 0;
    private final Random random = new Random();

    public BossEventTask(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void start() {
        long intervalTicks = plugin.getConfigAdapter().getBossRespawnMinutes() * 60L * 20L;
        task = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            long now = System.currentTimeMillis();
            if (now - lastSpawnTime < plugin.getConfigAdapter().getBossRespawnMinutes() * 60L * 1000L) {
                return;
            }
            Bukkit.getScheduler().runTask(plugin, this::spawnBoss);
        }, intervalTicks, intervalTicks);
    }

    private void spawnBoss() {
        List<String> commands = plugin.getConfigAdapter().getBossCommands();
        if (commands.isEmpty()) return;

        Player randomPlayer = null;
        for (Player p : Bukkit.getOnlinePlayers()) {
            randomPlayer = p;
            break;
        }
        if (randomPlayer == null) return;

        Location loc = randomPlayer.getLocation().add(
                random.nextDouble() * 100 - 50, 0, random.nextDouble() * 100 - 50);

        String command = commands.get(random.nextInt(commands.size()));
        command = command.replace("~ ~ ~",
                loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ());

        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);

        String bossName = extractBossName(command);
        Map<String, String> replacements = new HashMap<>();
        replacements.put("boss", bossName);
        replacements.put("x", String.valueOf(loc.getBlockX()));
        replacements.put("y", String.valueOf(loc.getBlockY()));
        replacements.put("z", String.valueOf(loc.getBlockZ()));

        // Send boss spawn embed
        plugin.getDiscordManager().sendEmbed(
                plugin.getConfigAdapter().getBossEventsChannelId(),
                plugin.getConfigAdapter().getBossSpawnEmbed(),
                replacements
        );
        lastSpawnTime = System.currentTimeMillis();
    }

    public void onBossDeath(String bossName, Player killer) {
        Map<String, String> replacements = new HashMap<>();
        replacements.put("boss", bossName);
        replacements.put("player", killer != null ? killer.getName() : "Unknown");
        plugin.getDiscordManager().sendEmbed(
                plugin.getConfigAdapter().getBossEventsChannelId(),
                plugin.getConfigAdapter().getBossDeathEmbed(),
                replacements
        );
    }

    private String extractBossName(String command) {
        if (command.contains("skeleton")) return "Shadow Knight";
        if (command.contains("zombie")) return "Ancient Golem";
        if (command.contains("spider")) return "Nether Spider";
        if (command.contains("creeper")) return "Storm Creeper";
        return "Unknown Boss";
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }
}
