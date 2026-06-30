package com.makrozai.eligiusconnector.stats;

import com.makrozai.eligiusconnector.EligiusConnector;

import java.sql.*;
import java.util.UUID;

public class PlayerStatsManager {

    private final EligiusConnector plugin;

    public PlayerStatsManager(EligiusConnector plugin) {
        this.plugin = plugin;
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS connector_player_stats (" +
                "uuid VARCHAR(36) PRIMARY KEY, " +
                "player_name VARCHAR(16), " +
                "first_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "last_join TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "total_playtime BIGINT DEFAULT 0, " +
                "kills INT DEFAULT 0, " +
                "deaths INT DEFAULT 0, " +
                "kills_player INT DEFAULT 0, " +
                "deaths_player INT DEFAULT 0, " +
                "kills_mob INT DEFAULT 0, " +
                "deaths_mob INT DEFAULT 0, " +
                "blocks_placed INT DEFAULT 0, " +
                "blocks_broken INT DEFAULT 0, " +
                "items_crafted INT DEFAULT 0, " +
                "distance_walked BIGINT DEFAULT 0, " +
                "jumps INT DEFAULT 0" +
                ")";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            // Add missing columns for existing tables
            addColumnIfMissing(stmt, "connector_player_stats", "last_join", "TIMESTAMP DEFAULT CURRENT_TIMESTAMP");
            addColumnIfMissing(stmt, "connector_player_stats", "kills_player", "INT DEFAULT 0");
            addColumnIfMissing(stmt, "connector_player_stats", "deaths_player", "INT DEFAULT 0");
            addColumnIfMissing(stmt, "connector_player_stats", "kills_mob", "INT DEFAULT 0");
            addColumnIfMissing(stmt, "connector_player_stats", "deaths_mob", "INT DEFAULT 0");
            addColumnIfMissing(stmt, "connector_player_stats", "blocks_placed", "INT DEFAULT 0");
            addColumnIfMissing(stmt, "connector_player_stats", "blocks_broken", "INT DEFAULT 0");
            addColumnIfMissing(stmt, "connector_player_stats", "items_crafted", "INT DEFAULT 0");
            addColumnIfMissing(stmt, "connector_player_stats", "distance_walked", "BIGINT DEFAULT 0");
            addColumnIfMissing(stmt, "connector_player_stats", "jumps", "INT DEFAULT 0");
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to create player_stats table: " + e.getMessage());
        }
    }

