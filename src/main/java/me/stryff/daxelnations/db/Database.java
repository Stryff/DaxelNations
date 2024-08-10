package me.stryff.daxelnations.db;

import me.stryff.daxelnations.DaxelNations;
import me.stryff.daxelnations.color.ColorUtils;
import me.stryff.daxelnations.model.Grant;
import me.stryff.daxelnations.model.PlayerChat;
import me.stryff.daxelnations.model.PlayerNation;
import me.stryff.daxelnations.model.Players;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {
    private final DaxelNations plugin;
    private Connection connection;

    public Database(DaxelNations plugin) {
        this.plugin = plugin;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            String url = "jdbc:mysql://" + plugin.getConfig().getString("data.address") + "/" + plugin.getConfig().getString("data.database") + "?useSSL=false";
            String user = plugin.getConfig().getString("data.username");
            String password = plugin.getConfig().getString("data.password");
            this.connection = DriverManager.getConnection(url, user, password);
            Bukkit.getServer().getConsoleSender().sendMessage(ColorUtils.toConsoleColor("Successfully connected to MySQL!"));
        }
        return this.connection;
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.AQUA + "[DaxelNations] Successfully closed MySQL connection!");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void initializeDatabase() throws SQLException {
        Statement statement = getConnection().createStatement();
        String sql1 = "CREATE TABLE IF NOT EXISTS player_nations(uuid varchar(36) primary key, nation varchar(10), role varchar(10), power_balance INT)";
        statement.execute(sql1);
        String sql2 = "CREATE TABLE IF NOT EXISTS players(uuid VARCHAR(36) PRIMARY KEY, user_name VARCHAR(16), display_name VARCHAR(16), rank VARCHAR(10), first_joined DATETIME, last_seen DATETIME, playtime BIGINT, ip_address VARCHAR(32), currency_balance DECIMAL(15, 2), banned_status BOOLEAN, muted_status BOOLEAN, warn_count INT)";
        statement.execute(sql2);
        String sql3 = "CREATE TABLE IF NOT EXISTS player_stats(uuid VARCHAR(36) PRIMARY KEY, recent_kill VARCHAR(16), kills INT, deaths INT, first_joined DATETIME, last_seen DATETIME, playtime BIGINT, ip_address VARCHAR(32), currency_balance DECIMAL(15, 2), banned_status BOOLEAN, muted_status BOOLEAN, warn_count INT)";
        statement.execute(sql3);
        String sql4 = "CREATE TABLE IF NOT EXISTS grant_logs (id INT AUTO_INCREMENT PRIMARY KEY, player_uuid VARCHAR(36), admin_uuid VARCHAR(36), old_rank VARCHAR(10), new_rank VARCHAR(10), timestamp VARCHAR(100), grant_reason VARCHAR(16), grant_duration VARCHAR(16), grant_status VARCHAR(7))";
        statement.execute(sql4);
        String sql5 = "CREATE TABLE IF NOT EXISTS player_chat(uuid VARCHAR(36) PRIMARY KEY, tag VARCHAR(16), chat_color VARCHAR(8), toggled BOOLEAN)";
        statement.execute(sql5);
        statement.close();
        Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "[DaxelNations] Successfully created Daxel tables!");
    }

    public PlayerNation findPlayerNationByUUID(String uuid) throws SQLException {
        Statement statement = getConnection().createStatement();
        String sql = "SELECT * FROM player_nations WHERE uuid = '" + uuid + "'";
        ResultSet results = statement.executeQuery(sql);
        if (results.next()) {
            String nation = results.getString("nation");
            String role = results.getString("role");
            int power_balance = results.getInt("power_balance");
            return new PlayerNation(uuid, nation, role, power_balance);
        }
        statement.close();
        return null;
    }

    public String stringNationByUUID(String uuid) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        String nation = null;

        try {
            connection = getConnection();
            String sql = "SELECT nation FROM player_nations WHERE uuid = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, uuid);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                nation = resultSet.getString("nation");
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return nation;
    }

    public void createPlayerNation(PlayerNation nation) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO player_nations(uuid, nation, role, power_balance) VALUES (?, ?, ?, ?)");
        statement.setString(1, nation.getUuid());
        statement.setString(2, nation.getNation());
        statement.setString(3, nation.getRole());
        statement.setInt(4, nation.getPowerBalance());
        statement.executeUpdate();
        statement.close();
    }

    public void updatePlayerNation(PlayerNation nation) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("UPDATE player_nations SET nation = ?, role = ?, power_balance = ? WHERE uuid = ?");
        statement.setString(1, nation.getNation());
        statement.setString(2, nation.getRole());
        statement.setInt(3, nation.getPowerBalance());
        statement.setString(4, nation.getUuid());
        statement.executeUpdate();
        statement.close();
    }

    public void createPlayer(Players players) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO players(uuid, user_name, display_name, rank, first_joined, last_seen, playtime, ip_address, currency_balance, banned_status, muted_status, warn_count) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, players.getUuid());
        statement.setString(2, players.getUserName());
        statement.setString(3, players.getDisplayName());
        statement.setString(4, players.getRank());
        statement.setString(5, players.getFirstJoined());
        statement.setString(6, players.getLastSeen());
        statement.setLong(7, players.getPlaytime());
        statement.setString(8, players.getIpAddress());
        statement.setDouble(9, players.getCurrencyBalance());
        statement.setBoolean(10, players.isBannedStatus());
        statement.setBoolean(11, players.isMutedStatus());
        statement.setInt(12, players.getWarnCount());
        statement.executeUpdate();
        statement.close();
    }

    public void updatePlayer(Players players) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("UPDATE players SET user_name = ?, display_name = ?, rank = ?, first_joined = ?, last_seen = ?, playtime = ?, ip_address = ?, currency_balance = ?, banned_status = ?, muted_status = ?, warn_count = ? WHERE uuid = ?");
        statement.setString(1, players.getUserName());
        statement.setString(2, players.getDisplayName());
        statement.setString(3, players.getRank());
        statement.setString(4, players.getFirstJoined());
        statement.setString(5, players.getLastSeen());
        statement.setLong(6, players.getPlaytime());
        statement.setString(7, players.getIpAddress());
        statement.setDouble(8, players.getCurrencyBalance());
        statement.setBoolean(9, players.isBannedStatus());
        statement.setBoolean(10, players.isMutedStatus());
        statement.setInt(11, players.getWarnCount());
        statement.setString(12, players.getUuid());
        statement.executeUpdate();
        statement.close();
    }

    public void createPlayerChat(PlayerChat playerChat) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("INSERT INTO player_chat(uuid, tag, chat_color, toggled) VALUES (?, ?, ?, ?)");
        statement.setString(1, playerChat.getUuid());
        statement.setString(2, playerChat.getTag());
        statement.setString(3, playerChat.getChatColor());
        statement.setBoolean(4, playerChat.getToggled());
        statement.executeUpdate();
        statement.close();
    }

    public void updatePlayerChat(PlayerChat playerChat) throws SQLException {
        PreparedStatement statement = getConnection()
                .prepareStatement("UPDATE player_chat SET tag = ?, chat_color = ?, toggled = ? WHERE uuid = ?");
        statement.setString(1, playerChat.getTag());
        statement.setString(2, playerChat.getChatColor());
        statement.setBoolean(3, playerChat.getToggled());
        statement.setString(4, playerChat.getUuid());
        statement.executeUpdate();
        statement.close();
    }

    public Players getPlayerByUUID(String uuid) throws SQLException {
        Players player = null;
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sql = "SELECT * FROM players WHERE uuid = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, uuid);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                player = new Players(
                        resultSet.getString("uuid"),
                        resultSet.getString("user_name"),
                        resultSet.getString("display_name"),
                        resultSet.getString("rank"),
                        resultSet.getString("first_joined"),
                        resultSet.getString("last_seen"),
                        resultSet.getLong("playtime"),
                        resultSet.getString("ip_address"),
                        resultSet.getDouble("currency_balance"),
                        resultSet.getBoolean("banned_status"),
                        resultSet.getBoolean("muted_status"),
                        resultSet.getInt("warn_count")
                );
            }
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return player;
    }

    public void logGrants(String playerUUID, String adminUUID, String oldRank, String newRank, Timestamp timestamp, String reason, String duration, String status) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement(
                "INSERT INTO grant_logs (player_uuid, admin_uuid, old_rank, new_rank, timestamp, grant_reason, grant_duration, grant_status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
        statement.setString(1, playerUUID);
        statement.setString(2, adminUUID);
        statement.setString(3, oldRank);
        statement.setString(4, newRank);
        statement.setString(5, timestamp.toString());
        statement.setString(6, reason);
        statement.setString(7, duration);
        statement.setString(8, status);
        statement.executeUpdate();
        statement.close();
    }

    // Method to get grants for a player
    public List<Grant> getGrantsByPlayerUUID(String playerUUID) throws SQLException {
        List<Grant> grants = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sql = "SELECT * FROM grant_logs WHERE player_uuid = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, playerUUID);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Grant grant = new Grant(
                        resultSet.getInt("id"),
                        resultSet.getString("player_uuid"),
                        resultSet.getString("admin_uuid"),
                        resultSet.getString("old_rank"),
                        resultSet.getString("new_rank"),
                        resultSet.getString("timestamp"),
                        resultSet.getString("grant_reason"),
                        resultSet.getString("grant_duration"),
                        resultSet.getString("grant_status")
                );
                grants.add(grant);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return grants;
    }

    public List<Grant> getGrantsByDurationNotLifetime() throws SQLException {
        List<Grant> grants = new ArrayList<>();
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sql = "SELECT * FROM grant_logs WHERE grant_duration != 'Lifetime' AND grant_status = 'active'";
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Grant grant = new Grant(
                        resultSet.getInt("id"),
                        resultSet.getString("player_uuid"),
                        resultSet.getString("admin_uuid"),
                        resultSet.getString("old_rank"),
                        resultSet.getString("new_rank"),
                        resultSet.getString("timestamp"),
                        resultSet.getString("grant_reason"),
                        resultSet.getString("grant_duration"),
                        resultSet.getString("grant_status")
                );
                grants.add(grant);
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return grants;
    }

    public boolean isGrantRevoked(String playerUUID, String newRank, String timestamp) throws SQLException {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        boolean isRevoked = false;

        try {
            connection = getConnection();
            String sql = "SELECT grant_status FROM grant_logs WHERE player_uuid = ? AND new_rank = ? AND timestamp = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, playerUUID);
            statement.setString(2, newRank);
            statement.setString(3, timestamp);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                isRevoked = "revoked".equalsIgnoreCase(resultSet.getString("grant_status"));
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return isRevoked;
    }


    public void revokeGrant(String playerUUID, String newRank, String timestamp) throws SQLException {
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            String sql = "UPDATE grant_logs SET grant_status = 'revoked' WHERE player_uuid = ? AND new_rank = ? AND timestamp = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, playerUUID);
            statement.setString(2, newRank);
            statement.setString(3, timestamp);
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    public void expireGrant(String playerUUID, String newRank, String timestamp) throws SQLException {
        PreparedStatement statement = null;

        try {
            connection = getConnection();
            String sql = "UPDATE grant_logs SET grant_status = 'expired' WHERE player_uuid = ? AND new_rank = ? AND timestamp = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, playerUUID);
            statement.setString(2, newRank);
            statement.setString(3, timestamp);
            statement.executeUpdate();
        } finally {
            if (statement != null) {
                statement.close();
            }
        }
    }

    // New method to get a specific grant
    public Grant getGrant(String adminUUID, String targetUUID, String timestamp) throws SQLException {
        Grant grant = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sql = "SELECT * FROM grant_logs WHERE admin_uuid = ? AND player_uuid = ? AND timestamp = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, adminUUID);
            statement.setString(2, targetUUID);
            statement.setString(3, timestamp);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                grant = new Grant(
                        resultSet.getInt("id"),
                        resultSet.getString("player_uuid"),
                        resultSet.getString("admin_uuid"),
                        resultSet.getString("old_rank"),
                        resultSet.getString("new_rank"),
                        resultSet.getString("timestamp"),
                        resultSet.getString("grant_reason"),
                        resultSet.getString("grant_duration"),
                        resultSet.getString("grant_status")
                );
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return grant;
    }

    public PlayerChat getPlayerChat(String uuid) throws SQLException {
        PlayerChat playerChat = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String sql = "SELECT * FROM player_chat WHERE uuid = ?";
            statement = connection.prepareStatement(sql);
            statement.setString(1, uuid);
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                playerChat = new PlayerChat(
                        resultSet.getString("uuid"),
                        resultSet.getString("tag"),
                        resultSet.getString("chat_color"),
                        resultSet.getBoolean("toggled")
                );
            }
        } finally {
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
        }

        return playerChat;
    }
}
