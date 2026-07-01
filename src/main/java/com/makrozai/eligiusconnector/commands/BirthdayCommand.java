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
            sender.sendMessage(plugin.msg(null, "keys.command.birthday.disabled"));
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg(null, "keys.general.not_online"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("connector.birthday")) {
            player.sendMessage(plugin.msg(player, "keys.general.no_permission"));
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
        player.sendMessage(plugin.msg(player, "keys.command.birthday.help_title"));
        player.sendMessage(plugin.msg(player, "keys.command.birthday.help_set"));
        player.sendMessage(plugin.msg(player, "keys.command.birthday.help_check"));
    }

    private boolean handleSet(Player player, String[] args) {
        if (args.length < 2) {
            player.sendMessage(plugin.msg(player, "keys.command.birthday.usage"));
            return true;
        }

        String date = args[1];
        if (!date.matches("\\d{2}/\\d{2}/\\d{4}")) {
            player.sendMessage(plugin.msg(player, "keys.command.birthday.invalid_format"));
            return true;
        }

        Long discordId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());
        if (discordId == null) {
            player.sendMessage(plugin.msg(player, "keys.command.birthday.not_linked"));
            return true;
        }

        if (plugin.getDatabaseManager().setBirthday(discordId, date)) {
            player.sendMessage(plugin.getConfigAdapter().getBirthdaySuccess());
        } else {
            player.sendMessage(plugin.msg(player, "keys.command.birthday.save_failed"));
        }
        return true;
    }

    private boolean handleCheck(Player player) {
        Long discordId = plugin.getDatabaseManager().getDiscordId(player.getUniqueId());
        if (discordId == null) {
            player.sendMessage(plugin.msg(player, "keys.command.birthday.not_linked"));
            return true;
        }

        String birthday = plugin.getDatabaseManager().getBirthday(discordId);
        if (birthday != null && !birthday.isEmpty()) {
            player.sendMessage(plugin.msg(player, "keys.command.birthday.your_birthday").replace("{date}", birthday));
        } else {
            player.sendMessage(plugin.msg(player, "keys.command.birthday.not_set"));
        }
        return true;
    }
}
