package mino.dx.curseletcraft.commands.handlers;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.api.events.ShardsChangedEvent;
import mino.dx.curseletcraft.api.interfaces.IShards;
import mino.dx.curseletcraft.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class SetShardHandler {
    public static void execute(ShardsEconomy plugin, CommandSender sender, String targetName, int amount) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID uuid = target.getUniqueId();
        IShards manager = plugin.getShardsManager();

        manager.getShards(uuid).thenAccept(oldBalance -> manager.setShards(uuid, amount).thenAccept(success -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (success) {
                ShardsChangedEvent event = new ShardsChangedEvent(target, oldBalance, amount);
                Bukkit.getPluginManager().callEvent(event);
                sender.sendMessage(PluginUtils.formatMessage("&aĐã đặt &dShard &acho " + target.getName() + " &avới số lượng &e" + amount));
            } else {
                sender.sendMessage(PluginUtils.formatMessage("&cLỗi hệ thống khi cập nhật Shard cho " + target.getName()));
            }
        })));
    }
}