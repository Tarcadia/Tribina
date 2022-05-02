package net.tarcadia.tribina;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.Player;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.InstanceManager;
import net.minestom.server.instance.SharedInstance;
import net.minestom.server.instance.block.Block;
import net.tarcadia.tribina.instance.ChangelessSharedInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static final int compressionThreshold = Integer.getInteger("minestom.compression-threshold", 256);
    private static final String brandName = "Tribina";

    public static void main(String[] args) {

        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.setCompressionThreshold(compressionThreshold);
        MinecraftServer.setBrandName(brandName);

        InstanceManager instanceManager = MinecraftServer.getInstanceManager();
        InstanceContainer instanceContainer = instanceManager.createInstanceContainer();

        instanceContainer.setGenerator(unit ->
                unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));

        GlobalEventHandler globalEventHandler = MinecraftServer.getGlobalEventHandler();
        globalEventHandler.addListener(PlayerLoginEvent.class, event -> {
            final Player player = event.getPlayer();
            SharedInstance lobbyInstance = new ChangelessSharedInstance(UUID.randomUUID(), instanceContainer);
            event.setSpawningInstance(instanceManager.registerSharedInstance(lobbyInstance));
            player.setRespawnPoint(new Pos(0, 42, 0));
        });
        // Start the server on port 25565
        server.start("127.0.0.1", 25565);
    }
}
