package net.tarcadia.tribina.gameplay.lobby;

import net.minestom.server.MinecraftServer;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.SharedInstance;
import net.tarcadia.tribina.gameplay.GamePlay;
import net.tarcadia.tribina.gameplay.lobby.instance.LobbyInstance;
import net.tarcadia.tribina.gameplay.tribina.TribinaGamePlay;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class LobbyGamePlay implements GamePlay {

    public static final SharedInstance LOBBY_INSTANCE = MinecraftServer.getInstanceManager().registerSharedInstance(new LobbyInstance(UUID.randomUUID(), TribinaGamePlay.TRIBINA_INSTANCE));
    public static final EventNode<InstanceEvent> LOBBY_EVENT_NODE = EventNode.value("lobby", EventFilter.INSTANCE, instance -> instance == LOBBY_INSTANCE);
    public static final Logger LOGGER = LoggerFactory.getLogger(LobbyGamePlay.class);

    @NotNull
    public static SharedInstance getLobbyInstance() {
        return LOBBY_INSTANCE;
    }

    @NotNull
    public static EventNode<InstanceEvent> getLobbyEventNode() {
        return LOBBY_EVENT_NODE;
    }

    public static synchronized void init() {

        LOBBY_INSTANCE.setTimeRate(20);
        LOBBY_INSTANCE.getWorldBorder().setCenter(0, 0);
        LOBBY_INSTANCE.getWorldBorder().setDiameter(1000);

        GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();
        handler.addChild(LOBBY_EVENT_NODE);
    }

    public final Player player;

    public LobbyGamePlay(@NotNull PlayerLoginEvent event) {
        event.setSpawningInstance(LOBBY_INSTANCE);
        this.player = event.getPlayer();
        this.player.setRespawnPoint(new Pos(0, 42, 0));
        this.load();
    }

    @Override
    public void load() {

    }

    @Override
    public void save() {

    }

    @Override
    public void start() {
        player.setGameMode(GameMode.SPECTATOR);
        player.setAutoViewable(false);
    }

    @Override
    public void end() {
        this.save();
        player.setAutoViewable(true);
    }
}
