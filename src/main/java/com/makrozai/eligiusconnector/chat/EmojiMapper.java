package com.makrozai.eligiusconnector.chat;

import java.util.Map;

public final class EmojiMapper {

    private EmojiMapper() {}

    private static final Map<String, String> EMOJI_MAP = Map.ofEntries(
            Map.entry("smile", "☺"),
            Map.entry("grinning", "😀"),
            Map.entry("joy", "😂"),
            Map.entry("rofl", "🤣"),
            Map.entry("wink", "😉"),
            Map.entry("blush", "😊"),
            Map.entry("heart_eyes", "😍"),
            Map.entry("kissing_heart", "😘"),
            Map.entry("thinking", "🤔"),
            Map.entry("neutral", "😐"),
            Map.entry("expressionless", "😑"),
            Map.entry("unamused", "😒"),
            Map.entry("sweat", "😓"),
            Map.entry("pensive", "😔"),
            Map.entry("confused", "😕"),
            Map.entry("confounded", "😖"),
            Map.entry("kissing_closed_eyes", "😚"),
            Map.entry("kissing", "😗"),
            Map.entry("stuck_out_tongue", "😛"),
            Map.entry("stuck_out_tongue_winking_eye", "😜"),
            Map.entry("disappointed", "😞"),
            Map.entry("worried", "😟"),
            Map.entry("angry", "😠"),
            Map.entry("rage", "😡"),
            Map.entry("cry", "😢"),
            Map.entry("persevere", "😤"),
            Map.entry("triumph", "😤"),
            Map.entry("relieved", "😌"),
            Map.entry("sleepy", "😴"),
            Map.entry("tired_face", "😫"),
            Map.entry("grimacing", "😬"),
            Map.entry("sob", "😭"),
            Map.entry("open_mouth", "😮"),
            Map.entry("hushed", "😯"),
            Map.entry("cold_sweat", "😰"),
            Map.entry("scream", "😱"),
            Map.entry("astonished", "😲"),
            Map.entry("flushed", "😳"),
            Map.entry("sleeping", "😴"),
            Map.entry("dizzy_face", "😵"),
            Map.entry("no_mouth", "😶"),
            Map.entry("mask", "😷"),
            Map.entry("sunglasses", "😎"),
            Map.entry("nerd", "🤓"),
            Map.entry("skull", "💀"),
            Map.entry("clown", "🤡"),
            Map.entry("ghost", "👻"),
            Map.entry("alien", "👽"),
            Map.entry("robot", "🤖"),
            Map.entry("fire", "🔥"),
            Map.entry("star", "⭐"),
            Map.entry("star2", "🌟"),
            Map.entry("zap", "⚡"),
            Map.entry("sunny", "☀"),
            Map.entry("cloud", "☁"),
            Map.entry("rain", "🌧"),
            Map.entry("snow", "❄"),
            Map.entry("heart", "❤"),
            Map.entry("broken_heart", "💔"),
            Map.entry("sparkling_heart", "💖"),
            Map.entry("green_heart", "💚"),
            Map.entry("blue_heart", "💙"),
            Map.entry("yellow_heart", "💛"),
            Map.entry("purple_heart", "💜"),
            Map.entry("thumbsup", "👍"),
            Map.entry("thumbsdown", "👎"),
            Map.entry("fist", "✊"),
            Map.entry("wave", "👋"),
            Map.entry("clap", "👏"),
            Map.entry("ok_hand", "👌"),
            Map.entry("v", "✌"),
            Map.entry("point_up", "☝"),
            Map.entry("point_down", "👇"),
            Map.entry("point_left", "👈"),
            Map.entry("point_right", "👉"),
            Map.entry("raised_hands", "🙌"),
            Map.entry("pray", "🙏"),
            Map.entry("muscle", "💪"),
            Map.entry("beer", "🍺"),
            Map.entry("beers", "🍻"),
            Map.entry("wine", "🍷"),
            Map.entry("coffee", "☕"),
            Map.entry("pizza", "🍕"),
            Map.entry("hamburger", "🍔"),
            Map.entry("fries", "🍟"),
            Map.entry("cake", "🎂"),
            Map.entry("cookie", "🍪"),
            Map.entry("apple", "🍎"),
            Map.entry("diamond", "💎"),
            Map.entry("trophy", "🏆"),
            Map.entry("crown", "👑"),
            Map.entry("bomb", "💣"),
            Map.entry("sword", "⚔"),
            Map.entry("shield", "🛡"),
            Map.entry("crossed_swords", "⚔"),
            Map.entry("pickaxe", "⛏"),
            Map.entry("wrench", "🔧"),
            Map.entry("hammer", "🔨"),
            Map.entry("lock", "🔒"),
            Map.entry("key", "🔑"),
            Map.entry("mega", "📣"),
            Map.entry("loud_sound", "🔊"),
            Map.entry("mute", "🔇"),
            Map.entry("bell", "🔔"),
            Map.entry("no_bell", "🔕"),
            Map.entry("warning", "⚠"),
            Map.entry("question", "❓"),
            Map.entry("exclamation", "❗"),
            Map.entry("white_check_mark", "✅"),
            Map.entry("x", "❌"),
            Map.entry("negative_squared_cross_mark", "❎"),
            Map.entry("arrows_counterclockwise", "🔄"),
            Map.entry("abc", "🔤"),
            Map.entry("100", "💯"),
            Map.entry("cool", "🔵"),
            Map.entry("free", "🆓"),
            Map.entry("new", "🆕"),
            Map.entry("top", "🔝"),
            Map.entry("underage", "🔞"),
            Map.entry("trident", "🔱")
    );

    /**
     * Convierte formato Discord markdown a Minecraft minimessage.
     */
    public static String discordToMc(String message) {
        if (message == null || message.isEmpty()) return message;

        String result = message;

        // Spoiler (antes que otros para evitar conflictos con ||)
        result = result.replaceAll("\\|\\|(.+?)\\|\\|", "<spoiler>$1</spoiler>");

        // Bold
        result = result.replaceAll("\\*\\*(.+?)\\*\\*", "<bold>$1</bold>");

        // Italic
        result = result.replaceAll("\\*(.+?)\\*", "<italic>$1</italic>");

        // Underline
        result = result.replaceAll("__(.+?)__", "<underlined>$1</underlined>");

        // Strikethrough
        result = result.replaceAll("~~(.+?)~~", "<strikethrough>$1</strikethrough>");

        // Code blocks
        result = result.replaceAll("```(.+?)```", "<dark_gray>$1</dark_gray>");

        // Inline code
        result = result.replaceAll("`(.+?)`", "<gray>$1</gray>");

        // Discord custom emotes: <:name:id> or <a:name:id>
        result = result.replaceAll("<a?:(\\w+):\\d+>", ":$1:");

        // Replace :emoji_name: with unicode
        for (var entry : EMOJI_MAP.entrySet()) {
            result = result.replace(":" + entry.getKey() + ":", entry.getValue());
        }

        return result;
    }

    /**
     * Elimina emotes personalizados de Discord y los deja como texto.
     * Ejemplo: <:verified:123456> -> :verified:
     */
    public static String stripCustomEmotes(String message) {
        if (message == null) return null;
        return message.replaceAll("<a?:(\\w+):\\d+>", ":$1:");
    }

    /**
     * Verifica si un mensaje contiene emotes personalizados de Discord.
     */
    public static boolean hasCustomEmotes(String message) {
        if (message == null) return false;
        return message.matches(".*<a?:\\w+:\\d+>.*");
    }
}
