package mino.dx.curseletcraft.commands.handlers;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.api.events.ShardsChangedEvent;
import mino.dx.curseletcraft.api.interfaces.IShards;
import mino.dx.curseletcraft.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
        manager.addShards(target.getUniqueId(), amount); // <-- dòng này, chỉ chạy sync

        // call event
        int balance = manager.getShards(target.getUniqueId());
        ShardsChangedEvent event = new ShardsChangedEvent(target, balance, balance + amount);
        event.callEvent();

        sender.sendMessage(PluginUtils.formatMessage("&aĐã thêm &f" + amount + " &dShard &acho &f" + target.getName()));

        if (target.isOnline()) {
            Player onlineTarget = (Player) target;
            onlineTarget.sendMessage(PluginUtils.formatMessage("&eBạn đã nhận được &f" + amount + " &dShard &etừ Admin."));
        }
    }
}
