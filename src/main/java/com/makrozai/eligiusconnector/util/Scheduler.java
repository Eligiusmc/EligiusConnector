package com.makrozai.eligiusconnector.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

// Folia scheduler APIs are called reflectively so the plugin also loads on Spigot.
public final class Scheduler {

    private static final boolean FOLIA;

    static {
        FOLIA = Bukkit.getName().equalsIgnoreCase("Folia");
    }

    public static BukkitTask runSync(Plugin plugin, Runnable task) {
        if (FOLIA) {
            try {
                Object scheduler = Bukkit.class.getMethod("getGlobalRegionScheduler").invoke(null);
                Class<?> schedulerType = Class.forName("io.papermc.paper.threadedregions.scheduler.GlobalRegionScheduler");
                Consumer<Object> consumer = scheduledTask -> task.run();
                Object scheduledTask = schedulerType.getMethod("run", Plugin.class, Consumer.class)
                        .invoke(scheduler, plugin, consumer);
                return wrap(plugin, scheduledTask, true);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to schedule Folia global task", e);
            }
        }
        return Bukkit.getScheduler().runTask(plugin, task);
    }

    public static BukkitTask runForEntity(Plugin plugin, Entity entity, Runnable task) {
        if (FOLIA) {
            try {
                Object scheduler = Entity.class.getMethod("getScheduler").invoke(entity);
                Class<?> schedulerType = Class.forName("io.papermc.paper.threadedregions.scheduler.EntityScheduler");
                Consumer<Object> consumer = st -> task.run();
                Object scheduledTask = schedulerType.getMethod("run", Plugin.class, Consumer.class, Runnable.class)
                        .invoke(scheduler, plugin, consumer, null);
                if (scheduledTask == null) return cancelledTask(plugin);
                return wrap(plugin, scheduledTask, true);
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Failed to schedule Folia entity task", e);
            }
        }
        return Bukkit.getScheduler().runTask(plugin, task);
    }

    public static BukkitTask runAsync(Plugin plugin, Runnable task) {
        if (FOLIA) {
            return wrap(plugin, Bukkit.getAsyncScheduler().runNow(plugin, st -> task.run()), false);
        }
        return Bukkit.getScheduler().runTaskAsynchronously(plugin, task);
    }

    public static BukkitTask runLaterAsync(Plugin plugin, Runnable task, long delayTicks) {
        if (FOLIA) {
            return wrap(plugin, Bukkit.getAsyncScheduler().runDelayed(plugin, st -> task.run(),
                    delayTicks * 50L, TimeUnit.MILLISECONDS), false);
        }
        return Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, task, delayTicks);
    }

    public static BukkitTask runTimerAsync(Plugin plugin, Runnable task, long delayTicks, long periodTicks) {
        if (FOLIA) {
            return wrap(plugin, Bukkit.getAsyncScheduler().runAtFixedRate(plugin, st -> task.run(),
                    delayTicks * 50L, periodTicks * 50L, TimeUnit.MILLISECONDS), false);
        }
        return Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, task, delayTicks, periodTicks);
    }

    private static BukkitTask wrap(Plugin plugin, Object scheduledTask, boolean sync) {
        // ponytail: Folia's ScheduledTask has cancel()/isCancelled(), mirror them via reflection
        // to avoid compile-time dependency on Folia API.
        return new BukkitTask() {
            public int getTaskId() { return -1; }
            public Plugin getOwner() { return plugin; }
            public boolean isSync() { return sync; }
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

    private static BukkitTask cancelledTask(Plugin plugin) {
        return new BukkitTask() {
            public int getTaskId() { return -1; }
            public Plugin getOwner() { return plugin; }
            public boolean isSync() { return true; }
            public boolean isCancelled() { return true; }
            public void cancel() {}
        };
    }
}
