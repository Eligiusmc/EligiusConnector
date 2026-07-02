package com.makrozai.eligiusconnector.util;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;

// ponytail: Folia detection via class presence. Reflection wrap for ScheduledTask
// since it's not accessible at compile time on Paper-only classpath.
public final class Scheduler {

    private static final boolean FOLIA;

    static {
        boolean folia = false;
        try {
            Class.forName("io.papermc.paper.threadedregions.scheduler.AsyncScheduler");
            folia = true;
        } catch (ClassNotFoundException ignored) {}
        FOLIA = folia;
    }

    public static BukkitTask runAsync(Plugin plugin, Runnable task) {
        if (FOLIA) {
            return wrap(Bukkit.getAsyncScheduler().runNow(plugin, st -> task.run()));
        }
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    public static BukkitTask runLaterAsync(Plugin plugin, Runnable task, long delayTicks) {
        if (FOLIA) {
            return wrap(Bukkit.getAsyncScheduler().runDelayed(plugin, st -> task.run(),
                    delayTicks * 50L, TimeUnit.MILLISECONDS));
        }
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
    }

    public static BukkitTask runTimerAsync(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        if (FOLIA) {
            return wrap(Bukkit.getAsyncScheduler().runAtFixedRate(plugin, st -> task.run(),
                    delayTicks * 50L, periodTicks * 50L, TimeUnit.MILLISECONDS));
        }
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
    }

    @SuppressWarnings("unchecked")
    private static BukkitTask wrap(Object scheduledTask) {
        // ponytail: Folia's ScheduledTask has cancel()/isCancelled(), mirror them via reflection
        // to avoid compile-time dependency on Folia API.
        return new BukkitTask() {
            public int getTaskId() { return -1; }
            public Plugin getOwner() { throw new UnsupportedOperationException(); }
            public boolean isSync() { return false; }
            public boolean isCancelled() {
                try {
                    return (boolean) scheduledTask.getClass().getMethod("isCancelled").invoke(scheduledTask);
                } catch (Exception e) { return false; }
            }
            public void cancel() {
                try {
                    scheduledTask.getClass().getMethod("cancel").invoke(scheduledTask);
                } catch (Exception e) {
                    throw new RuntimeException("Failed to cancel Folia task", e);
                }
            }
        };
    }
}
