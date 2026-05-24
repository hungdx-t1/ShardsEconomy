package mino.dx.curseletcraft.commands.handlers;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.api.events.ShardsChangedEvent;
import mino.dx.curseletcraft.api.interfaces.IShards;
import mino.dx.curseletcraft.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class ResetShardHandler {
    public static void execute(ShardsEconomy plugin, CommandSender sender, String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID uuid = target.getUniqueId();
        IShards manager = plugin.getShardsManager();

        manager.getShards(uuid).thenAccept(oldBalance -> manager.setShards(uuid, 0).thenAccept(success -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (success) {
                ShardsChangedEvent event = new ShardsChangedEvent(target, oldBalance, 0);
                Bukkit.getPluginManager().callEvent(event);
                sender.sendMessage(PluginUtils.formatMessage("&aĐã reset &dShard &avề 0 cho người chơi &e" + target.getName()));
            } else {
                sender.sendMessage(PluginUtils.formatMessage("&cLỗi hệ thống khi cập nhật Shard cho " + target.getName()));
            }
        })));
    }
}