    private void addColumnIfMissing(Statement stmt, String table, String column, String type) {
        try {
            stmt.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + type);
        } catch (SQLException ignored) {
            // Column already exists
        }
    }

    public void updateJoin(UUID uuid, String playerName) {
        String sql = "INSERT INTO connector_player_stats (uuid, player_name, last_join) " +
                "VALUES (?, ?, CURRENT_TIMESTAMP) " +
                "ON CONFLICT(uuid) DO UPDATE SET last_join = CURRENT_TIMESTAMP, player_name = ?";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.setString(2, playerName);
            stmt.setString(3, playerName);
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to update join: " + e.getMessage());
        }
    }

    public void addPlaytime(UUID uuid, long seconds) {
        String sql = "UPDATE connector_player_stats SET total_playtime = total_playtime + ? WHERE uuid = ?";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setLong(1, seconds);
            stmt.setString(2, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to add playtime: " + e.getMessage());
        }
    }

    public void addKill(UUID uuid, String type) {
        String column = type.equals("player") ? "kills_player" : "kills_mob";
        String sql = "UPDATE connector_player_stats SET kills = kills + 1, " + column + " = " + column + " + 1 WHERE uuid = ?";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to add kill: " + e.getMessage());
        }
    }

    public void addDeath(UUID uuid, String type) {
        String column = type.equals("player") ? "deaths_player" : "deaths_mob";
        String sql = "UPDATE connector_player_stats SET deaths = deaths + 1, " + column + " = " + column + " + 1 WHERE uuid = ?";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to add death: " + e.getMessage());
        }
    }

    public void addBlockPlaced(UUID uuid) {
        String sql = "UPDATE connector_player_stats SET blocks_placed = blocks_placed + 1 WHERE uuid = ?";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to add block placed: " + e.getMessage());
        }
    }

    public void addBlockBroken(UUID uuid) {
        String sql = "UPDATE connector_player_stats SET blocks_broken = blocks_broken + 1 WHERE uuid = ?";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to add block broken: " + e.getMessage());
        }
    }

    public void addItemCrafted(UUID uuid) {
        String sql = "UPDATE connector_player_stats SET items_crafted = items_crafted + 1 WHERE uuid = ?";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to add item crafted: " + e.getMessage());
        }
    }

    public PlayerStats getStats(UUID uuid) {
        String sql = "SELECT * FROM connector_player_stats WHERE uuid = ?";

        try (Connection conn = plugin.getDatabaseManager().getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, uuid.toString());
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new PlayerStats(
                        UUID.fromString(rs.getString("uuid")),
                        rs.getString("player_name"),
                        rs.getTimestamp("first_join"),
                        rs.getTimestamp("last_join"),
                        rs.getLong("total_playtime"),
                        rs.getInt("kills"),
                        rs.getInt("deaths"),
                        rs.getInt("kills_player"),
                        rs.getInt("deaths_player"),
                        rs.getInt("kills_mob"),
                        rs.getInt("deaths_mob"),
                        rs.getInt("blocks_placed"),
                        rs.getInt("blocks_broken"),
                        rs.getInt("items_crafted"),
                        rs.getLong("distance_walked"),
                        rs.getInt("jumps")
                );
            }
        } catch (SQLException e) {
            plugin.getLogger().warning("Failed to get stats: " + e.getMessage());
        }

        return null;
    }

    public static class PlayerStats {
        private final UUID uuid;
        private final String playerName;
        private final Timestamp firstJoin;
        private final Timestamp lastJoin;
        private final long totalPlaytime;
        private final int kills;
        private final int deaths;
        private final int killsPlayer;
        private final int deathsPlayer;
        private final int killsMob;
        private final int deathsMob;
        private final int blocksPlaced;
        private final int blocksBroken;
        private final int itemsCrafted;
        private final long distanceWalked;
        private final int jumps;

        public PlayerStats(UUID uuid, String playerName, Timestamp firstJoin, Timestamp lastJoin,
                          long totalPlaytime, int kills, int deaths, int killsPlayer, int deathsPlayer,
                          int killsMob, int deathsMob, int blocksPlaced, int blocksBroken,
                          int itemsCrafted, long distanceWalked, int jumps) {
            this.uuid = uuid;
            this.playerName = playerName;
            this.firstJoin = firstJoin;
            this.lastJoin = lastJoin;
            this.totalPlaytime = totalPlaytime;
            this.kills = kills;
            this.deaths = deaths;
            this.killsPlayer = killsPlayer;
            this.deathsPlayer = deathsPlayer;
            this.killsMob = killsMob;
            this.deathsMob = deathsMob;
            this.blocksPlaced = blocksPlaced;
            this.blocksBroken = blocksBroken;
            this.itemsCrafted = itemsCrafted;
            this.distanceWalked = distanceWalked;
            this.jumps = jumps;
        }

        public UUID getUuid() { return uuid; }
        public String getPlayerName() { return playerName; }
        public Timestamp getFirstJoin() { return firstJoin; }
        public Timestamp getLastJoin() { return lastJoin; }
        public long getTotalPlaytime() { return totalPlaytime; }
        public int getKills() { return kills; }
        public int getDeaths() { return deaths; }
        public int getKillsPlayer() { return killsPlayer; }
        public int getDeathsPlayer() { return deathsPlayer; }
        public int getKillsMob() { return killsMob; }
        public int getDeathsMob() { return deathsMob; }
        public int getBlocksPlaced() { return blocksPlaced; }
        public int getBlocksBroken() { return blocksBroken; }
        public int getItemsCrafted() { return itemsCrafted; }
        public long getDistanceWalked() { return distanceWalked; }
        public int getJumps() { return jumps; }

        public double getKDR() {
            return deaths == 0 ? kills : (double) kills / deaths;
        }
    }
}
