package com.makrozai.eligiusconnector.placeholders;

import com.makrozai.eligiusconnector.EligiusConnector;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

public class PlaceholderResolver {

    private final EligiusConnector plugin;
    private final ConcurrentHashMap<String, Function<Player, String>> resolvers = new ConcurrentHashMap<>();
    private final boolean papiAvailable;

    public PlaceholderResolver(EligiusConnector plugin) {
        this.plugin = plugin;
        this.papiAvailable = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        registerBuiltIns();
    }

    private void registerBuiltIns() {
        register("server_online", player -> String.valueOf(Bukkit.getOnlinePlayers().size()));
        register("server_max_players", player -> String.valueOf(Bukkit.getMaxPlayers()));
        register("server_name", player -> Bukkit.getServer().getName());
        register("server_ram_used", player -> String.valueOf((Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / (1024 * 1024)));
        register("server_ram_free", player -> String.valueOf(Runtime.getRuntime().freeMemory() / (1024 * 1024)));
        register("server_ram_total", player -> String.valueOf(Runtime.getRuntime().totalMemory() / (1024 * 1024)));
        register("server_uptime", player -> {
            long ms = System.currentTimeMillis() - plugin.getStartTime();
            long s = ms / 1000;
            return (s / 3600) + "h " + ((s % 3600) / 60) + "m";
        });
        register("count", player -> String.valueOf(Bukkit.getOnlinePlayers().size()));

        register("server_members", player -> {
            var guild = plugin.getDiscordManager().getGuild();
            return guild != null ? String.valueOf(guild.getMemberCount()) : "0";
        });

        register("player_name", player -> player != null ? player.getName() : "");
        register("player_displayname", player -> player != null ? player.getDisplayName() : "");
        register("player_world", player -> player != null ? player.getWorld().getName() : "");
        register("player_health", player -> player != null ? String.valueOf((int) player.getHealth()) : "0");
        register("player_food", player -> player != null ? String.valueOf(player.getFoodLevel()) : "0");
    }

    public void register(String key, Function<Player, String> resolver) {
        resolvers.put(key, resolver);
    }

    public String resolve(String text, Player player) {
        if (text == null || text.isEmpty()) return text;

        for (Map.Entry<String, Function<Player, String>> entry : resolvers.entrySet()) {
            String placeholder = "%" + entry.getKey() + "%";
            if (text.contains(placeholder)) {
                String value = entry.getValue().apply(player);
                text = text.replace(placeholder, value);
            }
        }

        if (papiAvailable) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    public String resolve(String text) {
        return resolve(text, null);
    }
}
