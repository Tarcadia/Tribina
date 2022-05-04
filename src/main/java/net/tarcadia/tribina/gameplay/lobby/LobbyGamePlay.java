package net.tarcadia.tribina.gameplay.lobby;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.tarcadia.tribina.gameplay.GamePlay;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LobbyGamePlay implements GamePlay {

    private static Instance lobbyWorld;
    public static final Logger LOGGER = LoggerFactory.getLogger(LobbyGamePlay.class);

    static {
        init();
    }

    @NotNull
    public static Instance getLobbyWorld() {
        return lobbyWorld;
    }

    public static synchronized void init() {

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        lobbyWorld = instanceManager.createInstanceContainer();
        lobbyWorld.setGenerator(unit ->
                unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

    }

    public final Player player;

    public LobbyGamePlay(@NotNull Player player) {
        this.player = player;
        this.load();
    }

    public void start_no_respawn(@NotNull Player player) {
        player.setGameMode(GameMode.SPECTATOR);
        player.setAutoViewable(false);
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    @Override
    public void start() {
        start_no_respawn(player);
        player.setInstance(lobbyWorld);
        player.setRespawnPoint(new Pos(0, 42, 0));
    }

    @Override
    public void end() {

    }
}
