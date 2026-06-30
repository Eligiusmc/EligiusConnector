package com.makrozai.eligiusconnector.placeholders;

import com.makrozai.eligiusconnector.EligiusConnector;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PlaceholderAPIManager extends PlaceholderExpansion {

    private final EligiusConnector plugin;
    private final Map<String, String> placeholderCache = new ConcurrentHashMap<>();
    private long lastCacheClear = 0;

    public PlaceholderAPIManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getIdentifier() {
        return "connector";
    }

    @Override
    public String getAuthor() {
        return "makrozai";
    }

    @Override
    public String getVersion() {
        return plugin.getDescription().getVersion();
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if (player == null) {
            return "";
        }

        String cacheKey = player.getUniqueId() + ":" + identifier;

        String result = "";

        // Handle placeholders
        if (identifier.startsWith("linked_")) {
            String targetName = identifier.substring(7);
            result = handleLinked(targetName);
        } else if (identifier.startsWith("discord_id_")) {
            String targetName = identifier.substring(11);
            result = handleDiscordId(targetName);
        } else if (identifier.startsWith("discord_name_")) {
            String targetName = identifier.substring(13);
            result = handleDiscordName(targetName);
        } else if (identifier.startsWith("minecraft_name_")) {
            String targetName = identifier.substring(15);
            result = handleMinecraftName(targetName);
        } else if (identifier.startsWith("clan_")) {
            String targetName = identifier.substring(5);
            result = handleClan(targetName);
        } else if (identifier.startsWith("clan_tag_")) {
            String targetName = identifier.substring(9);
            result = handleClanTag(targetName);
        } else if (identifier.startsWith("group_")) {
            String targetName = identifier.substring(6);
            result = handleGroup(targetName);
        } else if (identifier.startsWith("health_")) {
            String targetName = identifier.substring(7);
            result = handleHealth(targetName);
        } else if (identifier.startsWith("food_")) {
            String targetName = identifier.substring(5);
            result = handleFood(targetName);
        } else if (identifier.startsWith("level_")) {
            String targetName = identifier.substring(6);
            result = handleLevel(targetName);
        } else if (identifier.startsWith("world_")) {
            String targetName = identifier.substring(6);
            result = handleWorld(targetName);
        } else if (identifier.startsWith("x_")) {
            String targetName = identifier.substring(2);
            result = handleX(targetName);
        } else if (identifier.startsWith("y_")) {
            String targetName = identifier.substring(2);
            result = handleY(targetName);
        } else if (identifier.startsWith("z_")) {
            String targetName = identifier.substring(2);
            result = handleZ(targetName);
        } else if (identifier.equals("linked_count")) {
            result = String.valueOf(plugin.getDatabaseManager().getLinkedCount());
        }

        return result;
    }

    private Player getTargetPlayer(String targetName) {
        if (targetName.isEmpty()) {
            return null;
        }
        return Bukkit.getPlayer(targetName);
    }

    private String handleLinked(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "false";
        return String.valueOf(plugin.getDatabaseManager().isLinked(target.getUniqueId()));
    }

    private String handleDiscordId(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        Long discordId = plugin.getDatabaseManager().getDiscordId(target.getUniqueId());
        return discordId != null ? String.valueOf(discordId) : "";
    }

    private String handleDiscordName(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        Long discordId = plugin.getDatabaseManager().getDiscordId(target.getUniqueId());
        if (discordId == null) return "";
        // Would need to fetch from Discord API
        return "";
    }

    private String handleMinecraftName(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        return target.getName();
    }

    private String handleClan(String targetName) {
        // Would integrate with clan plugins via PlaceholderAPI
        return "";
    }

    private String handleClanTag(String targetName) {
        // Would integrate with clan plugins via PlaceholderAPI
        return "";
    }

    private String handleGroup(String targetName) {
        // Would integrate with LuckPerms
        return "";
    }

    private String handleHealth(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        return String.valueOf((int) target.getHealth());
    }

    private String handleFood(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        return String.valueOf(target.getFoodLevel());
    }

    private String handleLevel(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        return String.valueOf(target.getLevel());
    }

    private String handleWorld(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        return target.getWorld().getName();
    }

    private String handleX(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        return String.valueOf((int) target.getLocation().getX());
    }

    private String handleY(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        return String.valueOf((int) target.getLocation().getY());
    }

    private String handleZ(String targetName) {
        Player target = getTargetPlayer(targetName);
        if (target == null) return "";
        return String.valueOf((int) target.getLocation().getZ());
    }
}
