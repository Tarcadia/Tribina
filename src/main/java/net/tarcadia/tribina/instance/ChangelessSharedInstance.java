package net.tarcadia.tribina.instance;

import net.minestom.server.coordinate.Point;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.*;
import net.minestom.server.instance.block.Block;
import net.minestom.server.instance.block.BlockHandler;
import net.minestom.server.instance.generator.Generator;
import net.minestom.server.utils.async.AsyncUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class ChangelessSharedInstance extends SharedInstance {

    public ChangelessSharedInstance(@NotNull UUID uniqueId, @NotNull InstanceContainer instanceContainer) {
        super(uniqueId, instanceContainer);
    }

    @Override
    public void setBlock(int x, int y, int z, @NotNull Block block) {}

    @Override
    public boolean placeBlock(@NotNull BlockHandler.Placement placement) {
        return false;
    }

    @Override
    public boolean breakBlock(@NotNull Player player, @NotNull Point blockPosition) {
        return false;
    }

    @Override
    public @NotNull CompletableFuture<Void> saveInstance() {
        return AsyncUtils.VOID_FUTURE;
    }

    @Override
    public @NotNull CompletableFuture<Void> saveChunkToStorage(@NotNull Chunk chunk) {
        return AsyncUtils.VOID_FUTURE;
    }

    @Override
    public @NotNull CompletableFuture<Void> saveChunksToStorage() {
        return AsyncUtils.VOID_FUTURE;
    }

    @Override
    public void setGenerator(@Nullable Generator generator) {}

    @Override
    public void enableAutoChunkLoad(boolean enable) {}

}
