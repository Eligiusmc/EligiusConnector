package com.makrozai.eligiusconnector.events;

import java.util.*;

public class EventResult {
    public enum Status { SUCCESS, PENDING, FAILED }
    
    private final Status status;
    private UUID firstKiller;
    private UUID winner;
    private UUID runnerUp;
    private List<UUID> topSearchers = new ArrayList<>();
    private Map<UUID, Integer> killCounts = new HashMap<>();
    
    public EventResult(Status status) {
        this.status = status;
    }
    
    public static EventResult success() { return new EventResult(Status.SUCCESS); }
    public static EventResult pending() { return new EventResult(Status.PENDING); }
    public static EventResult failed() { return new EventResult(Status.FAILED); }
    
    public Status getStatus() { return status; }
    
    public Optional<UUID> getFirstKiller() { return Optional.ofNullable(firstKiller); }
    public void setFirstKiller(UUID firstKiller) { this.firstKiller = firstKiller; }
    
    public Optional<UUID> getWinner() { return Optional.ofNullable(winner); }
    public void setWinner(UUID winner) { this.winner = winner; }
    
    public Optional<UUID> getRunnerUp() { return Optional.ofNullable(runnerUp); }
    public void setRunnerUp(UUID runnerUp) { this.runnerUp = runnerUp; }
    
    public List<UUID> getTopSearchers() { return Collections.unmodifiableList(topSearchers); }
    public void setTopSearchers(List<UUID> topSearchers) { this.topSearchers = new ArrayList<>(topSearchers); }
    
    public Map<UUID, Integer> getKillCounts() { return Collections.unmodifiableMap(killCounts); }
    public void setKillCounts(Map<UUID, Integer> killCounts) { this.killCounts = new HashMap<>(killCounts); }
    public void addKill(UUID player) { killCounts.merge(player, 1, Integer::sum); }
    public int getKills(UUID player) { return killCounts.getOrDefault(player, 0); }
    
    public List<UUID> getTopSearchers(int count) {
        return topSearchers.stream()
                .limit(count)
                .toList();
    }
    
    public List<Map.Entry<UUID, Integer>> getTopKillers(int count) {
        return killCounts.entrySet().stream()
                .sorted(Map.Entry.<UUID, Integer>comparingByValue().reversed())
                .limit(count)
                .toList();
    }
}
