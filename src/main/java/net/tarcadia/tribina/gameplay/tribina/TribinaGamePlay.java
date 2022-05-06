package net.tarcadia.tribina.gameplay.tribina;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.InstanceContainer;
import net.minestom.server.instance.block.Block;
import net.tarcadia.tribina.gameplay.lobby.LobbyGamePlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TribinaGamePlay {

    // TODO: A LOT

    public static final InstanceContainer TRIBINA_INSTANCE = MinecraftServer.getInstanceManager().createInstanceContainer();
    public static final EventNode<InstanceEvent> TRIBINA_EVENT_NODE = EventNode.value("tribina", EventFilter.INSTANCE, instance -> instance == TRIBINA_INSTANCE);
    public static final Logger LOGGER = LoggerFactory.getLogger(LobbyGamePlay.class);

    public static void init() {
        TRIBINA_INSTANCE.setGenerator(unit -> unit.modifier().fillHeight(0, 40, Block.GRASS_BLOCK));
        TRIBINA_INSTANCE.getWorldBorder().setCenter(0, 0);
        TRIBINA_INSTANCE.getWorldBorder().setDiameter(16000);

        GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();
        handler.addChild(TRIBINA_EVENT_NODE);
    }

}
