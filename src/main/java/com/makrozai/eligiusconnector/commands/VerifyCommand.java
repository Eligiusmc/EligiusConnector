package com.makrozai.eligiusconnector.commands;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;
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
            sender.sendMessage(plugin.msg(null, "general.not_online"));
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("connector.verify")) {
            player.sendMessage(plugin.msg(player, "general.no_permission"));
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(plugin.msg(player, "verify.code_instruction"));
            return true;
        }

        // Issue #14: aceptar minúsculas, los jugadores las escriben naturalmente
        String code = args[0].toUpperCase(java.util.Locale.ROOT);

        if (code.length() != 6 || !code.matches("[A-Z0-9]+")) {
            player.sendMessage(plugin.msg(player, "verify.invalid_format"));
            playSound(player, false);
            return true;
        }

        java.util.UUID uuid = player.getUniqueId();
        String playerName = player.getName();
        Scheduler.runAsync(plugin, () -> {
            String result;
            Long discordId = findDiscordIdByCode(code);
            if (plugin.getDatabaseManager().isLinked(uuid)) {
                result = "already_linked";
            } else if (discordId == null || !plugin.claimVerifyCode(discordId, code)) {
                result = "invalid_code";
            } else if (plugin.getDatabaseManager().linkAccount(discordId, uuid, playerName, playerName)) {
                plugin.consumeVerifyCode(discordId);
                plugin.getDatabaseManager().logAudit("info", "account_linked", playerName, "minecraft",
                        "Discord ID: " + discordId, null);
                plugin.getDiscordManager().sendDmEmbed(discordId,
                        plugin.getConfigAdapter().getVerifyDmEmbed(), java.util.Map.of("player", playerName));
                result = "success_mc";
            } else {
                plugin.releaseVerifyCode(discordId);
                result = "link_failed";
            }

            String messageKey = result;
            Scheduler.runForEntity(plugin, player, () -> {
                if (!player.isOnline()) return;
                player.sendMessage(plugin.msg(player, "verify." + messageKey));
                playSound(player, "success_mc".equals(messageKey));
                if ("success_mc".equals(messageKey)) {
                    plugin.syncRolesForPlayer(player);
                    executePostVerifyCommands(player);
                }
            });
        });

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
            Scheduler.runSync(plugin, () ->
                    plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), resolved));
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
