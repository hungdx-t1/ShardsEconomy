package mino.dx.curseletcraft.config;

import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.exceptions.ConfigNotFoundException;
import mino.dx.curseletcraft.utils.PluginUtils;
import org.bukkit.configuration.file.FileConfiguration;

@SuppressWarnings("unused")
public class Config {
    private static FileConfiguration config;

    public static void init(ShardsEconomy plugin) {
        config = plugin.getConfig();
        PluginUtils.log("Đang kiểm tra tính hoàn chỉnh của file config...");
        checkFileConfig();
    }

    public static boolean getBooleanConfig(String key) {
        if (!config.contains(key)) throw new ConfigNotFoundException(key);
        return config.getBoolean(key);
    }

    public static String getStringConfig(String key) {
        if (!config.contains(key)) throw new ConfigNotFoundException(key);
        return config.getString(key);
    }

    public static int getIntConfig(String key) {
        if (!config.contains(key)) throw new ConfigNotFoundException(key);
        return config.getInt(key);
    }

    private static void checkFileConfig() {
        String[] requiredKeys = {
                "database.enable-mysql",
                "database.host",
                "database.port",
                "database.database",
                "database.user",
                "database.password",
                "enable-log-change"
        };

        for (String key : requiredKeys) {
            if (!config.contains(key)) {
                PluginUtils.warn("&c[!] Thiếu cấu hình: " + key);
            }
        }

        PluginUtils.log("File config đã được kiểm tra. " +
                "Nếu thiếu cấu hình, vui lòng kiểm tra file config.yml trong plugin này " +
                "bằng ứng dụng 7Zip hoặc WinRAR.");
    }
}
