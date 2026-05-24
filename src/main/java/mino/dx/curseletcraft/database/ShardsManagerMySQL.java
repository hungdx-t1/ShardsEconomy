package mino.dx.curseletcraft.database;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.api.interfaces.IShards;
import mino.dx.curseletcraft.utils.PluginUtils;

import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ShardsManagerMySQL implements IShards {
    private final Connection connection;

    public ShardsManagerMySQL(ShardsEconomy plugin) {
        try {
            String host = plugin.getConfig().getString("database.host");
            String port = plugin.getConfig().getString("database.port");
            String database = plugin.getConfig().getString("database.database");
            String user = plugin.getConfig().getString("database.user");
            String password = plugin.getConfig().getString("database.password");

            String url = "jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false&autoReconnect=true";
            connection = DriverManager.getConnection(url, user, password);

            createTable();
            PluginUtils.log("ShardManager MySQL initialized.");
        } catch (SQLException e) {
            throw new RuntimeException(("Failed to connect to MySQL database: " + e.getMessage()));
        }
    }

    private void createTable() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS shards (
                    uuid VARCHAR(36) PRIMARY KEY,
                    amount INT NOT NULL
                );
            """);
        }
    }

    @Override
    public CompletableFuture<Long> getShards(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement("SELECT amount FROM shards WHERE uuid = ?")) {
            ps.setString(1, uuid.toString());
            ResultSet rs = ps.executeQuery();
            return rs.next() ? rs.getInt("amount") : 0;
        } catch (SQLException e) {
            PluginUtils.err(e.getMessage());
            return 0;
        }
    }

    @Override
    public CompletableFuture<Boolean> setShards(UUID uuid, int amount) {
        try (PreparedStatement ps = connection.prepareStatement("""
            INSERT INTO shards (uuid, amount)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE amount = VALUES(amount)
        """)) {
            ps.setString(1, uuid.toString());
            ps.setInt(2, Math.max(0, amount));
            ps.executeUpdate();
        } catch (SQLException e) {
            PluginUtils.err(e.getMessage());
        }
        return null;
    }

    @Override
    public CompletableFuture<Boolean> addShards(UUID uuid, int amount) {
        int current = getShards(uuid);
        setShards(uuid, current + amount);
        return null;
    }

    @Override
    public CompletableFuture<Boolean> removeShards(UUID uuid, int amount) {
        int current = getShards(uuid);
        setShards(uuid, Math.max(0, current - amount));
        return null;
    }

    public void close() throws SQLException {
        if (!connection.isClosed()) connection.close();
    }
}
