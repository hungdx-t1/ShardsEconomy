package mino.dx.curseletcraft.commands.handlers;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.utils.PluginUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class BalanceShardHandler {
    public static void execute(ShardsEconomy plugin, CommandSender sender, String targetName) {
        OfflinePlayer target = Bukkit.getOfflinePlayer(targetName);
        UUID uuid = target.getUniqueId();

        plugin.getShardsManager().getShards(uuid).thenAccept(
                bal -> sender.sendMessage(PluginUtils.formatMessage("&e" + target.getName() + " &ahiện đang có &d" + bal + " &aShard."))
        );
    }
}
