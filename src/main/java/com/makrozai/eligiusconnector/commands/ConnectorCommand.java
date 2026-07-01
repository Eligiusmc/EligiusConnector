package com.makrozai.eligiusconnector.commands;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConnectorCommand implements CommandExecutor, TabCompleter {

    private final EligiusConnector plugin;

    public ConnectorCommand(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "reload":
                return handleReload(sender);
            case "status":
                return handleStatus(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + plugin.msg("keys.command.connector.help_title"));
        sender.sendMessage(ChatColor.YELLOW + plugin.msg("keys.command.connector.help_reload"));
        sender.sendMessage(ChatColor.YELLOW + plugin.msg("keys.command.connector.help_status"));
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("connector.reload")) {
            sender.sendMessage(ChatColor.RED + plugin.msg("keys.command.connector.no_permission_reload"));
            return true;
        }

        plugin.getConfigAdapter().reloadAll();
        plugin.getLanguageManager().reload();
        plugin.getPanelManager().reloadPanels();
        sender.sendMessage(ChatColor.GREEN + plugin.msg("keys.command.connector.reload_success"));
        return true;
    }

    private boolean handleStatus(CommandSender sender) {
        if (!sender.hasPermission("connector.admin")) {
            sender.sendMessage(ChatColor.RED + plugin.msg("keys.command.connector.no_permission_status"));
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + plugin.msg("keys.command.connector.status_title"));
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + plugin.msg("keys.command.connector.status_discord")
                .replace("{status}", plugin.getDiscordManager().isConnected() ? "Connected" : "Disconnected"));
        sender.sendMessage(ChatColor.YELLOW + plugin.msg("keys.command.connector.status_database")
                .replace("{type}", plugin.getDatabaseManager().getType()));
        sender.sendMessage(ChatColor.YELLOW + plugin.msg("keys.command.connector.status_linked")
                .replace("{count}", String.valueOf(plugin.getDatabaseManager().getLinkedCount())));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if (args.length == 1) {
            completions.addAll(Arrays.asList("reload", "status"));
        }
        return completions;
    }
}
