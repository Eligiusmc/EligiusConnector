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
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            // Unlink another player (admin only)
            if (!player.hasPermission("connector.unlink.other")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to unlink other players.");
                return true;
            }

            String targetName = args[0];
            Player target = plugin.getServer().getPlayer(targetName);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "Player not found: " + targetName);
                return true;
            }

            if (!plugin.getDatabaseManager().isLinked(target.getUniqueId())) {
                player.sendMessage(ChatColor.RED + targetName + "'s account is not linked.");
                return true;
            }

            if (plugin.getDatabaseManager().unlinkAccountByUUID(target.getUniqueId())) {
                player.sendMessage(ChatColor.GREEN + "Successfully unlinked " + targetName + "'s account.");

                // Log to audit
                plugin.getDatabaseManager().logAudit(
                        "info",
                        "account_unlinked",
                        player.getName(),
                        "minecraft",
                        "Unlinked " + targetName,
                        null
                );

                // Send to Discord
                plugin.getDiscordManager().sendGlobalMessage(":x: **" + targetName + "**'s account has been unlinked by **" + player.getName() + "**.");
            } else {
                player.sendMessage(ChatColor.RED + "Failed to unlink " + targetName + "'s account.");
            }
        } else {
            // Unlink self
            if (!player.hasPermission("connector.unlink.self")) {
                player.sendMessage(ChatColor.RED + "You don't have permission to unlink your account.");
                return true;
            }

            if (!plugin.getDatabaseManager().isLinked(player.getUniqueId())) {
                player.sendMessage(ChatColor.RED + "Your account is not linked.");
                return true;
            }

            if (plugin.getDatabaseManager().unlinkAccountByUUID(player.getUniqueId())) {
                player.sendMessage(ChatColor.GREEN + "Account unlinked successfully!");

                // Log to audit
                plugin.getDatabaseManager().logAudit(
                        "info",
                        "account_unlinked",
                        player.getName(),
                        "minecraft",
                        "Self unlink",
                        null
                );

                // Send to Discord
                plugin.getDiscordManager().sendGlobalMessage(":x: **" + player.getName() + "** has unlinked their Minecraft account.");
            } else {
                player.sendMessage(ChatColor.RED + "Failed to unlink account.");
            }
        }

        return true;
    }
}
