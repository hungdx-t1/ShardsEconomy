package mino.dx.curseletcraft.handler;

import mino.dx.curseletcraft.ShardsEconomy;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CacheHandler {
    private final Map<UUID, Long> shardsCache = new ConcurrentHashMap<>();
    private final ShardsEconomy plugin;

    public CacheHandler(ShardsEconomy plugin) {
        this.plugin = plugin;
    }

    // Khi người chơi join game, gọi hàm này
    public void loadPlayerToCache(UUID uuid) {
        plugin.getShardsManager().getShards(uuid).thenAccept(shards -> shardsCache.put(uuid, shards));
    }

    // Khi người chơi thoát game, xóa cache để giải phóng RAM
    public void removePlayerFromCache(UUID uuid) {
        shardsCache.remove(uuid);
    }

    // Hàm mới để PAPI gọi (Lấy data ngay lập tức không cần DB)
    public long getCachedShards(UUID uuid) {
        return shardsCache.getOrDefault(uuid, 0L);
    }

    public void setCachedShards(UUID uuid, long amount) {
        if (shardsCache.containsKey(uuid)) { // Chỉ update nếu họ đang online
            shardsCache.put(uuid, amount);
        }
    }
}
