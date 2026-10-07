package com.makrozai.eligiusconnector.tasks;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.scheduler.BukkitTask;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RoleSyncTask {

    private final EligiusConnector plugin;
    private BukkitTask task;
    private Permission perms;

    public RoleSyncTask(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void start() {
        RegisteredServiceProvider<Permission> rsp = Bukkit.getServicesManager().getRegistration(Permission.class);
        if (rsp == null) {
            plugin.getLogger().warning("[RoleSync] No Vault permission provider found, role sync disabled");
            return;
        }
        perms = rsp.getProvider();

        task = Scheduler.runTimerAsync(plugin, this::syncAll, 20L,
                plugin.getConfigAdapter().getRoleSyncInterval() * 20L);
        plugin.getLogger().info("[RoleSync] Started (interval: " + plugin.getConfigAdapter().getRoleSyncInterval() + "s)");
    }

    public void stop() {
        if (task != null) {
            task.cancel();
            task = null;
        }
    }

    public void syncAll() {
        if (!plugin.getDiscordManager().isConnected()) return;
        if (perms == null) return;
        Map<String, String> mapping = plugin.getConfigAdapter().getRoleGroupMapping();
        if (mapping.isEmpty()) return;

        Guild guild = plugin.getDiscordManager().getGuild();
        if (guild == null) return;

        Scheduler.runSync(plugin, () -> Bukkit.getOnlinePlayers().forEach(player ->
                Scheduler.runForEntity(plugin, player, () -> syncPlayer(player, guild, mapping))));
    }

    public void syncPlayer(Player player) {
        if (!plugin.getDiscordManager().isConnected()) return;
        if (perms == null) return;
        Map<String, String> mapping = plugin.getConfigAdapter().getRoleGroupMapping();
        if (mapping.isEmpty()) return;

        Scheduler.runForEntity(plugin, player, () -> {
            Guild guild = plugin.getDiscordManager().getGuild();
            if (guild != null) syncPlayer(player, guild, mapping);
        });
    }

    private void syncPlayer(Player player, Guild guild, Map<String, String> mapping) {
        Set<String> mcGroups = new HashSet<>(Arrays.asList(perms.getPlayerGroups(player)));
        java.util.UUID uuid = player.getUniqueId();
        Scheduler.runAsync(plugin, () -> {
            Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);
            if (discordId == null) return;
            guild.retrieveMemberById(discordId).queue(member -> {
                Set<String> dcRoleIds = new HashSet<>();
                for (Role role : member.getRoles()) {
                    dcRoleIds.add(role.getId());
                }

                for (var entry : mapping.entrySet()) {
                    String mcGroup = entry.getKey();
                    String dcRoleId = entry.getValue();
                    Role role = guild.getRoleById(dcRoleId);
                    if (role == null) continue;

                    boolean hasInMC = mcGroups.contains(mcGroup);
                    boolean hasInDC = dcRoleIds.contains(dcRoleId);

                    if (hasInMC && !hasInDC) {
                        guild.addRoleToMember(member, role).queue(
                                s -> plugin.getLogger().info("[RoleSync] +" + mcGroup + " → " + member.getUser().getName()),
                                e -> plugin.getLogger().warning("[RoleSync] Failed to add " + mcGroup + ": " + e.getMessage())
                        );
                    } else if (!hasInMC && hasInDC) {
                        guild.removeRoleFromMember(member, role).queue(
                                s -> plugin.getLogger().info("[RoleSync] -" + mcGroup + " → " + member.getUser().getName()),
                                e -> plugin.getLogger().warning("[RoleSync] Failed to remove " + mcGroup + ": " + e.getMessage())
                        );
                    }
                }
            }, error -> {}); // ponytail: member not in guild, skip silently
        });
    }
}
