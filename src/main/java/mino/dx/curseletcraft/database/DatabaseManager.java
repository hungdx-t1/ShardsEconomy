package mino.dx.curseletcraft.database;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.api.interfaces.IShards;
import org.bukkit.configuration.file.FileConfiguration;

public class DatabaseManager {

    private final IShards shardManager;
    private final DatabaseType databaseType;

    public DatabaseManager(ShardsEconomy plugin) {
        FileConfiguration config = plugin.getConfig();

        // true = mysql, false = sqlite
        if (config.getBoolean("database.enable-mysql", false)) {
            databaseType = DatabaseType.MYSQL;
            shardManager = new ShardsManagerMySQL(plugin);
        } else {
            databaseType = DatabaseType.SQLITE;
            shardManager = new ShardsManager(plugin);
        }

        plugin.getLogger().info("Database enabled");
        plugin.getLogger().info("Database type: " + databaseType);
    }

    public IShards getShardsManager() {
        return shardManager;
    }

    @Override
    public String toString() {
        return "Using " + databaseType + " as Database Type.";
    }
}