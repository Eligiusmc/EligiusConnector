package com.makrozai.eligiusconnector.commands;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class VerifyCommand implements CommandExecutor {

    private final EligiusConnector plugin;
    private final Map<String, Long> codeExpiration = new HashMap<>();

    public VerifyCommand(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("connector.verify")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {
            player.sendMessage(ChatColor.RED + "Usage: /verify <code>");
            return true;
        }

        String code = args[0];

        // Check if code is valid format
        if (code.length() != 6 || !code.matches("[A-Z0-9]+")) {
            player.sendMessage(ChatColor.RED + "Invalid code format. Code must be 6 alphanumeric characters.");
            return true;
        }

        // Check if code exists and is not expired
        if (!codeExpiration.containsKey(code)) {
            player.sendMessage(ChatColor.RED + "Invalid code. Please generate a new one on Discord.");
            return true;
        }

        Long expiration = codeExpiration.get(code);
        if (System.currentTimeMillis() > expiration) {
            codeExpiration.remove(code);
            player.sendMessage(ChatColor.RED + "Code expired. Please generate a new one on Discord.");
            return true;
        }

        // Check if player is already linked
        if (plugin.getDatabaseManager().isLinked(player.getUniqueId())) {
            player.sendMessage(ChatColor.RED + "Your account is already linked. Use /unlink first.");
            return true;
        }

        // TODO: Get Discord ID from code (requires Discord bot integration)
        // For now, simulate linking
        long discordId = 123456789L; // This would come from Discord

        // Link account
        if (plugin.getDatabaseManager().linkAccount(discordId, player.getUniqueId(), player.getName(), player.getName())) {
            player.sendMessage(ChatColor.GREEN + "Account linked successfully!");
            codeExpiration.remove(code);

            // Log to audit
            plugin.getDatabaseManager().logAudit(
                    "info",
                    "account_linked",
                    player.getName(),
                    "minecraft",
                    "Discord ID: " + discordId,
                    null
            );

            // Send to Discord
            plugin.getDiscordManager().sendGlobalMessage(":white_check_mark: **" + player.getName() + "** has linked their Minecraft account!");
        } else {
            player.sendMessage(ChatColor.RED + "Failed to link account. Please try again.");
        }

        return true;
    }

    public void addCode(String code, long expirationMs) {
        codeExpiration.put(code, System.currentTimeMillis() + expirationMs);
    }

    public void removeCode(String code) {
        codeExpiration.remove(code);
    }
}
