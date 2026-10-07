package com.makrozai.eligiusconnector.commands;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class UnlinkCommand implements CommandExecutor {

    private final EligiusConnector plugin;

    public UnlinkCommand(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + plugin.msg("general.not_online"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (!player.hasPermission("connector.unlink.other")) {
                player.sendMessage(ChatColor.RED + plugin.msg(player, "unlink.no_permission"));
                return true;
            }

            String targetName = args[0];
            String playerName = player.getName();
            Scheduler.runAsync(plugin, () -> {
                Long targetDiscordId = plugin.getDatabaseManager().getDiscordIdByName(targetName);
                String key;
                if (targetDiscordId == null) {
                    key = "target_not_found";
                } else if (!plugin.getDatabaseManager().unlinkAccount(targetDiscordId)) {
                    key = "failed";
                } else {
                    key = "success";
                    plugin.getDatabaseManager().logAudit("info", "account_unlinked", playerName,
                            "minecraft", "Unlinked " + targetName, null);
                    plugin.getDiscordManager().sendMessage(plugin.getConfigAdapter().getChatChannelId(),
                            plugin.msg("unlink.unlinked_by").replace("{target}", targetName)
                                    .replace("{player}", playerName));
                }
                sendResult(player, key, targetName, "success".equals(key));
            });
        } else {
            if (!player.hasPermission("connector.unlink.self")) {
                player.sendMessage(ChatColor.RED + plugin.msg(player, "unlink.no_permission"));
                return true;
            }

            java.util.UUID uuid = player.getUniqueId();
            String playerName = player.getName();
            Scheduler.runAsync(plugin, () -> {
                String key;
                if (!plugin.getDatabaseManager().isLinked(uuid)) {
                    key = "not_linked";
                } else if (!plugin.getDatabaseManager().unlinkAccountByUUID(uuid)) {
                    key = "failed";
                } else {
                    key = "success_self";
                    plugin.getDatabaseManager().logAudit("info", "account_unlinked", playerName,
                            "minecraft", "Self unlink", null);
                    plugin.getDiscordManager().sendMessage(plugin.getConfigAdapter().getChatChannelId(),
                            plugin.msg("unlink.unlinked_self").replace("{player}", playerName));
                }
                sendResult(player, key, null, "success_self".equals(key));
            });
        }

        return true;
    }

    private void sendResult(Player player, String key, String target, boolean success) {
        Scheduler.runForEntity(plugin, player, () -> {
            if (!player.isOnline()) return;
            String message = plugin.msg(player, "unlink." + key);
            if (target != null) message = message.replace("{player}", target);
            player.sendMessage((success ? ChatColor.GREEN : ChatColor.RED) + message);
        });
    }
}
