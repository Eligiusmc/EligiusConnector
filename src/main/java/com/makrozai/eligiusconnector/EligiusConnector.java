package com.makrozai.eligiusconnector;

import com.makrozai.eligiusconnector.config.ConfigManager;
import com.makrozai.eligiusconnector.database.DatabaseManager;
import com.makrozai.eligiusconnector.discord.DiscordManager;
import com.makrozai.eligiusconnector.listeners.PlayerListener;
import com.makrozai.eligiusconnector.placeholders.PlaceholderAPIManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

public final class EligiusConnector extends JavaPlugin {

    private static EligiusConnector instance;
    private ConfigManager configManager;
    private DatabaseManager databaseManager;
    private DiscordManager discordManager;
    private PlaceholderAPIManager placeholderAPIManager;

    @Override
    public void onEnable() {
        instance = this;

        // Print ASCII art logo
        printLogo();

        // Check version
        if (!checkVersion()) {
            getLogger().severe("Incompatible server version! Requires 1.21+");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        // Initialize managers
        getLogger().info("Initializing EligiusConnector...");

        configManager = new ConfigManager(this);
        configManager.loadConfigs();

        databaseManager = new DatabaseManager(this);
        databaseManager.initialize();

        discordManager = new DiscordManager(this);
        discordManager.initialize();

        // Register listeners
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);

        // PlaceholderAPI
        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            placeholderAPIManager = new PlaceholderAPIManager(this);
            placeholderAPIManager.register();
            getLogger().info("PlaceholderAPI integration enabled!");
        }

        // Register commands
        getCommand("verify").setExecutor(new com.makrozai.eligiusconnector.commands.VerifyCommand(this));
        getCommand("unlink").setExecutor(new com.makrozai.eligiusconnector.commands.UnlinkCommand(this));
        getCommand("connector").setExecutor(new com.makrozai.eligiusconnector.commands.ConnectorCommand(this));

        // Print success
        getLogger().info(ChatColor.GREEN + "=========================================");
        getLogger().info(ChatColor.GREEN + "  EligiusConnector v" + getDescription().getVersion() + " enabled!");
        getLogger().info(ChatColor.GREEN + "  Discord: " + (discordManager.isConnected() ? "Connected" : "Disconnected"));
        getLogger().info(ChatColor.GREEN + "  Database: " + databaseManager.getType());
        getLogger().info(ChatColor.GREEN + "=========================================");

        // Check for updates
        checkForUpdates();
    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling EligiusConnector...");

        if (discordManager != null) {
            discordManager.shutdown();
        }

        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("EligiusConnector disabled!");
    }

    private void printLogo() {
        getLogger().info("");
        getLogger().info("  _____ _           _              _____                _ ");
        getLogger().info(" |  ___(_)_ __   __| | _____  __  |  ___|__  _ __ ___ | | __ _ _   _");
        getLogger().info(" | |_  | | '_ \\ / _` |/ _ \\ \\/ /  | |_ / _ \\| '__/ _ \\| |/ _` | | | |");
        getLogger().info(" |  _| | | | | | (_| |  __/>  <   |  _| (_) | | | (_) | | (_| | |_| |");
        getLogger().info(" |_|   |_|_| |_|\\__,_|\\___/_/\\_\\  |_|  \\___/|_|  \\___/|_|\\__,_|\\__, |");
        getLogger().info("                                                                 |___/");
        getLogger().info("");
    }

    private boolean checkVersion() {
        String version = Bukkit.getVersion();
        // Check for 1.21+
        return version.contains("1.21") || version.contains("26.1");
    }

    private void checkForUpdates() {
        // Async update check
        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            try {
                // Simple version check (could be expanded to check GitHub API)
                getLogger().info("Checking for updates...");
                getLogger().info("Current version: " + getDescription().getVersion());
                getLogger().info("Latest version: Check https://github.com/Eligiusmc/EligiusConnector/releases");
            } catch (Exception e) {
                getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    public static EligiusConnector getInstance() {
        return instance;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public DiscordManager getDiscordManager() {
        return discordManager;
    }

    public PlaceholderAPIManager getPlaceholderAPIManager() {
        return placeholderAPIManager;
    }
}
// Test CI
