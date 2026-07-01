package com.makrozai.eligiusconnector.commands;

import com.makrozai.eligiusconnector.EligiusConnector;
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
            sender.sendMessage(ChatColor.RED + plugin.msg("keys.general.not_online"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            if (!player.hasPermission("connector.unlink.other")) {
                player.sendMessage(ChatColor.RED + plugin.msg(player, "keys.command.unlink.no_permission"));
                return true;
            }

            String targetName = args[0];
            Long targetDiscordId = plugin.getDatabaseManager().getDiscordIdByName(targetName);

            if (targetDiscordId == null) {
                player.sendMessage(ChatColor.RED + plugin.msg(player, "keys.command.unlink.target_not_found").replace("{player}", targetName));
                return true;
            }

            if (!plugin.getDatabaseManager().isLinkedByName(targetName)) {
                player.sendMessage(ChatColor.RED + plugin.msg(player, "keys.command.unlink.target_not_linked").replace("{player}", targetName));
                return true;
            }

            if (plugin.getDatabaseManager().unlinkAccount(targetDiscordId)) {
                player.sendMessage(ChatColor.GREEN + plugin.msg(player, "keys.command.unlink.success").replace("{player}", targetName));
                plugin.getDatabaseManager().logAudit("info", "account_unlinked", player.getName(), "minecraft", "Unlinked " + targetName, null);
                plugin.getDiscordManager().sendMessage(
                        plugin.getConfigAdapter().getGlobalChannelId(),
                        plugin.msg("keys.command.unlink.unlinked_by").replace("{target}", targetName).replace("{player}", player.getName()));
            } else {
                player.sendMessage(ChatColor.RED + plugin.msg(player, "keys.command.unlink.failed").replace("{player}", targetName));
            }
        } else {
            if (!player.hasPermission("connector.unlink.self")) {
                player.sendMessage(ChatColor.RED + plugin.msg(player, "keys.command.unlink.no_permission"));
                return true;
            }

            if (!plugin.getDatabaseManager().isLinked(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + plugin.msg(player, "keys.command.unlink.not_linked"));
                return true;
            }

            if (plugin.getDatabaseManager().unlinkAccountByUUID(player.getUniqueId())) {
                player.sendMessage(ChatColor.GREEN + plugin.msg(player, "keys.command.unlink.success_self"));
                plugin.getDatabaseManager().logAudit("info", "account_unlinked", player.getName(), "minecraft", "Self unlink", null);
                plugin.getDiscordManager().sendMessage(
                        plugin.getConfigAdapter().getGlobalChannelId(),
                        plugin.msg("keys.command.unlink.unlinked_self").replace("{player}", player.getName()));
            } else {
                player.sendMessage(ChatColor.RED + plugin.msg(player, "keys.command.unlink.failed"));
            }
        }

        return true;
    }
}
