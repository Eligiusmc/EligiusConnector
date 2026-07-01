package com.makrozai.eligiusconnector.commands;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.events.GameEvent;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class EventsCommand implements CommandExecutor, TabCompleter {

    private final EligiusConnector plugin;

    public EventsCommand(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("connector.events")) {
            sender.sendMessage(ChatColor.RED + plugin.msg("keys.general.no_permission"));
            return true;
        }

        if (plugin.getEventManager() == null) {
            sender.sendMessage(ChatColor.RED + plugin.msg("keys.command.events.disabled"));
            return true;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return true;
        }

        String subCommand = args[0].toLowerCase();

        switch (subCommand) {
            case "list":
                return handleList(sender);
            case "start":
                return handleStart(sender, args);
            case "stop":
                return handleStop(sender, args);
            default:
                sendHelp(sender);
                return true;
        }
    }

    private void sendHelp(CommandSender sender) {
        sender.sendMessage(ChatColor.GOLD + plugin.msg("keys.command.events.list_title"));
        sender.sendMessage(ChatColor.YELLOW + "/events list - " + plugin.msg("keys.command.events.list_title"));
        sender.sendMessage(ChatColor.YELLOW + "/events start <id> - " + plugin.msg("keys.command.events.started").replace("{event}", ""));
        sender.sendMessage(ChatColor.YELLOW + "/events stop <id> - " + plugin.msg("keys.command.events.stopped").replace("{event}", ""));
    }

    private boolean handleList(CommandSender sender) {
        var activeEvents = plugin.getEventManager().getActiveEvents();
        if (activeEvents.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + plugin.msg("keys.command.events.no_events"));
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + plugin.msg("keys.command.events.list_title"));
        for (var entry : activeEvents.entrySet()) {
            GameEvent event = entry.getValue();
            sender.sendMessage(ChatColor.YELLOW + event.getId() + ChatColor.WHITE + " - " + event.getName());
        }
        return true;
    }

    private boolean handleStart(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + plugin.msg("keys.command.events.usage_start"));
            return true;
        }

        String eventId = args[1];
        GameEvent event = plugin.getEventManager().getEvent(eventId);

        if (event == null) {
            sender.sendMessage(ChatColor.RED + plugin.msg("keys.command.events.not_found").replace("{name}", eventId));
            return true;
        }

        plugin.getEventManager().startEvent(eventId);
        sender.sendMessage(ChatColor.GREEN + plugin.msg("keys.command.events.started").replace("{name}", event.getName()));
        return true;
    }

    private boolean handleStop(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + plugin.msg("keys.command.events.usage_stop"));
            return true;
        }

        String eventId = args[1];
        GameEvent event = plugin.getEventManager().getEvent(eventId);

        if (event == null) {
            sender.sendMessage(ChatColor.RED + plugin.msg("keys.command.events.not_found").replace("{name}", eventId));
            return true;
        }

        plugin.getEventManager().stopEvent(eventId);
        sender.sendMessage(ChatColor.RED + plugin.msg("keys.command.events.stopped").replace("{name}", event.getName()));
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(List.of("list", "start", "stop"));
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("start") || args[0].equalsIgnoreCase("stop"))) {
            if (plugin.getEventManager() != null) {
                for (var event : plugin.getEventManager().getAllEvents()) {
                    completions.add(event.getId());
                }
            }
        }

        return completions;
    }
}
