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
            PluginUtils.log("MySQL initialized.");
        } catch (Exception e) {
            throw new RuntimeException(("Failed to initialize MySQL database: " + e.getMessage()));
        }
    }

    private void createTable() {
        try (Statement stmt = connection.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS shards (
                    uuid VARCHAR(36) PRIMARY KEY,
                    amount INT NOT NULL
                );
            """);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create table shards", e);
        }
    }

    @Override
    public CompletableFuture<Long> getShards(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement("SELECT amount FROM shards WHERE uuid = ?")) {
                ps.setString(1, uuid.toString());
                try (ResultSet rs = ps.executeQuery()) {
                    return rs.next() ? rs.getLong("amount") : 0L;
                }
            } catch (SQLException e) {
                PluginUtils.err(e.getMessage());
                return 0L;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> setShards(UUID uuid, int amount) {
        return CompletableFuture.supplyAsync(() -> {
            try (PreparedStatement ps = connection.prepareStatement("""
                INSERT INTO shards (uuid, amount)
                VALUES (?, ?)
                ON DUPLICATE KEY UPDATE amount = VALUES(amount)
            """)) {
                ps.setString(1, uuid.toString());
                ps.setInt(2, Math.max(0, amount));
                ps.executeUpdate();
                return true;
            } catch (SQLException e) {
                PluginUtils.err(e.getMessage());
                return false;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> addShards(UUID uuid, int amount) {
        return getShards(uuid).thenCompose(current -> setShards(uuid, (int) (current + amount)));
    }

    @Override
    public CompletableFuture<Boolean> removeShards(UUID uuid, int amount) {
        return getShards(uuid).thenCompose(current -> setShards(uuid, (int) Math.max(0, current - amount)));
    }

    public void close() throws SQLException {
        if (!connection.isClosed()) connection.close();
    }
}
