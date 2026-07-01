package com.makrozai.eligiusconnector.events;

import java.util.*;

public class EventResult {
    public enum Status { SUCCESS, FAILED }
    
    private final Status status;
    private UUID winner;
    private UUID runnerUp;
    private List<UUID> topSearchers = new ArrayList<>();
    
    public EventResult(Status status) {
        this.status = status;
    }
    
    public static EventResult success() { return new EventResult(Status.SUCCESS); }
    public static EventResult failed() { return new EventResult(Status.FAILED); }
    
    public Status getStatus() { return status; }
    
    public Optional<UUID> getWinner() { return Optional.ofNullable(winner); }
    public void setWinner(UUID winner) { this.winner = winner; }
    
    public Optional<UUID> getRunnerUp() { return Optional.ofNullable(runnerUp); }
    public void setRunnerUp(UUID runnerUp) { this.runnerUp = runnerUp; }
    
    public List<UUID> getTopSearchers() { return Collections.unmodifiableList(topSearchers); }
    public void setTopSearchers(List<UUID> topSearchers) { this.topSearchers = new ArrayList<>(topSearchers); }
    
    public List<UUID> getTopSearchers(int count) {
        return topSearchers.stream()
                .limit(count)
                .toList();
    }
}
