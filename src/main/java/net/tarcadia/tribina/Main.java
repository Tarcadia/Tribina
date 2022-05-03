package net.tarcadia.tribina;

import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.block.Block;
import net.minestom.server.network.ConnectionManager;
import net.tarcadia.tribina.command.StopCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Main {

    public static final int MAX_PLAYER_COUNT = Integer.getInteger("tribina.max-player-count", 100);
    public static final int COMPRESSION_THRESHOLD = Integer.getInteger("minestom.compression-threshold", 256);
    public static final String BRAND_NAME = "Tribina";
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Init the server
        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.setCompressionThreshold(COMPRESSION_THRESHOLD);
        MinecraftServer.setBrandName(BRAND_NAME);

        initServerInfo();
        initCommand();
        initGamePlay();

        // Start the server on port 25565
        server.start("127.0.0.1", 25565);
    }

    public static void initServerInfo() {
        ConnectionManager manager = MinecraftServer.getConnectionManager();
        manager.setUuidProvider((playerConnection, username) ->
                UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)));
        manager.setPlayerProvider(((uuid, username, connection) -> {
            Player player = new Player(uuid, username, connection);
            player.setSkin(PlayerSkin.fromUsername(username));
            return player;
        }));

    }

    public static void initCommand() {
        CommandManager manager = MinecraftServer.getCommandManager();
        manager.setUnknownCommandCallback(((sender, command) -> {
        }));
        manager.register(new StopCommand());
    }

    public static void initGamePlay() {

        // Instances
        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        instanceContainer.setGenerator(unit ->
                unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            event.setSpawningInstance(instanceContainer);
            player.setGameMode(GameMode.SPECTATOR);
            player.setRespawnPoint(new Pos(0, 42, 0));
        });

    }

}
