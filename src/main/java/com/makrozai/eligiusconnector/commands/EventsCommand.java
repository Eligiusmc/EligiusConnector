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
            sender.sendMessage(ChatColor.RED + "You don't have permission to use this command.");
            return true;
        }

        if (plugin.getEventManager() == null) {
            sender.sendMessage(ChatColor.RED + "Events module is disabled.");
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
        sender.sendMessage(ChatColor.GOLD + "=== Events ===");
        sender.sendMessage(ChatColor.YELLOW + "/events list" + ChatColor.WHITE + " - List active events");
        sender.sendMessage(ChatColor.YELLOW + "/events start <id>" + ChatColor.WHITE + " - Start an event");
        sender.sendMessage(ChatColor.YELLOW + "/events stop <id>" + ChatColor.WHITE + " - Stop an event");
    }

    private boolean handleList(CommandSender sender) {
        var activeEvents = plugin.getEventManager().getActiveEvents();
        if (activeEvents.isEmpty()) {
            sender.sendMessage(ChatColor.YELLOW + plugin.getConfigAdapter().getEventsNoEvents());
            return true;
        }

        sender.sendMessage(ChatColor.GOLD + plugin.getConfigAdapter().getEventsListTitle());
        for (var entry : activeEvents.entrySet()) {
            GameEvent event = entry.getValue();
            sender.sendMessage(ChatColor.YELLOW + event.getId() + ChatColor.WHITE + " - " + event.getName());
        }
        return true;
    }

    private boolean handleStart(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /events start <id>");
            return true;
        }

        String eventId = args[1];
        GameEvent event = plugin.getEventManager().getEvent(eventId);

        if (event == null) {
            sender.sendMessage(ChatColor.RED + "Event not found: " + eventId);
            return true;
        }

        plugin.getEventManager().startEvent(eventId);
        sender.sendMessage(ChatColor.GREEN + "Started event: " + event.getName());
        return true;
    }

    private boolean handleStop(CommandSender sender, String[] args) {
        if (args.length < 2) {
            sender.sendMessage(ChatColor.RED + "Usage: /events stop <id>");
            return true;
        }

        String eventId = args[1];
        GameEvent event = plugin.getEventManager().getEvent(eventId);

        if (event == null) {
            sender.sendMessage(ChatColor.RED + "Event not found: " + eventId);
            return true;
        }

        plugin.getEventManager().stopEvent(eventId);
        sender.sendMessage(ChatColor.GREEN + "Stopped event: " + event.getName());
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
