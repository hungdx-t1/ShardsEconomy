package mino.dx.curseletcraft.api.interfaces;

import org.jetbrains.annotations.CheckReturnValue;

import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

// todo rewrite javadocs
@CheckReturnValue
public interface IShards {
    CompletableFuture<Long> getShards(UUID uuid);
    CompletableFuture<Boolean> setShards(UUID uuid, int amount);
    CompletableFuture<Boolean> addShards(UUID uuid, int amount);
    CompletableFuture<Boolean> removeShards(UUID uuid, int amount);
    void close() throws SQLException;
}