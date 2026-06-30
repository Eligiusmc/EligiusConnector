package com.makrozai.eligiusconnector.commands;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ChatCommand implements CommandExecutor {

    private final EligiusConnector plugin;

    public ChatCommand(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigAdapter().isChatEnabled()) {
            sender.sendMessage(ChatColor.RED + "Chat bridge module is disabled.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("connector.chat")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(ChatColor.RED + "Usage: /chat <message>");
            return true;
        }

        String message = String.join(" ", args);
        String avatarUrl = plugin.getConfigAdapter().getWebhookAvatar()
                .replace("{player}", player.getName())
                .replace("{player_name}", player.getName());

        plugin.getDiscordManager().sendChatMessage(player.getName(), message, avatarUrl);
        player.sendMessage(ChatColor.GREEN + "[DC] " + player.getName() + ": " + message);

        return true;
    }
}
