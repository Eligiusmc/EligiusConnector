package com.makrozai.eligiusconnector;

import com.makrozai.eligiusconnector.config.ConfigAdapter;
import com.makrozai.eligiusconnector.config.LanguageManager;
import com.makrozai.eligiusconnector.database.DatabaseManager;
import com.makrozai.eligiusconnector.discord.ConsoleLogReader;
import com.makrozai.eligiusconnector.discord.DiscordManager;
import com.makrozai.eligiusconnector.discord.PanelManager;
import com.makrozai.eligiusconnector.discord.WebhookManager;
import com.makrozai.eligiusconnector.events.EventManager;
import com.makrozai.eligiusconnector.listeners.PlayerListener;
import com.makrozai.eligiusconnector.listeners.StatsListener;
import com.makrozai.eligiusconnector.stats.PlayerStatsManager;
import com.makrozai.eligiusconnector.counters.CounterManager;
import com.makrozai.eligiusconnector.placeholders.PlaceholderResolver;
import com.makrozai.eligiusconnector.tasks.NicknameSyncTask;
import com.makrozai.eligiusconnector.tasks.RoleSyncTask;
import com.makrozai.eligiusconnector.util.StartupLogger;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class EligiusConnector extends JavaPlugin {

    private static EligiusConnector instance;
    private ConfigAdapter configAdapter;
    private LanguageManager languageManager;
    private DatabaseManager databaseManager;
    private DiscordManager discordManager;
    private WebhookManager webhookManager;
    private PanelManager panelManager;
    private ConsoleLogReader consoleLogReader;
    private EventManager eventManager;
    private PlayerStatsManager statsManager;
    private CounterManager counterManager;
    private PlaceholderResolver placeholderResolver;
    private NicknameSyncTask nicknameSyncTask;
    private RoleSyncTask roleSyncTask;

    private final Map<Long, String> verifyCodes = new ConcurrentHashMap<>();
    private final Map<Long, Long> verifyExpiry = new ConcurrentHashMap<>();

    private long startTime;

    @Override
    public void onEnable() {
        instance = this;
        startTime = System.currentTimeMillis();

        // Load configurations
        configAdapter = new ConfigAdapter(this);
        configAdapter.loadAll();
        configAdapter.logModules();

        languageManager = new LanguageManager(this);
        languageManager.load();

        // Initialize database
        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        // Initialize stats
        statsManager = new PlayerStatsManager(this);
        statsManager.createTable();

        // Initialize Discord
        discordManager = new DiscordManager(this);
        discordManager.initialize();

        // Initialize placeholder resolver
        placeholderResolver = new PlaceholderResolver(this);

        webhookManager = new WebhookManager(this);

        // Initialize panels
        panelManager = new PanelManager(this);
        panelManager.initializePanels();

        // Start role sync
        if (configAdapter.isRoleSyncEnabled()) {
            roleSyncTask = new RoleSyncTask(this);
            roleSyncTask.start();
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new StatsListener(this), this);

        // Register commands
        safeSetExecutor("verify", new com.makrozai.eligiusconnector.commands.VerifyCommand(this));
        safeSetExecutor("unlink", new com.makrozai.eligiusconnector.commands.UnlinkCommand(this));
        safeSetExecutor("connector", new com.makrozai.eligiusconnector.commands.ConnectorCommand(this));
        safeSetExecutor("events", new com.makrozai.eligiusconnector.commands.EventsCommand(this));
        safeSetExecutor("chat", new com.makrozai.eligiusconnector.commands.ChatCommand(this));

        // Initialize counters
        if (configAdapter.isCountersEnabled()) {
            counterManager = new CounterManager(this);
            counterManager.loadAll(configAdapter.getCountersConfig());
            counterManager.startAll();
        }
        if (configAdapter.isSynchronizationEnabled() && configAdapter.isNicknameSyncEnabled()) {
            nicknameSyncTask = new NicknameSyncTask(this);
            nicknameSyncTask.start();
            getLogger().info("Nickname sync task started (interval: " + configAdapter.getNicknameInterval() + "s)");
        }

        // Start console log reader
        if (configAdapter.isConsoleEnabled()) {
            consoleLogReader = new ConsoleLogReader(this);
            consoleLogReader.start();
        }

        // Load events
        if (configAdapter.isEventsEnabled()) {
            eventManager = new EventManager(this);
            eventManager.loadEvents();
        }

        // Send server status
        if (counterManager != null) counterManager.onServerStart();
        if (configAdapter.isStatusEnabled() && discordManager != null && discordManager.isConnected()) {
            discordManager.sendStatusEmbed(new java.util.HashMap<>(), null);
        }

        // Print success
        long elapsed = System.currentTimeMillis() - startTime;
        StartupLogger.printSuccess(elapsed);
    }

    @Override
    public void onDisable() {
        if (counterManager != null) {
            counterManager.onServerStop();
            counterManager.stopAll();
        }
        if (configAdapter.isStatusEnabled() && discordManager != null && discordManager.isConnected()) {
            discordManager.sendStatusOffEmbedSync();
        }
        if (nicknameSyncTask != null) nicknameSyncTask.stop();
        if (roleSyncTask != null) roleSyncTask.stop();
        if (consoleLogReader != null) consoleLogReader.stop();
        if (discordManager != null) discordManager.shutdown();
        if (databaseManager != null) databaseManager.close();
        getLogger().info("EligiusConnector disabled!");
    }

    public void syncRolesForPlayer(Player player) {
        if (roleSyncTask != null) roleSyncTask.syncPlayer(player);
    }

    public String generateVerifyCode(long discordId) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        StringBuilder code = new StringBuilder(6);
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        String result = code.toString();
        verifyCodes.put(discordId, result);
        verifyExpiry.put(discordId, System.currentTimeMillis() + (configAdapter.getCodeExpiryMinutes() * 60000L));
        return result;
    }

    public String getVerifyCode(long discordId) {
        Long expiry = verifyExpiry.get(discordId);
        if (expiry != null && System.currentTimeMillis() > expiry) {
            verifyCodes.remove(discordId);
            verifyExpiry.remove(discordId);
            return null;
        }
        return verifyCodes.get(discordId);
    }

    public boolean verifyPlayer(long discordId, String code) {
        String storedCode = getVerifyCode(discordId);
        if (storedCode != null && storedCode.equals(code)) {
            verifyCodes.remove(discordId);
            verifyExpiry.remove(discordId);
            return true;
        }
        return false;
    }

    // Getters
    public static EligiusConnector getInstance() { return instance; }
    public ConfigAdapter getConfigAdapter() { return configAdapter; }
    public LanguageManager getLanguageManager() { return languageManager; }
    public DatabaseManager getDatabaseManager() { return databaseManager; }
    public DiscordManager getDiscordManager() { return discordManager; }
    public WebhookManager getWebhookManager() { return webhookManager; }
    public PanelManager getPanelManager() { return panelManager; }
    public EventManager getEventManager() { return eventManager; }
    public PlayerStatsManager getStatsManager() { return statsManager; }
    public Map<Long, String> getVerifyCodes() { return verifyCodes; }
    public NicknameSyncTask getNicknameSyncTask() { return nicknameSyncTask; }
    public CounterManager getCounterManager() { return counterManager; }
    public PlaceholderResolver getPlaceholderResolver() { return placeholderResolver; }
    public long getStartTime() { return startTime; }

    // i18n helper methods
    public String msg(String key) {
        return languageManager.get(key);
    }

    public String msg(org.bukkit.entity.Player player, String key) {
        if (player == null) return languageManager.get(key);
        return languageManager.get(player.getUniqueId(), key);
    }

    public String msg(String key, java.util.Map<String, String> replacements) {
        return languageManager.get(key, replacements);
    }

    public String msg(org.bukkit.entity.Player player, String key, java.util.Map<String, String> replacements) {
        if (player == null) return languageManager.get(key, replacements);
        return languageManager.get(player.getUniqueId(), key, replacements);
    }

    public String applyPlaceholders(Player player, String text) {
        if (text == null || text.isEmpty()) return text;
        return placeholderResolver.resolve(text, player);
    }

    private void safeSetExecutor(String name, org.bukkit.command.CommandExecutor executor) {
        var cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(executor);
        } else {
            getLogger().warning("Command not found in plugin.yml: " + name);
        }
    }
}
