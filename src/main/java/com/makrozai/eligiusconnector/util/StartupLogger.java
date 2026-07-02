package com.makrozai.eligiusconnector.util;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;

public class StartupLogger {

    private static final LegacyComponentSerializer SERIALIZER = LegacyComponentSerializer.builder()
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    private static void log(String miniMessageString) {
        String legacy = SERIALIZER.serialize(
                MiniMessage.miniMessage().deserialize(miniMessageString));
        Bukkit.getConsoleSender().sendMessage(legacy);
    }

    public static void printLogo(String version, String platform, String storage) {
        Bukkit.getConsoleSender().sendMessage("");
        log("<gray>------------------------------------------------------------");
        log("<gradient:#9b59b6:#8e44ad>                  <bold>ELIGIUS MC</bold></gradient>");
        log("<gradient:#9b59b6:#8e44ad>      /\\_/\\       </gradient>");
        log("<gradient:#9b59b6:#8e44ad>     ( o.o )      </gradient><aqua>Connector v" + version + "</aqua>");
        log("<gradient:#9b59b6:#8e44ad>      > ^ <       </gradient><gray>Platform:</gray> <white>" + platform + "</white> <gray>|</gray> <gray>Storage:</gray> <white>" + storage + "</white>");
        log("<gradient:#9b59b6:#8e44ad>                  </gradient><dark_gray>GitHub:</dark_gray> <aqua>https://github.com/Eligiusmc</aqua>");
        log("<gray>------------------------------------------------------------");
    }

    public static void printStep(String step) {
        log("<gray>[<gradient:#9b59b6:#8e44ad>EligiusConnector</gradient>]</gray> <white>" + step + "</white>");
    }

    public static void printError(String error) {
        log("<gray>[<gradient:#9b59b6:#8e44ad>EligiusConnector</gradient>]</gray> <red>[ERROR] " + error + "</red>");
    }

    public static void printSuccess(long ms) {
        log("<gray>[<gradient:#9b59b6:#8e44ad>EligiusConnector</gradient>]</gray> <green>Successfully enabled. (took " + ms + "ms)</green>");
    }
}
