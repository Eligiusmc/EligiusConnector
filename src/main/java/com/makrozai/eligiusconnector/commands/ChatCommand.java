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
        if (!plugin.getConfigAdapter().isChatEnabled() || !plugin.getConfigAdapter().isMcToDiscordEnabled()) {
            sender.sendMessage(plugin.msg(null, "chat.disabled_module"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg(null, "general.not_online"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("connector.chat")) {
            player.sendMessage(plugin.msg(player, "general.no_permission"));
            return true;
        }

        if (args.length == 0) {
            player.sendMessage(plugin.msg(player, "chat.usage"));
            return true;
        }

        String message = String.join(" ", args);
        if (plugin.getChatFilterEngine().filter(message, player).blocked()) return true;
        String avatarUrl = plugin.applyPlaceholders(player,
                plugin.getConfigAdapter().getWebhookAvatar()
                        .replace("{player}", player.getName())
                        .replace("{player_name}", player.getName()));

        plugin.getDiscordManager().sendChatMessage(player.getName(), message, avatarUrl);
        player.sendMessage(ChatColor.GREEN + "[DC] " + player.getName() + ": " + message);

        return true;
    }
}
