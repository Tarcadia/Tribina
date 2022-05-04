package net.tarcadia.tribina;

import net.kyori.adventure.text.Component;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.PlayerSkin;
import net.minestom.server.event.GlobalEventHandler;
import net.minestom.server.event.player.AsyncPlayerPreLoginEvent;
import net.minestom.server.event.server.ServerListPingEvent;
import net.minestom.server.network.ConnectionManager;
import net.minestom.server.network.packet.server.login.LoginDisconnectPacket;
import net.minestom.server.ping.ResponseData;
import net.tarcadia.tribina.util.Favicon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.UUID;

public class ServerInfo {

    private static String FAVICON = Favicon.fromFile(new File("./favicon.png"));
    private static Component MOTD = Component.text("Tribina comes here.");
    private static int MAX_PLAYER_COUNT = Integer.getInteger("tribina.max-player-count", 100);
    private static Component MAX_PLAYER_MESSAGE = Component.translatable("multiplayer.disconnect.server_full");

    @Nullable
    public static String getFavicon() {
        return FAVICON;
    }

    public static boolean setFaviconFromFile(@NotNull String filename) {
        String favicon = null;
        try {
            File file = new File(filename);
            favicon = Favicon.fromFile(file);
        } catch (Throwable ignored) {}
        if (favicon != null) {
            FAVICON = favicon;
            return true;
        } else {
            return false;
        }
    }

    public static boolean setFaviconFromResource(@NotNull String filename) {
        String favicon = null;
        try {
            favicon = Favicon.fromResource(filename);
        } catch (Throwable ignored) {}
        if (favicon != null) {
            FAVICON = favicon;
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public static Component getMOTD() {
        return MOTD;
    }

    public static boolean setMOTDPlainText(@NotNull String text) {
        Component motd = null;
        try {
            motd = Component.text(text);
        } catch (Throwable ignored) {}
        if (motd != null) {
            MOTD = motd;
            return true;
        } else {
            return false;
        }
    }

    public static int getMaxPlayerCount() {
        return MAX_PLAYER_COUNT;
    }

    public static boolean setMaxPlayerCount(int maxPlayerCount) {
        if (maxPlayerCount >= 1) {
            MAX_PLAYER_COUNT = maxPlayerCount;
            return true;
        } else {
            return false;
        }
    }

    @Nullable
    public static Component getMaxPlayerMessage() {
        return MAX_PLAYER_MESSAGE;
    }

    public static boolean setMaxPlayerMessageTranslatable(@NotNull String translatable) {
        Component message = null;
        try {
            message = Component.translatable(translatable);
        } catch (Throwable ignored) {}
        if (message != null) {
            MAX_PLAYER_MESSAGE = message;
            return true;
        } else {
            return false;
        }
    }

    public static boolean setMaxPlayerMessageText(@NotNull String text) {
        Component message = null;
        try {
            message = Component.text(text);
        } catch (Throwable ignored) {}
        if (message != null) {
            MAX_PLAYER_MESSAGE = message;
            return true;
        } else {
            return false;
        }
    }

    public static void initServerInfo() {
        ConnectionManager manager = MinecraftServer.getConnectionManager();
        manager.setUuidProvider((playerConnection, username) -> UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8)));
        manager.setPlayerProvider(((uuid, username, connection) -> {
            Player player = new Player(uuid, username, connection);
            player.setSkin(PlayerSkin.fromUsername(username));
            return player;
        }));

        GlobalEventHandler handler = MinecraftServer.getGlobalEventHandler();
        handler.addListener(AsyncPlayerPreLoginEvent.class, event -> {
            if (MinecraftServer.getConnectionManager().getOnlinePlayers().size() >= MAX_PLAYER_COUNT) {
                event.getPlayer().getPlayerConnection().sendPacket(new LoginDisconnectPacket(MAX_PLAYER_MESSAGE));
                event.getPlayer().getPlayerConnection().disconnect();
            }
        });
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
