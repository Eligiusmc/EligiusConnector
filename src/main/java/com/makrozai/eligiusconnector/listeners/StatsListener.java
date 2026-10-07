package com.makrozai.eligiusconnector.listeners;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;

import java.util.UUID;

public class StatsListener implements Listener {

    private final EligiusConnector plugin;

    public StatsListener(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID victim = player.getUniqueId();
        Scheduler.runAsync(plugin, () -> plugin.getStatsManager().addDeath(victim, "player"));

        if (player.getKiller() != null) {
            UUID killer = player.getKiller().getUniqueId();
            Scheduler.runAsync(plugin, () -> plugin.getStatsManager().addKill(killer, "player"));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity().getKiller() == null) return;
        if (event.getEntity() instanceof Player) return;

        UUID killer = event.getEntity().getKiller().getUniqueId();
        Scheduler.runAsync(plugin, () -> plugin.getStatsManager().addKill(killer, "mob"));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Scheduler.runAsync(plugin, () -> plugin.getStatsManager().addBlockBroken(uuid));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent event) {
        UUID uuid = event.getPlayer().getUniqueId();
        Scheduler.runAsync(plugin, () -> plugin.getStatsManager().addBlockPlaced(uuid));
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onCraft(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player player) {
            UUID uuid = player.getUniqueId();
            Scheduler.runAsync(plugin, () -> plugin.getStatsManager().addItemCrafted(uuid));
        }
    }
}
