package com.makrozai.eligiusconnector.discord;

import org.bukkit.entity.Player;

public class MinecraftChatEvent {

    private final Player player;
    private final String message;

    public MinecraftChatEvent(Player player, String message) {
        this.player = player;
        this.message = message;
    }

    public Player getPlayer() {
        return player;
    }

    public String getMessage() {
        return message;
    }
}
