package mino.dx.curseletcraft.hooks;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.utils.PluginUtils;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

public class RegisterPlaceholder extends PlaceholderExpansion {
    private final ShardsEconomy plugin;

    public RegisterPlaceholder(ShardsEconomy plugin) {
        this.plugin = plugin;
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String placeholder) {
        if(player == null) return null;
        long shards = plugin.getCacheHandler().getCachedShards(player.getUniqueId());
        return switch (placeholder.toLowerCase()) {
            case "shards" -> String.valueOf(shards);
            case "shards_formatted" -> PluginUtils.formatWithDot(shards);
            default -> null;
        };
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    @NotNull
    public String getAuthor() {
        return plugin.getPluginMeta().getAuthors().getFirst();
    }

    @Override
    @NotNull
    public String getIdentifier() {
        return "shardsx";
    }

    @Override
    @NotNull
    public String getVersion() {
        return plugin.getPluginMeta().getVersion();
    }
}
