package com.makrozai.eligiusconnector.database;

import com.makrozai.eligiusconnector.EligiusConnector;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;

public class DatabaseManager {

    private final EligiusConnector plugin;
    private HikariDataSource dataSource;
    private String databaseType;

    public DatabaseManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void initialize() {
        databaseType = plugin.getConfigAdapter().getDatabaseType().toLowerCase();

        switch (databaseType) {
            case "mysql":
                initializeMySQL();
                break;
            case "redis":
                initializeRedis();
                break;
            case "sqlite":
            default:
                initializeSQLite();
                break;
        }

        createTables();
        plugin.getLogger().info("Database initialized: " + databaseType);
    }

    private void initializeSQLite() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + new File(plugin.getDataFolder(), "connector.db").getAbsolutePath());
        config.setDriverClassName("org.sqlite.JDBC");
        config.setMaximumPoolSize(1);
        dataSource = new HikariDataSource(config);
    }

    private void initializeMySQL() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://" +
                plugin.getConfigAdapter().getMySQLHost() + ":" +
                plugin.getConfigAdapter().getMySQLPort() + "/" +
                plugin.getConfigAdapter().getMySQLDatabase());
        config.setUsername(plugin.getConfigAdapter().getMySQLUsername());
        config.setPassword(plugin.getConfigAdapter().getMySQLPassword());
        config.setMaximumPoolSize(plugin.getConfigAdapter().getMySQLMaxPoolSize());
        config.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource = new HikariDataSource(config);
    }

    private void initializeRedis() {
        // Redis doesn't use JDBC - would use Jedis directly
        // For now, fall back to SQLite for account storage
        plugin.getLogger().warning("Redis as primary database not yet fully supported. Using SQLite for account storage.");
        initializeSQLite();
    }

    private void createTables() {
        String accountTable = "CREATE TABLE IF NOT EXISTS connector_accounts (" +
                "discord_id BIGINT PRIMARY KEY," +
                "minecraft_uuid VARCHAR(36) UNIQUE," +
                "minecraft_name VARCHAR(16)," +
                "linked_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "linked_by VARCHAR(16)," +
                "birthday VARCHAR(10)" +
                ")";

        String auditTable = "CREATE TABLE IF NOT EXISTS connector_audit_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "level VARCHAR(10)," +
                "action VARCHAR(50)," +
                "user VARCHAR(16)," +
                "user_type VARCHAR(10)," +
                "details TEXT," +
                "ip VARCHAR(45)" +
                ")";

        String filterTable = "CREATE TABLE IF NOT EXISTS connector_filter_warnings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "uuid VARCHAR(36)," +
                "filter_type VARCHAR(50)," +
                "level INT," +
                "timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String statsTable = "CREATE TABLE IF NOT EXISTS connector_player_stats (" +
                "uuid VARCHAR(36) PRIMARY KEY," +
                "player_name VARCHAR(16)," +
                "kills INT DEFAULT 0," +
                "deaths INT DEFAULT 0," +
                "playtime BIGINT DEFAULT 0," +
                "blocks_broken INT DEFAULT 0," +
                "blocks_placed INT DEFAULT 0," +
                "items_crafted INT DEFAULT 0," +
                "last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "first_joined TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ")";

        String eventsLogTable = "CREATE TABLE IF NOT EXISTS connector_events_log (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "event_id VARCHAR(50)," +
                "event_name VARCHAR(100)," +
                "started_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                "ended_at TIMESTAMP," +
                "started_by VARCHAR(16)," +
                "winner VARCHAR(16)," +
                "status VARCHAR(20)" +
                ")";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(accountTable);
            stmt.executeUpdate(auditTable);
            stmt.executeUpdate(filterTable);
            stmt.executeUpdate(statsTable);
            stmt.executeUpdate(eventsLogTable);
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to create tables", e);
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null || dataSource.isClosed()) {
            throw new SQLException("DataSource is not initialized or closed");
        }
        return dataSource.getConnection();
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }

    public String getType() {
        return databaseType;
    }

    // Account methods
    public boolean linkAccount(long discordId, UUID minecraftUuid, String minecraftName, String linkedBy) {
        String sql = "INSERT OR REPLACE INTO connector_accounts (discord_id, minecraft_uuid, minecraft_name, linked_at, linked_by) VALUES (?, ?, ?, CURRENT_TIMESTAMP, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, discordId);
            stmt.setString(2, minecraftUuid.toString());
            stmt.setString(3, minecraftName);
            stmt.setString(4, linkedBy);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to link account", e);
            return false;
        }
    }

    public boolean unlinkAccount(long discordId) {
        String sql = "DELETE FROM connector_accounts WHERE discord_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, discordId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to unlink account", e);
            return false;
        }
    }

    public boolean unlinkAccountByUUID(UUID minecraftUuid) {
        String sql = "DELETE FROM connector_accounts WHERE minecraft_uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, minecraftUuid.toString());
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to unlink account", e);
            return false;
        }
    }

    public Long getDiscordId(UUID minecraftUuid) {
        String sql = "SELECT discord_id FROM connector_accounts WHERE minecraft_uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, minecraftUuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("discord_id");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get Discord ID", e);
        }
        return null;
    }

    public Long getDiscordIdByName(String playerName) {
        String sql = "SELECT discord_id FROM connector_accounts WHERE minecraft_name = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playerName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong("discord_id");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get Discord ID by name", e);
        }
        return null;
    }

    public boolean isLinkedByName(String playerName) {
        return getDiscordIdByName(playerName) != null;
    }

    public UUID getMinecraftUuid(long discordId) {
        String sql = "SELECT minecraft_uuid FROM connector_accounts WHERE discord_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, discordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return UUID.fromString(rs.getString("minecraft_uuid"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get Minecraft UUID", e);
        }
        return null;
    }

    public String getMinecraftName(long discordId) {
        String sql = "SELECT minecraft_name FROM connector_accounts WHERE discord_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, discordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("minecraft_name");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get Minecraft name", e);
        }
        return null;
    }

    public boolean isLinked(UUID minecraftUuid) {
        return getDiscordId(minecraftUuid) != null;
    }

    public boolean isLinkedByDiscord(long discordId) {
        return getMinecraftUuid(discordId) != null;
    }

    public int getLinkedCount() {
        String sql = "SELECT COUNT(*) FROM connector_accounts";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get linked count", e);
        }
        return 0;
    }

    // Audit methods
    public void logAudit(String level, String action, String user, String userType, String details, String ip) {
        String sql = "INSERT INTO connector_audit_log (level, action, user, user_type, details, ip) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, level);
            stmt.setString(2, action);
            stmt.setString(3, user);
            stmt.setString(4, userType);
            stmt.setString(5, details);
            stmt.setString(6, ip);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to log audit", e);
        }
    }

    // Filter warning methods
    public void addFilterWarning(UUID uuid, String filterType, int level) {
        String sql = "INSERT INTO connector_filter_warnings (uuid, filter_type, level, timestamp) VALUES (?, ?, ?, CURRENT_TIMESTAMP)";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, filterType);
            stmt.setInt(3, level);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to add filter warning", e);
        }
    }

    public int getFilterWarningCount(UUID uuid, String filterType) {
        String sql = "SELECT COUNT(*) FROM connector_filter_warnings WHERE uuid = ? AND filter_type = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, filterType);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get filter warning count", e);
        }
        return 0;
    }

    public void clearFilterWarnings(UUID uuid, String filterType) {
        String sql = "DELETE FROM connector_filter_warnings WHERE uuid = ? AND filter_type = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, filterType);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to clear filter warnings", e);
        }
    }

    public boolean hasPlayedBefore(UUID uuid) {
        String sql = "SELECT 1 FROM connector_player_stats WHERE uuid = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            return false;
        }
    }

    public String getBirthday(long discordId) {
        String sql = "SELECT birthday FROM connector_accounts WHERE discord_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, discordId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("birthday");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to get birthday", e);
        }
        return null;
    }

    public boolean setBirthday(long discordId, String birthday) {
        String sql = "UPDATE connector_accounts SET birthday = ? WHERE discord_id = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, birthday);
            stmt.setLong(2, discordId);
            stmt.executeUpdate();
            return true;
        } catch (SQLException e) {
            plugin.getLogger().log(Level.SEVERE, "Failed to set birthday", e);
            return false;
        }
    }
}
