package com.makrozai.eligiusconnector.commands;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class BirthdayCommand implements CommandExecutor {

    private final EligiusConnector plugin;

    public BirthdayCommand(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!plugin.getConfigAdapter().isBirthdayEnabled()) {
            sender.sendMessage(ChatColor.RED + "Birthday module is disabled.");
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("connector.birthday")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length == 0) {
            sendHelp(player);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "set":
                return handleSet(player, args);
            case "check":
                return handleCheck(player);
            default:
                sendHelp(player);
                return true;
        }
    }

    private void sendHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== Birthday ===");
        player.sendMessage(ChatColor.YELLOW + "/birthday set <dd/MM/yyyy>" + ChatColor.WHITE + " - Set your birthday");
        player.sendMessage(ChatColor.YELLOW + "/birthday check" + ChatColor.WHITE + " - Check your birthday");
    }

    private boolean handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(ChatColor.RED + "Usage: /birthday set <dd/MM/yyyy>");
            return true;
        }

        String date = args[1];
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            player.sendMessage(ChatColor.RED + "Invalid date format. Use dd/MM/yyyy");
            return true;
        }

        Long discordId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());
        if (discordId == null) {
            player.sendMessage(ChatColor.RED + "You need to link your Discord account first. Use /verify");
            return true;
        }

        if (plugin.getDatabaseManager().setBirthday(discordId, date)) {
            player.sendMessage(plugin.getConfigAdapter().getBirthdaySuccess());
        } else {
            player.sendMessage(ChatColor.RED + "Failed to save birthday.");
        }
        return true;
    }

    private boolean handleCheck(Player player) {
        Long discordId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());
        if (discordId == null) {
            player.sendMessage(ChatColor.RED + "You need to link your Discord account first. Use /verify");
            return true;
        }

        String birthday = plugin.getDatabaseManager().getBirthday(discordId);
        if (birthday != null && !birthday.isEmpty()) {
            player.sendMessage(ChatColor.GREEN + "Your birthday: " + birthday);
        } else {
            player.sendMessage(ChatColor.YELLOW + "You haven't set your birthday yet.");
        }
        return true;
    }
}
