package com.makrozai.eligiusconnector.commands;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class VerifyCommand implements CommandExecutor {

    private final EligiusConnector plugin;

    public VerifyCommand(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(plugin.msg(null, "keys.general.not_online"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("connector.verify")) {
            player.sendMessage(plugin.msg(player, "keys.general.no_permission"));
            return true;
        }

        if (plugin.getDatabaseManager().isLinked(player.getUniqueId())) {
            player.sendMessage(plugin.msg(player, "keys.command.verify.already_linked"));
            playSound(player, false);
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.msg(player, "keys.command.verify.code_instruction"));
            return true;
        }

        String code = args[0];

        if (code.length() != plugin.getConfigAdapter().getVerifyCodeLength() || !code.matches("[0-9]+")) {
            player.sendMessage(plugin.msg(player, "keys.command.verify.invalid_format"));
            playSound(player, false);
            return true;
        }

        Long discordId = findDiscordIdByCode(code);
        if (discordId == null) {
            player.sendMessage(plugin.msg(player, "keys.command.verify.invalid_code"));
            playSound(player, false);
            return true;
        }

        if (!plugin.verifyPlayer(discordId, code)) {
            player.sendMessage(plugin.msg(player, "keys.command.verify.invalid_code"));
            playSound(player, false);
            return true;
        }

        if (plugin.getDatabaseManager().linkAccount(discordId, player.getUniqueId(), player.getName(), player.getName())) {
            player.sendMessage(plugin.msg(player, "keys.command.verify.success_mc"));
            playSound(player, true);

            plugin.getDatabaseManager().logAudit("info", "account_linked", player.getName(), "minecraft", "Discord ID: " + discordId, null);

            plugin.getDiscordManager().sendTempMessage(
                    plugin.getConfigAdapter().getVerifyChannelId(),
                    plugin.msg(null, "keys.command.verify.discord_linked").replace("{player}", player.getName()),
                    5);

            // Assign verified role
            plugin.assignVerifiedRole(discordId);

            // Execute post-verify commands
            executePostVerifyCommands(player);
        } else {
            player.sendMessage(plugin.msg(player, "keys.command.verify.link_failed"));
            playSound(player, false);
        }

        return true;
    }

    private void playSound(Player player, boolean success) {
        if (!plugin.getConfigAdapter().isVerifySoundsEnabled()) return;

        try {
            String soundName = success
                    ? plugin.getConfigAdapter().getVerifySuccessSound()
                    : plugin.getConfigAdapter().getVerifyErrorSound();
            float volume = success
                    ? plugin.getConfigAdapter().getVerifySuccessVolume()
                    : plugin.getConfigAdapter().getVerifyErrorVolume();
            float pitch = success
                    ? plugin.getConfigAdapter().getVerifySuccessPitch()
                    : plugin.getConfigAdapter().getVerifyErrorPitch();

            Sound sound = Sound.valueOf(soundName.toUpperCase().replace(".", "_"));
            player.playSound(player.getLocation(), sound, volume, pitch);
        } catch (Exception e) {
            plugin.getLogger().warning("Invalid sound name in verify config: " + e.getMessage());
        }
    }

    private void executePostVerifyCommands(Player player) {
        List<String> commands = plugin.getConfigAdapter().getPostVerifyCommands();
        if (commands == null || commands.isEmpty()) return;

        for (String cmd : commands) {
            String resolved = plugin.applyPlaceholders(player,
                    cmd.replace("{player}", player.getName())
                            .replace("{uuid}", player.getUniqueId().toString()));
            plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), resolved);
        }
    }

    private Long findDiscordIdByCode(String code) {
        for (Long discordId : plugin.getVerifyCodes().keySet()) {
            if (code.equals(plugin.getVerifyCode(discordId))) {
                return discordId;
            }
        }
        return null;
    }
}
