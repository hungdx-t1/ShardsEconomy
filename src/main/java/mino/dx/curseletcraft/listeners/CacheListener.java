package mino.dx.curseletcraft.listeners;

import mino.dx.curseletcraft.handler.CacheHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class CacheListener implements Listener {
    private final CacheHandler cacheHandler;

    public CacheListener(CacheHandler cacheHandler) {
        this.cacheHandler = cacheHandler;
    }

    @EventHandler
    public void onPlayerPreLogin(AsyncPlayerPreLoginEvent event) {
        // Tải dữ liệu vào RAM ngay khi người chơi đang kết nối
        // (Event này bản thân nó đã chạy ở luồng Async nên rất an toàn)
        cacheHandler.loadPlayerToCache(event.getUniqueId());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Xóa khỏi RAM khi thoát game để chống Memory Leak
        cacheHandler.removePlayerFromCache(event.getPlayer().getUniqueId());
    }
}
