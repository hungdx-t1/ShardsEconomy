package mino.dx.curseletcraft;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import mino.dx.curseletcraft.api.interfaces.IShards;
import mino.dx.curseletcraft.commands.ShardsCmd;
import mino.dx.curseletcraft.config.Config;
import mino.dx.curseletcraft.database.DatabaseManager;
import mino.dx.curseletcraft.handler.CacheHandler;
import mino.dx.curseletcraft.hooks.RegisterPlaceholder;
import mino.dx.curseletcraft.listeners.CacheListener;
import mino.dx.curseletcraft.listeners.ShardChangedListener;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@SuppressWarnings("unused")
public final class ShardsEconomy extends JavaPlugin {

    private IShards shardManager;
    private DatabaseManager databaseManager;
    private CacheHandler cacheHandler;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        Config.init(this);

        try {
            databaseManager = new DatabaseManager(this);
            shardManager = databaseManager.getShardsManager();
        } catch (Exception e) {
            getLogger().severe("Plugin khởi tạo thất bại: " + e.getMessage());
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        this.cacheHandler = new CacheHandler(this);

        getServer().getPluginManager().registerEvents(new CacheListener(this.cacheHandler), this);
        getServer().getPluginManager().registerEvents(new ShardChangedListener(this), this);

        this.getLifecycleManager().registerEventHandler(
                LifecycleEvents.COMMANDS,
                event ->
                        event.registrar().register(
                                ShardsCmd.build(this).build(),
                                "Shards sub commands",
                                List.of("shard", "shardsx")));

        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null) {
            Bukkit.getScheduler().runTask(this, () -> new RegisterPlaceholder(this).register());
        }

        getLogger().info("ShardsEconomy đã được bật thành công!");
    }

    @Override
    public void onDisable() {
        if(shardManager != null) {
            shardManager.close();
        }
    }

    public IShards getShardsManager() {
        return shardManager;
    }

    public ShardsEconomy getPlugin() {
        return this;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public CacheHandler getCacheHandler() {
        return cacheHandler;
    }
}
