package com.makrozai.eligiusconnector.events;

import java.util.List;
import java.util.Map;

public class EventRewards {

    private final Map<String, RewardGroup> groups;

    public EventRewards(Map<String, RewardGroup> groups) {
        this.groups = groups;
    }

    public Map<String, RewardGroup> getGroups() { return groups; }

    public static class RewardGroup {
        private final List<String> commands;
        private final String message;

        public RewardGroup(List<String> commands, String message) {
            this.commands = commands;
            this.message = message;
        }

        public List<String> getCommands() { return commands; }
        public String getMessage() { return message; }
    }
}
