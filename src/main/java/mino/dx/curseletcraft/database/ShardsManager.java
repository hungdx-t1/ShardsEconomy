package mino.dx.curseletcraft.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.api.interfaces.IShards;
import mino.dx.curseletcraft.utils.PluginUtils;

import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ShardsManager implements IShards {
    private final HikariDataSource dataSource;

    public ShardsManager(ShardsEconomy plugin) {
        File dataFolder = plugin.getDataFolder();
        File dbFile = new File(dataFolder, "shards.db");

        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:sqlite:" + dbFile.getPath());
        config.setDriverClassName("org.sqlite.JDBC"); // Thỉnh thoảng Hikari cần khai báo rõ cho SQLite

        // BẮT BUỘC cho SQLite để tránh lỗi Database is locked
        config.setMaximumPoolSize(1);
        config.setConnectionTestQuery("SELECT 1");

        this.dataSource = new HikariDataSource(config);

        try {
            createTable();
            PluginUtils.log("SQLite initialized.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize SQLite database", e);
        }
    }

    private void createTable() {
        try (Connection conn = dataSource.getConnection(); Statement stmt = conn.createStatement()) {
            stmt.executeUpdate("""
                CREATE TABLE IF NOT EXISTS shards (
                    uuid TEXT PRIMARY KEY,
                    amount INTEGER NOT NULL
                );
            """);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create table shards", e);
        }
    }

    @Override
    public CompletableFuture<Long> getShards(UUID uuid) {
        return CompletableFuture.supplyAsync(() -> {
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("SELECT amount FROM shards WHERE uuid = ?")) {
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
            try (Connection conn = dataSource.getConnection();
                 PreparedStatement ps = conn.prepareStatement("""
            INSERT INTO shards (uuid, amount)
            VALUES (?, ?)
            ON CONFLICT(uuid) DO UPDATE SET amount = excluded.amount
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
        return getShards(uuid).thenCompose(cur -> setShards(uuid, (int) (cur + amount)));
    }

    @Override
    public CompletableFuture<Boolean> removeShards(UUID uuid, int amount) {
        return getShards(uuid).thenCompose(cur -> setShards(uuid, (int) Math.max(0, cur - amount)));
    }

    public void close() {
        if (dataSource != null && !dataSource.isClosed()) {
            dataSource.close();
        }
    }
}
