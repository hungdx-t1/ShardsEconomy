package mino.dx.curseletcraft.utils;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.logging.Level;

@SuppressWarnings("unused")
public class PluginUtils {
    private static final JavaPlugin shardsx = JavaPlugin.getPlugin(mino.dx.curseletcraft.ShardsEconomy.class);
    private static final DecimalFormat dotFormatter;

    static {
        dotFormatter = new DecimalFormat("#,###");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setGroupingSeparator('.'); // Dùng dấu chấm
        dotFormatter.setDecimalFormatSymbols(symbols);
        dotFormatter.setGroupingUsed(true);
    }

    private PluginUtils() {}

    public static void log(String s) {
        shardsx.getLogger().log(Level.INFO, s);
    }

    public static void err(String s) {
        shardsx.getLogger().log(Level.SEVERE, s);
    }

    public static void warn(String s) {
        shardsx.getLogger().log(Level.WARNING, s);
    }

    public static void console(String s) {
        Bukkit.getConsoleSender().sendMessage(s);
    }

    public static Component formatMessage(String msg) {
        return LegacyComponentSerializer.legacyAmpersand().deserialize(msg);
    }

    public static String formatWithDot(long number) {
        return dotFormatter.format(number);
    }
}
