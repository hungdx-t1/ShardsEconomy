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

        IShards shardsManager = plugin.getShardsManager();
        shardsManager.setShards(uuid, 0);

        // call event
        int balance = shardsManager.getShards(uuid);
        ShardsChangedEvent event = new ShardsChangedEvent(target, balance, 0);
        event.callEvent();

        sender.sendMessage(PluginUtils.formatMessage("&aĐã reset &dShard &avề 0 cho người chơi &e" + target.getName()));
    }
}
