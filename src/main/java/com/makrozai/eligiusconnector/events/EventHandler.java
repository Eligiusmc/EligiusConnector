package com.makrozai.eligiusconnector.events;

import org.bukkit.entity.Player;
import java.util.List;
import java.util.UUID;

public interface EventHandler {
    void onStart(GameEvent event);
    void onEnd(GameEvent event, EventResult result);
    void onTick(GameEvent event);
    boolean evaluateCondition(GameEvent event, EventCondition condition);
    void cleanup(GameEvent event);
    List<UUID> getParticipants(GameEvent event);
}
