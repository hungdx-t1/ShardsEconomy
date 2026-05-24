package mino.dx.curseletcraft.commands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import mino.dx.curseletcraft.ShardsEconomy;
import mino.dx.curseletcraft.commands.handlers.*;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.concurrent.CompletableFuture;

import static io.papermc.paper.command.brigadier.Commands.argument;
import static io.papermc.paper.command.brigadier.Commands.literal;

public class ShardsCmd {
    public static LiteralArgumentBuilder<CommandSourceStack> build(ShardsEconomy plugin) {
        return literal("shards")
                .requires(src -> src.getSender().hasPermission("shardsx.use"))
                .then(literal("give")
                        .requires(src -> src.getSender().hasPermission("shardsx.admin"))
                        .then(argument("amount", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                    CommandSender sender = ctx.getSource().getSender();
                                    AddShardHandler.execute(plugin, sender, amount, null);
                                    return 1;
                                })
                                .then(argument("player", StringArgumentType.word())
                                        .suggests(ShardsCmd::getPlayersSuggestions)
                                        .executes(ctx -> {
                                            String playerName = ctx.getArgument("player", String.class);
                                            int amount = ctx.getArgument("amount", Integer.class);
                                            CommandSender sender = ctx.getSource().getSender();
                                            AddShardHandler.execute(plugin, sender, amount, playerName);
                                            return 1;
                                        })
                                )
                        )
                )

                // /shardsx balance <player>
                .then(literal("balance")
                        .then(argument("target-player", StringArgumentType.word())
                                .suggests(ShardsCmd::getPlayersSuggestions)
                                .executes(ctx -> {
                                    String target = StringArgumentType.getString(ctx, "target-player");
                                    CommandSender sender = ctx.getSource().getSender();
                                    BalanceShardHandler.execute(plugin, sender, target);
                                    return 1;
                                })
                        )
                )

                // /shardsx remove <player> <amount>
                .then(literal("remove")
                        .requires(src -> src.getSender().hasPermission("shardsx.admin"))
                        .then(argument("target-player", StringArgumentType.word())
                                .suggests(ShardsCmd::getPlayersSuggestions)
                                .then(argument("amount", IntegerArgumentType.integer(1))
                                        .executes(ctx -> {
                                            String target = StringArgumentType.getString(ctx, "target-player");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            CommandSender sender = ctx.getSource().getSender();
                                            TakeShardHandler.execute(plugin, sender, target, amount);
                                            return 1;
                                        })
                                )
                        )
                )

                // /shardsx reset <player>
                .then(literal("reset")
                        .requires(src -> src.getSender().hasPermission("shardsx.admin"))
                        .then(argument("target-player", StringArgumentType.word())
                                .suggests(ShardsCmd::getPlayersSuggestions)
                                .executes(ctx -> {
                                    String target = StringArgumentType.getString(ctx, "target-player");
                                    CommandSender sender = ctx.getSource().getSender();
                                    ResetShardHandler.execute(plugin, sender, target);
                                    return 1;
                                })
                        )
                )

                // /shardsx set <player> <amount>
                .then(literal("set")
                        .requires(src -> src.getSender().hasPermission("shardsx.admin"))
                        .then(argument("target-player", StringArgumentType.word())
                                .suggests(ShardsCmd::getPlayersSuggestions)
                                .then(argument("amount", IntegerArgumentType.integer(0))
                                        .executes(ctx -> {
                                            String target = StringArgumentType.getString(ctx, "target-player");
                                            int amount = IntegerArgumentType.getInteger(ctx, "amount");
                                            CommandSender sender = ctx.getSource().getSender();
                                            SetShardHandler.execute(plugin, sender, target, amount);
                                            return 1;
                                        })
                                )
                        )
                );
    }

    private static CompletableFuture<Suggestions> getPlayersSuggestions(final CommandContext<CommandSourceStack> ctx, final SuggestionsBuilder builder) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            builder.suggest(player.getName());
        }

        return builder.buildFuture();
    }
}
