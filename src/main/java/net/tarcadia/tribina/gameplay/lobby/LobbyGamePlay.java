package net.tarcadia.tribina.gameplay.lobby;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import net.minestom.server.MinecraftServer;
import net.minestom.server.command.CommandManager;
import net.minestom.server.coordinate.Pos;
import net.minestom.server.entity.GameMode;
import net.minestom.server.entity.Player;
import net.minestom.server.event.EventFilter;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.PlayerLoginEvent;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.event.trait.InstanceEvent;
import net.minestom.server.instance.SharedInstance;
import net.tarcadia.tribina.gameplay.GamePlay;
import net.tarcadia.tribina.gameplay.lobby.command.CommandStart;
import net.tarcadia.tribina.gameplay.lobby.instance.LobbyInstance;
import net.tarcadia.tribina.gameplay.tribina.TribinaGamePlay;
import net.tarcadia.tribina.util.configuration.Configuration;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

public class LobbyGamePlay implements GamePlay {

    private static final String PATH_CONFIG = "./tribina.lobby.properties";

    private static final String CFG_TITLE = "title";
    private static final String CFG_SUBTITLE = "subtitle";
    private static final String CFG_LOBBY_TIME = "lobby-time";
    private static final String CFG_HELLO_WORDS = "hello-words";
    private static final String CFG_HELLO_BROADCAST = "hello-broadcast";

    public static final SharedInstance LOBBY_INSTANCE = MinecraftServer.getInstanceManager().registerSharedInstance(new LobbyInstance(UUID.randomUUID(), TribinaGamePlay.TRIBINA_INSTANCE));
    public static final EventNode<InstanceEvent> LOBBY_EVENT_NODE = EventNode.value("lobby", EventFilter.INSTANCE, instance -> instance == LOBBY_INSTANCE);
    public static final Configuration CONFIG = new Configuration(new File(PATH_CONFIG));
    public static Component TITLE = Component.text(CONFIG.getString(CFG_TITLE, "TRIBINA"));
    public static Component SUBTITLE = Component.text(CONFIG.getString(CFG_SUBTITLE, "COMMAND START TO START..."));
    public static long LOBBY_TIME = CONFIG.getLong(CFG_LOBBY_TIME, 10000);
    public static Component HELLO_WORDS = Component.text(CONFIG.getString(CFG_HELLO_WORDS, "Hello."));
    public static Component HELLO_BROADCAST = Component.text(CONFIG.getString(CFG_HELLO_BROADCAST, "Hello."));
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

        LOBBY_INSTANCE.setTimeRate(5);
        LOBBY_INSTANCE.getWorldBorder().setCenter(0, 0);
        LOBBY_INSTANCE.getWorldBorder().setDiameter(1000);

        CommandManager manager = MinecraftServer.getCommandManager();
        manager.register(new CommandStart());
        GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();
        handler.addChild(LOBBY_EVENT_NODE);
    }

    private final Player player;
    private final long playerStartTime;

    public LobbyGamePlay(@NotNull PlayerLoginEvent event) {
        event.setSpawningInstance(LOBBY_INSTANCE);
        this.player = event.getPlayer();
        this.player.setRespawnPoint(new Pos(0, 42, 0));
        this.playerStartTime = System.currentTimeMillis();
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
        this.player.setGameMode(GameMode.SPECTATOR);
        this.player.setAutoViewable(false);
        this.player.showTitle(Title.title(TITLE, SUBTITLE, Title.Times.times(Duration.ofSeconds(1), Duration.ofMillis(LOBBY_TIME - 2000), Duration.ofSeconds(1))));
    }

    @Override
    public void tick() {
        if (System.currentTimeMillis() - this.playerStartTime > LOBBY_TIME) player.kick(Component.translatable("multiplayer.disconnect.idling"));
    }

    @Override
    public void end() {
        this.save();
        this.player.setAutoViewable(true);
    }
}
