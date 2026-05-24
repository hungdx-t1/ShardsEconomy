package mino.dx.curseletcraft.commands.handlers;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.api.events.ShardsChangedEvent;
import mino.dx.curseletcraft.api.interfaces.IShards;
import mino.dx.curseletcraft.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class AddShardHandler {
    public static void execute(ShardsEconomy plugin, CommandSender sender, int amount, String tarPlayer) {
        OfflinePlayer target;
        // Nếu như không có đề cập tarPlayer thì sẽ thêm shard cho người sử dụng lệnh
        if (tarPlayer == null) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(PluginUtils.formatMessage("&cConsole phải chỉ định người chơi cụ thể!"));
                return;
            }
            target = (Player) sender;
        } else {
            target = Bukkit.getOfflinePlayer(tarPlayer);
        }

        if (target.getName() == null) {
            sender.sendMessage(PluginUtils.formatMessage("&cKhông tìm thấy người chơi có tên: &f" + tarPlayer));
            return;
        }

        IShards manager = plugin.getShardsManager();
        UUID uuid = target.getUniqueId();
        String name = target.getName();

        manager.getShards(uuid).thenAccept(oldBalance -> manager.addShards(uuid, amount).thenAccept(success -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (success) {
                ShardsChangedEvent event = new ShardsChangedEvent(target, oldBalance, oldBalance + amount);
                Bukkit.getPluginManager().callEvent(event);
                sender.sendMessage(PluginUtils.formatMessage("&aĐã thêm &f" + amount + " &dShard &acho &f" + name));

                if (target.isOnline()) {
                    Player onlineTarget = (Player) target;
                    onlineTarget.sendMessage(PluginUtils.formatMessage("&eBạn đã nhận được &f" + amount + " &dShard &etừ Admin."));
                }
            } else {
                sender.sendMessage(PluginUtils.formatMessage("&cLỗi hệ thống khi cập nhật Shard cho " + name));
            }
        })));
    }
}
