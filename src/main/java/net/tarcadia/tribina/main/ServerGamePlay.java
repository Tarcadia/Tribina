package net.tarcadia.tribina.main;

import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerDisconnectEvent;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.extras.MojangAuth;
import net.minestom.server.network.ConnectionManager;
import net.tarcadia.tribina.gameplay.GamePlay;
import net.tarcadia.tribina.gameplay.lobby.LobbyGamePlay;
import net.tarcadia.tribina.gameplay.tribina.TribinaGamePlay;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ServerGamePlay {

    private static final Map<UUID, GamePlay> playerGamePlay = new ConcurrentHashMap<>();

    @Nullable
    public static GamePlay getGamePlay(@NotNull Player player) {
        return playerGamePlay.get(player.getUuid());
    }

    public static void setGamePlay(@NotNull Player player, @NotNull GamePlay gamePlay) {
        GamePlay preGamePlay = playerGamePlay.get(player.getUuid());
        if (preGamePlay != null) preGamePlay.end();
        playerGamePlay.put(player.getUuid(), gamePlay);
        gamePlay.start();
    }

    public static void removeGamePlay(@NotNull Player player) {
        playerGamePlay.remove(player.getUuid());
    }

    public static void saveGamePlay(@NotNull Player player) {
        GamePlay gamePlay = playerGamePlay.get(player.getUuid());
        if (gamePlay != null) gamePlay.save();
    }

    public static void endGamePlay(@NotNull Player player) {
        GamePlay gamePlay = playerGamePlay.get(player.getUuid());
        if (gamePlay != null) gamePlay.end();
    }

    public static void saveAllGamePlay() {
        for (GamePlay gamePlay : playerGamePlay.values()) {
            gamePlay.save();
        }
    }

    public static void endAllGamePlay() {
        for (GamePlay gamePlay : playerGamePlay.values()) {
            gamePlay.end();
        }
    }

    public static void initServerGamePlay() {

        LobbyGamePlay.init();
        TribinaGamePlay.init();

        if (!MojangAuth.isEnabled()) {
            ConnectionManager manager = MinecraftServer.getConnectionManager();
            manager.setUuidProvider((playerConnection, username) -> UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)));
            manager.setPlayerProvider((uuid, username, connection) -> {
                Player player = new Player(uuid, username, connection);
                player.setSkin(PlayerSkin.fromUsername(username));
                return player;
            });
        }

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            Player player = event.getPlayer();
            ServerGamePlay.setGamePlay(player, new LobbyGamePlay(event));
        });

        GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();
        handler.addListener(PlayerDisconnectEvent.class, event -> {
            Player player = event.getPlayer();
            saveGamePlay(player);
            endGamePlay(player);
            removeGamePlay(player);
        });
    }

}
