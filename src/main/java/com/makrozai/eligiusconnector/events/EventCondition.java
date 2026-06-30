package com.makrozai.eligiusconnector.events;

public class EventCondition {

    private final String type;
    private final String entity;
    private final int minutes;

    public EventCondition(String type, String entity, int minutes) {
        this.type = type;
        this.entity = entity;
        this.minutes = minutes;
    }

    public String getType() { return type; }
    public String getEntity() { return entity; }
    public int getMinutes() { return minutes; }
}
