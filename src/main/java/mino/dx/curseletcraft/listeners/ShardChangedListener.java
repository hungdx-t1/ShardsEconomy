package mino.dx.curseletcraft.listeners;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.api.events.ShardsChangedEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

@SuppressWarnings("ClassCanBeRecord")
public class ShardChangedListener implements Listener {
    private final ShardsEconomy plugin;

    public ShardChangedListener(ShardsEconomy plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChangedShards(ShardsChangedEvent event) {
        UUID uuid = event.getPlayerUUID();
        String playerName = event.getPlayerName() == null ? "unknown" : event.getPlayerName(); // tránh NPE

        long oldShards = event.getOldShards();
        long newShards = event.getNewShards();

        plugin.getCacheHandler().setCachedShards(uuid, newShards);

        boolean changedLogEnabled = plugin.getConfig().getBoolean("enable-log-change", true);
        if(changedLogEnabled) {
            String ctx = "Shards changed: {player_name: '%s', player_uuid: '%s', old_shards: '%d', new_shards: '%d'}"
                    .formatted(playerName, uuid, oldShards, newShards);
            plugin.getLogger().info(ctx);
        }
    }
}
