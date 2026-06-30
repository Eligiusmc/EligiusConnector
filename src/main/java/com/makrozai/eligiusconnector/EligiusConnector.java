package com.makrozai.eligiusconnector;

import com.makrozai.eligiusconnector.config.ConfigAdapter;
import com.makrozai.eligiusconnector.config.LanguageManager;
import com.makrozai.eligiusconnector.database.DatabaseManager;
import com.makrozai.eligiusconnector.discord.ConsoleLogReader;
import com.makrozai.eligiusconnector.discord.DiscordManager;
import com.makrozai.eligiusconnector.discord.WebhookManager;
import com.makrozai.eligiusconnector.events.EventManager;
import com.makrozai.eligiusconnector.listeners.PlayerListener;
import com.makrozai.eligiusconnector.listeners.StatsListener;
import com.makrozai.eligiusconnector.stats.PlayerStatsManager;
import com.makrozai.eligiusconnector.tasks.AllMembersCounterTask;
import com.makrozai.eligiusconnector.tasks.NicknameSyncTask;
import com.makrozai.eligiusconnector.tasks.OnlineCounterTask;
import com.makrozai.eligiusconnector.tasks.ServerStatusCounterTask;
import com.makrozai.eligiusconnector.util.StartupLogger;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.bukkit.Bukkit;
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
    private ConsoleLogReader consoleLogReader;
    private EventManager eventManager;
    private PlayerStatsManager statsManager;
    private OnlineCounterTask onlineCounterTask;
    private AllMembersCounterTask allMembersCounterTask;
    private ServerStatusCounterTask serverStatusCounterTask;
    private NicknameSyncTask nicknameSyncTask;

    private final Map<Long, String> verifyCodes = new ConcurrentHashMap<>();
    private final Map<Long, Long> verifyExpiry = new ConcurrentHashMap<>();
    private final Map<Long, Long> birthdaySetupUsers = new ConcurrentHashMap<>();

    private long startTime;

    @Override
    public void onEnable() {
        instance = this;
        startTime = System.currentTimeMillis();

        // Load configurations
        configAdapter = new ConfigAdapter(this);
        configAdapter.loadAll();

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

        webhookManager = new WebhookManager(this);

        // Create verified role if needed
        if (configAdapter.isRoleSyncEnabled()) {
            createVerifiedRoleIfNeeded();
        }

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        getServer().getPluginManager().registerEvents(new StatsListener(this), this);

        // Register commands
        safeSetExecutor("verify", new com.makrozai.eligiusconnector.commands.VerifyCommand(this));
        safeSetExecutor("unlink", new com.makrozai.eligiusconnector.commands.UnlinkCommand(this));
        safeSetExecutor("connector", new com.makrozai.eligiusconnector.commands.ConnectorCommand(this));
        safeSetExecutor("events", new com.makrozai.eligiusconnector.commands.EventsCommand(this));
        safeSetExecutor("birthday", new com.makrozai.eligiusconnector.commands.BirthdayCommand(this));
        safeSetExecutor("chat", new com.makrozai.eligiusconnector.commands.ChatCommand(this));

        // Start tasks
        if (configAdapter.isOnlineCounterEnabled()) {
            onlineCounterTask = new OnlineCounterTask(this);
            onlineCounterTask.start();
        }
        if (configAdapter.isModuleEnabled("all_members_counter")) {
            allMembersCounterTask = new AllMembersCounterTask(this);
            allMembersCounterTask.start();
        }
        if (configAdapter.isServerStatusCounterEnabled()) {
            serverStatusCounterTask = new ServerStatusCounterTask(this);
            serverStatusCounterTask.start();
        }
        if (configAdapter.isSynchronizationEnabled() && configAdapter.isNicknameSyncEnabled()) {
            nicknameSyncTask = new NicknameSyncTask(this);
            nicknameSyncTask.start();
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

        // Send server start status
        sendServerStatus(true);

        // Print success
        long elapsed = System.currentTimeMillis() - startTime;
        StartupLogger.printSuccess(elapsed);
    }

    @Override
    public void onDisable() {
        sendServerStatus(false);
        if (onlineCounterTask != null) onlineCounterTask.stop();
        if (allMembersCounterTask != null) allMembersCounterTask.stop();
        if (serverStatusCounterTask != null) serverStatusCounterTask.stop();
        if (nicknameSyncTask != null) nicknameSyncTask.stop();
        if (consoleLogReader != null) consoleLogReader.stop();
        if (discordManager != null) discordManager.shutdown();
        if (databaseManager != null) databaseManager.close();
        getLogger().info("EligiusConnector disabled!");
    }

    private void sendServerStatus(boolean online) {
        if (discordManager == null || !discordManager.isConnected()) return;

        // Update server status channel name
        if (serverStatusCounterTask != null) {
            serverStatusCounterTask.updateStatus(online);
        }

        if (!configAdapter.isStatusEnabled()) return;

        if (online) {
            Map<String, String> replacements = new java.util.HashMap<>();
            replacements.put("online", String.valueOf(Bukkit.getOnlinePlayers().size()));
            replacements.put("max", String.valueOf(Bukkit.getMaxPlayers()));
            discordManager.sendStatusEmbed(replacements);
        } else {
            discordManager.sendStatusOffEmbed();
        }
    }

    private void createVerifiedRoleIfNeeded() {
        String roleId = configAdapter.getVerifiedRoleId();
        if (roleId == null || roleId.isEmpty()) {
            // Create role and save ID
            Guild guild = discordManager.getGuild();
            if (guild == null) return;

            guild.createRole()
                    .setName("Verificado")
                    .setColor(0x2ECC71)
                    .setMentionable(false)
                    .queue(role -> {
                        getLogger().info("Created 'Verificado' role: " + role.getId());
                        // Save role ID to config
                        configAdapter.setVerifiedRoleId(role.getId());
                    }, error -> getLogger().warning("Failed to create verified role: " + error.getMessage()));
        }
    }

    public void assignVerifiedRole(long discordId) {
        if (!configAdapter.isRoleSyncEnabled() || !configAdapter.isRoleOnLinkEnabled()) return;
        String roleId = configAdapter.getVerifiedRoleId();
        if (roleId == null || roleId.isEmpty()) return;

        Guild guild = discordManager.getGuild();
        if (guild == null) return;

        guild.retrieveMemberById(discordId).queue(member -> {
            Role role = guild.getRoleById(roleId);
            if (role != null && !member.getRoles().contains(role)) {
                guild.addRoleToMember(member, role).queue(
                        success -> getLogger().info("Assigned 'Verificado' role to " + member.getUser().getName()),
                        error -> getLogger().warning("Failed to assign role: " + error.getMessage())
                );
            }
        }, error -> getLogger().warning("Failed to retrieve member for role assignment: " + error.getMessage()));
    }

    public String generateVerifyCode(long discordId) {
        String code = String.valueOf((int) (Math.random() * 900000) + 100000);
        verifyCodes.put(discordId, code);
        verifyExpiry.put(discordId, System.currentTimeMillis() + (configAdapter.getCodeExpiryMinutes() * 60000L));
        return code;
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
    public EventManager getEventManager() { return eventManager; }
    public PlayerStatsManager getStatsManager() { return statsManager; }
    public Map<Long, String> getVerifyCodes() { return verifyCodes; }
    public Map<Long, Long> getBirthdaySetupUsers() { return birthdaySetupUsers; }
    public NicknameSyncTask getNicknameSyncTask() { return nicknameSyncTask; }

    private void safeSetExecutor(String name, org.bukkit.command.CommandExecutor executor) {
        var cmd = getCommand(name);
        if (cmd != null) {
            cmd.setExecutor(executor);
        } else {
            getLogger().warning("Command not found in plugin.yml: " + name);
        }
    }
}
