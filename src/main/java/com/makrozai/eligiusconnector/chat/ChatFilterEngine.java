package com.makrozai.eligiusconnector.chat;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.makrozai.eligiusconnector.util.Scheduler;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
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

        if (plugin.getConfigAdapter().isSpamFilterEnabled() && isSpam(uuid)) {
            String punishment = executePunishment(uuid, player, "spam");
            return FilterResult.blocked("spam", punishment);
        }

        if (plugin.getConfigAdapter().isProfanityFilterEnabled() && containsProfanity(message)) {
            String punishment = executePunishment(uuid, player, "profanity");
            return FilterResult.blocked("profanity", punishment);
        }

        if (plugin.getConfigAdapter().isLinksFilterEnabled() && containsBlockedLinks(message)) {
            String punishment = executePunishment(uuid, player, "links");
            return FilterResult.blocked("links", punishment);
        }

        if (plugin.getConfigAdapter().isCapsFilterEnabled() && exceedsCapsLimit(message)) {
            String punishment = executePunishment(uuid, player, "caps");
            return FilterResult.blocked("caps", punishment);
        }

        return FilterResult.passed();
    }

    private boolean isSpam(UUID uuid) {
        long now = System.currentTimeMillis();
        int maxMessages = plugin.getConfigAdapter().getSpamMaxMessages();
        long timeWindowMs = plugin.getConfigAdapter().getSpamTimeWindowSeconds() * 1000L;

        List<Long> timestamps = messageTimestamps.computeIfAbsent(uuid, k -> new CopyOnWriteArrayList<>());
        timestamps.removeIf(t -> now - t > timeWindowMs);
        timestamps.add(now);

        return timestamps.size() > maxMessages;
    }

    private boolean containsProfanity(String message) {
        String lower = message.toLowerCase(java.util.Locale.ROOT);
        for (String word : plugin.getConfigAdapter().getBlockedWords()) {
            if (lower.contains(word.toLowerCase(java.util.Locale.ROOT))) {
                return true;
            }
        }
        return false;
    }

    private boolean containsBlockedLinks(String message) {
        Matcher matcher = URL_PATTERN.matcher(message);
        boolean blacklistMode = "blacklist".equalsIgnoreCase(plugin.getConfigAdapter().getLinksMode());
        while (matcher.find()) {
            String url = matcher.group().toLowerCase();
            boolean found = false;
            for (String domain : plugin.getConfigAdapter().getLinksDomains()) {
                if (url.contains(domain)) {
                    found = true;
                    break;
                }
            }
            if (blacklistMode) return found;
            if (!found) return true;
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

    private String executePunishment(UUID uuid, Player player, String filterType) {
        int warnings = violationCount.merge(uuid, 1, Integer::sum);
        Scheduler.runAsync(plugin, () -> plugin.getDatabaseManager().addFilterWarning(uuid, filterType, warnings));

        if (plugin.getConfigAdapter().isPunishmentEnabled()) {
            java.util.Map<Integer, String> thresholds = plugin.getConfigAdapter().getPunishmentThresholds();
            String action = null;
            int matchedLevel = 0;
            for (java.util.Map.Entry<Integer, String> entry : thresholds.entrySet()) {
                if (warnings >= entry.getKey() && entry.getKey() > matchedLevel) {
                    action = entry.getValue();
                    matchedLevel = entry.getKey();
                }
            }
            if (action != null) {
                executePunishmentAction(player, action);
                notifyBlocked(player, filterType);
                return action.contains(":") ? action.split(":")[0] : action;
            }
        }

        notifyBlocked(player, filterType);
        return "warn";
    }

    private void notifyBlocked(Player player, String filterType) {
        Scheduler.runForEntity(plugin, player, () ->
                player.sendMessage(plugin.msg(player, "chat.blocked", java.util.Map.of("reason", filterType))));
    }

    private void executePunishmentAction(Player player, String action) {
        if (action.equals("warn") || action.equals("delete")) return;

        String[] parts = action.split(":", 2);
        String type = parts[0];
        int duration = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;

        String command = switch (type) {
            case "mute" -> "mute %player% " + duration + "s [ChatFilter]";
            case "tempban" -> "tempban %player% " + duration + "s [ChatFilter]";
            case "banip" -> "banip %player% [ChatFilter]";
            default -> null;
        };
        if (command != null) runPunishment(player, command);
    }

    private void runPunishment(Player player, String commandTemplate) {
        Scheduler.runForEntity(plugin, player, () -> {
            String command = commandTemplate.replace("%player%", player.getName());
            Scheduler.runSync(plugin, () -> org.bukkit.Bukkit.dispatchCommand(org.bukkit.Bukkit.getConsoleSender(), command));
        });
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
