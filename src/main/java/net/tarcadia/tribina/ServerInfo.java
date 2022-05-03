package net.tarcadia.tribina;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.packet.server.play.DisconnectPacket;
import net.minestom.server.ping.ResponseData;
import net.tarcadia.tribina.util.Favicon;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

public class ServerInfo {

    public static String FAVICON = Favicon.fromFile(new File("./favicon.png"), Favicon.Type.PNG);
    public static Component MOTD = Component.text("");
    public static int MAX_PLAYER_COUNT = Integer.getInteger("tribina.max-player-count", 100);
    public static Component MAX_PLAYER_MESSAGE = Component.text("Full Server");

    public static void initServerInfo() {
        ConnectionManager manager = MinecraftServer.getConnectionManager();
        manager.setUuidProvider((playerConnection, username) -> {
            if (MinecraftServer.getConnectionManager().getOnlinePlayers().size() >= MAX_PLAYER_COUNT) {
                playerConnection.sendPacket(new DisconnectPacket(MAX_PLAYER_MESSAGE));
                playerConnection.disconnect();
                return null;
            } else {
                return UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
            }
        });
        manager.setPlayerProvider(((uuid, username, connection) -> {
            Player player = new Player(uuid, username, connection);
            player.setSkin(PlayerSkin.fromUsername(username));
            return player;
        }));

        GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();
        handler.addListener(ServerListPingEvent.class, event -> {
            final ResponseData responseData = event.getResponseData();
            if (FAVICON != null) responseData.setFavicon(FAVICON);
            if (MOTD != null) responseData.setDescription(MOTD);
            Collection<Player> onlinePlayers = MinecraftServer.getConnectionManager().getOnlinePlayers();
            responseData.setMaxPlayer(Math.min(onlinePlayers.size() + 1, MAX_PLAYER_COUNT));
            responseData.addEntries(onlinePlayers);
        });
    }

}
