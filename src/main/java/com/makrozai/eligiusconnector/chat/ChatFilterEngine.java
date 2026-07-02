package com.makrozai.eligiusconnector.chat;

import com.makrozai.eligiusconnector.EligiusConnector;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatFilterEngine {

    private static final Pattern URL_PATTERN = Pattern.compile(
            "https?://[\\w\\-._~:/?#\\[\\]@!$&'()*+,;=%]+",
            Pattern.CASE_INSENSITIVE
    );

    private final EligiusConnector plugin;
    private final Map<UUID, List<Long>> messageTimestamps = new ConcurrentHashMap<>();
    private final Map<UUID, Integer> violationCount = new ConcurrentHashMap<>();

    public ChatFilterEngine(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public FilterResult filter(String message, Player player) {
        if (player == null || message == null) return FilterResult.passed();
        if (!plugin.getConfigAdapter().isFilterEnabled()) {
            return FilterResult.passed();
        }

        UUID uuid = player.getUniqueId();

        if (plugin.getConfigAdapter().isAntiSpamEnabled() && isSpam(uuid)) {
            String punishment = executePunishment(uuid, "spam");
            return FilterResult.blocked("spam", punishment);
        }

        if (containsProfanity(message)) {
            String punishment = executePunishment(uuid, "profanity");
            return FilterResult.blocked("profanity", punishment);
        }

        if (containsBlockedLinks(message)) {
            String punishment = executePunishment(uuid, "links");
            return FilterResult.blocked("links", punishment);
        }

        if (exceedsCapsLimit(message)) {
            String punishment = executePunishment(uuid, "caps");
            return FilterResult.blocked("caps", punishment);
        }

        return FilterResult.passed();
    }

    private boolean isSpam(UUID uuid) {
        long now = System.currentTimeMillis();
        int maxMessages = plugin.getConfigAdapter().getMaxMessages();
        long timeWindowMs = plugin.getConfigAdapter().getTimeWindowSeconds() * 1000L;

        List<Long> timestamps = messageTimestamps.computeIfAbsent(uuid, k -> new ArrayList<>());
        timestamps.removeIf(t -> now - t > timeWindowMs);
        timestamps.add(now);

        return timestamps.size() > maxMessages;
    }

    private boolean containsProfanity(String message) {
        String lower = message.toLowerCase();
        for (String word : plugin.getConfigAdapter().getBlockedWords()) {
            if (lower.contains(word.toLowerCase())) {
                return true;
            }
        }
        return false;
    }

    private boolean containsBlockedLinks(String message) {
        Matcher matcher = URL_PATTERN.matcher(message);
        while (matcher.find()) {
            String url = matcher.group().toLowerCase();
            for (String domain : plugin.getConfigAdapter().getLinksDomains()) {
                if (url.contains(domain)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    private boolean exceedsCapsLimit(String message) {
        if (message.length() < plugin.getConfigAdapter().getCapsMinLength()) return false;

        long uppercaseCount = message.chars()
                .filter(Character::isUpperCase)
                .count();

        double percentage = (double) uppercaseCount / message.length();
        return percentage > plugin.getConfigAdapter().getCapsMaxPercentage() / 100.0;
    }

    private String executePunishment(UUID uuid, String filterType) {
        int warnings = violationCount.merge(uuid, 1, Integer::sum);
        plugin.getDatabaseManager().addFilterWarning(uuid, filterType, warnings);

        Player player = Bukkit.getPlayer(uuid);
        if (player == null) return "none";

        if (warnings >= 10) {
            scheduleTask(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "banip " + player.getName() + " [ChatFilter] Too many violations"));
            return "banip";
        }

        if (warnings >= 5) {
            scheduleTask(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "tempban " + player.getName() + " 1d [ChatFilter] Excessive violations"));
            return "tempban";
        }

        if (warnings >= 2) {
            scheduleTask(() -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
                    "mute " + player.getName() + " 5m [ChatFilter] Repeat offense"));
            return "mute";
        }

        player.sendMessage(plugin.msg(player, "keys.chat.blocked", java.util.Map.of("reason", filterType)));
        return "warn";
    }

    private void scheduleTask(Runnable task) {
        Bukkit.getScheduler().runTask(plugin, task);
    }

    public void resetViolations(UUID uuid) {
        violationCount.remove(uuid);
        messageTimestamps.remove(uuid);
    }

    public record FilterResult(boolean blocked, String reason, String punishment) {
        public static FilterResult passed() {
            return new FilterResult(false, null, null);
        }

        public static FilterResult blocked(String reason, String punishment) {
            return new FilterResult(true, reason, punishment);
        }
    }
}
