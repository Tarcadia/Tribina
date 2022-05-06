package net.tarcadia.tribina.gameplay.tribina;

import net.minestom.server.MinecraftServer;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.InstanceContainer;
import net.tarcadia.tribina.gameplay.lobby.LobbyGamePlay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TribinaGamePlay {

    // TODO: A LOT

    public static final InstanceContainer TRIBINA_WORLD = MinecraftServer.getInstanceManager().createInstanceContainer();
    public static final EventNode<InstanceEvent> TRIBINA_EVENT_NODE = EventNode.value("tribina", EventFilter.INSTANCE, instance -> instance == TRIBINA_WORLD);
    public static final Logger LOGGER = LoggerFactory.getLogger(LobbyGamePlay.class);

}
