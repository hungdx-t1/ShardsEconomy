package mino.dx.curseletcraft.commands.handlers;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.api.events.ShardsChangedEvent;
import mino.dx.curseletcraft.api.interfaces.IShards;
import mino.dx.curseletcraft.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class TakeShardHandler {
    public static void execute(ShardsEconomy plugin, CommandSender sender, String targetName, int amount) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID uuid = target.getUniqueId();
        IShards manager = plugin.getShardsManager();

        manager.getShards(uuid).thenAccept(oldBalance -> {
            long newBalance = Math.max(0, oldBalance - amount);
            manager.setShards(uuid, (int) newBalance).thenAccept(success -> Bukkit.getScheduler().runTask(plugin, () -> {
                if (success) {
                    ShardsChangedEvent event = new ShardsChangedEvent(target, oldBalance, newBalance);
                    Bukkit.getPluginManager().callEvent(event);
                    sender.sendMessage(PluginUtils.formatMessage("&aĐã xóa &e" + amount + " &dShard &akhỏi " + target.getName()));
                } else {
                    sender.sendMessage(PluginUtils.formatMessage("&cLỗi hệ thống khi cập nhật Shard cho " + target.getName()));
                }
            }));
        });
    }
}
