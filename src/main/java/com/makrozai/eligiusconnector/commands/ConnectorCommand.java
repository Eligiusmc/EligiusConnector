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
import java.util.UUID;

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
            case "profile":
                return handleProfile(sender, args);
            case "status":
                return handleStatus(sender);
            case "papi":
                return handlePapi(sender);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + "=== EligiusConnector ===");
        sender.sendMessage(ChatColor.YELLOW + "/connector reload" + ChatColor.WHITE + " - Reload configuration");
        sender.sendMessage(ChatColor.YELLOW + "/connector profile [player]" + ChatColor.WHITE + " - View profile");
        sender.sendMessage(ChatColor.YELLOW + "/connector status" + ChatColor.WHITE + " - View plugin status");
        sender.sendMessage(ChatColor.YELLOW + "/connector papi" + ChatColor.WHITE + " - View placeholders");
    }

    private boolean handleReload(CommandSender sender) {
        if (!sender.hasPermission("connector.reload")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to reload.");
            return true;
        }

        plugin.getConfigAdapter().reloadAll();
        sender.sendMessage(ChatColor.GREEN + "Configuration reloaded!");
        return true;
    }

    private boolean handleProfile(CommandSender sender, String[] args) {
        if (args.length > 1) {
            // View another player's profile (admin only)
            if (!sender.hasPermission("connector.profile.other")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to view other players' profiles.");
                return true;
            }

            String targetName = args[1];
            Player target = plugin.getServer().getPlayer(targetName);

            if (target == null) {
                sender.sendMessage(ChatColor.RED + "Player not found: " + targetName);
                return true;
            }

            showProfile(sender, target);
        } else {
            // View own profile
            if (!(sender instanceof Player)) {
                sender.sendMessage(ChatColor.RED + "This command can only be executed by players.");
                return true;
            }

            Player player = (Player) sender;
            if (!sender.hasPermission("connector.profile.self")) {
                sender.sendMessage(ChatColor.RED + "You don't have permission to view your profile.");
                return true;
            }

            showProfile(sender, player);
        }
        return true;
    }

    private void showProfile(CommandSender sender, Player player) {
        UUID uuid = player.getUniqueId();
        Long discordId = plugin.getDatabaseManager().getDiscordId(uuid);

        sender.sendMessage(ChatColor.GOLD + "=== Profile: " + player.getName() + " ===");
        sender.sendMessage(ChatColor.YELLOW + "UUID: " + ChatColor.WHITE + uuid);
        sender.sendMessage(ChatColor.YELLOW + "Linked: " + ChatColor.WHITE + (discordId != null ? "Yes" : "No"));

        if (discordId != null) {
            sender.sendMessage(ChatColor.YELLOW + "Discord ID: " + ChatColor.WHITE + discordId);
        }

        sender.sendMessage(ChatColor.YELLOW + "Health: " + ChatColor.WHITE + (int) player.getHealth() + "/" + (int) player.getMaxHealth());
        sender.sendMessage(ChatColor.YELLOW + "Food: " + ChatColor.WHITE + player.getFoodLevel());
        sender.sendMessage(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + player.getLevel());
        sender.sendMessage(ChatColor.YELLOW + "World: " + ChatColor.WHITE + player.getWorld().getName());
        sender.sendMessage(ChatColor.YELLOW + "Location: " + ChatColor.WHITE +
                String.format("X: %.1f, Y: %.1f, Z: %.1f",
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ()));
    }

    private boolean handleStatus(CommandSender sender) {
        if (!sender.hasPermission("connector.admin")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to view status.");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "=== EligiusConnector Status ===");
        sender.sendMessage(ChatColor.YELLOW + "Version: " + ChatColor.WHITE + plugin.getDescription().getVersion());
        sender.sendMessage(ChatColor.YELLOW + "Discord: " + ChatColor.WHITE + (plugin.getDiscordManager().isConnected() ? "Connected" : "Disconnected"));
        sender.sendMessage(ChatColor.YELLOW + "Database: " + ChatColor.WHITE + plugin.getDatabaseManager().getType());
        sender.sendMessage(ChatColor.YELLOW + "Linked Accounts: " + ChatColor.WHITE + plugin.getDatabaseManager().getLinkedCount());
        sender.sendMessage(ChatColor.YELLOW + "PlaceholderAPI: " + ChatColor.WHITE + "Enabled");
        return true;
    }

    private boolean handlePapi(CommandSender sender) {
        if (!sender.hasPermission("connector.papi")) {
            sender.sendMessage(ChatColor.RED + "You don't have permission to view placeholders.");
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + "=== PlaceholderAPI Placeholders ===");
        sender.sendMessage(ChatColor.YELLOW + "%connector_linked_<player>%" + ChatColor.WHITE + " - Is player linked");
        sender.sendMessage(ChatColor.YELLOW + "%connector_discord_id_<player>%" + ChatColor.WHITE + " - Discord ID");
        sender.sendMessage(ChatColor.YELLOW + "%connector_discord_name_<player>%" + ChatColor.WHITE + " - Discord name");
        sender.sendMessage(ChatColor.YELLOW + "%connector_minecraft_name_<player>%" + ChatColor.WHITE + " - Minecraft name");
        sender.sendMessage(ChatColor.YELLOW + "%connector_clan_<player>%" + ChatColor.WHITE + " - Player's clan");
        sender.sendMessage(ChatColor.YELLOW + "%connector_clan_tag_<player>%" + ChatColor.WHITE + " - Player's clan tag");
        sender.sendMessage(ChatColor.YELLOW + "%connector_group_<player>%" + ChatColor.WHITE + " - LuckPerms group");
        sender.sendMessage(ChatColor.YELLOW + "%connector_health_<player>%" + ChatColor.WHITE + " - Player health");
        sender.sendMessage(ChatColor.YELLOW + "%connector_food_<player>%" + ChatColor.WHITE + " - Player food");
        sender.sendMessage(ChatColor.YELLOW + "%connector_level_<player>%" + ChatColor.WHITE + " - Player level");
        sender.sendMessage(ChatColor.YELLOW + "%connector_world_<player>%" + ChatColor.WHITE + " - Player world");
        sender.sendMessage(ChatColor.YELLOW + "%connector_x_<player>%" + ChatColor.WHITE + " - Player X");
        sender.sendMessage(ChatColor.YELLOW + "%connector_y_<player>%" + ChatColor.WHITE + " - Player Y");
        sender.sendMessage(ChatColor.YELLOW + "%connector_z_<player>%" + ChatColor.WHITE + " - Player Z");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(Arrays.asList("reload", "profile", "status", "papi"));
        } else if (args.length == 2 && args[0].equalsIgnoreCase("profile")) {
            // Add online player names
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                completions.add(player.getName());
            }
        }

        return completions;
    }
}
