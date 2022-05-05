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
import net.tarcadia.tribina.util.FaviconUtil;
import net.tarcadia.tribina.util.ConfigUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

public class ServerInfo {

    private static final String PATH_CONFIG = "./tribina.properties";
    private static final String PATH_FAVICON = "./favicon.png";
    private static final String PATH_BAN_PLAYER_LIST = "./ban-player.txt";
    private static final String PATH_BAN_IP_LIST = "./ban-ip.txt";

    private static final String CFG_FAVICON = "favicon";
    private static final String CFG_MOTD = "motd";
    private static final String CFG_MAX_PLAYER_COUNT = "max-player-count";

    private static final Map<String, String> CONFIG = new ConcurrentHashMap<>(ConfigUtil.fromFileMap(new File(PATH_CONFIG)));
    private static String FAVICON = FaviconUtil.fromFile(new File(CONFIG.getOrDefault(CFG_FAVICON, PATH_FAVICON)));
    private static Component MOTD = Component.text(CONFIG.getOrDefault(CFG_MOTD, "Tribina comes here."));
    private static int MAX_PLAYER_COUNT = Integer.parseInt(CONFIG.getOrDefault(CFG_MAX_PLAYER_COUNT, "100"));
    private static final Set<String> BAN_PLAYER_LIST = new CopyOnWriteArraySet<>(ConfigUtil.fromFileList(new File(PATH_BAN_PLAYER_LIST)));
    private static final Set<String> BAN_IP_LIST = new CopyOnWriteArraySet<>(ConfigUtil.fromFileList(new File(PATH_BAN_IP_LIST)));

    @Nullable
    public static String getFavicon() {
        return FAVICON;
    }

    public static boolean setFavicon(@NotNull String filename) {
        String favicon = null;
        try {
            File file = new File(filename);
            favicon = FaviconUtil.fromFile(file);
        } catch (Throwable ignored) {}
        if (favicon != null) {
            CONFIG.put(CFG_FAVICON, filename);
            FAVICON = favicon;
            try {
                ConfigUtil.toFileMap(new File(PATH_CONFIG), CONFIG);
                return true;
            } catch (Throwable e) {
                return false;
            }
        } else {
            return false;
        }
    }

    @Nullable
    public static Component getMOTD() {
        return MOTD;
    }

    public static boolean setMOTD(@NotNull String text) {
        Component motd = null;
        motd = Component.text(text);
        MOTD = motd;
        CONFIG.put(CFG_MOTD, text);
        try {
            ConfigUtil.toFileMap(new File(PATH_CONFIG), CONFIG);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static int getMaxPlayerCount() {
        return MAX_PLAYER_COUNT;
    }

    public static boolean setMaxPlayerCount(int maxPlayerCount) {
        if (maxPlayerCount >= 1) {
            MAX_PLAYER_COUNT = maxPlayerCount;
            CONFIG.put(CFG_MAX_PLAYER_COUNT, String.valueOf(MAX_PLAYER_COUNT));
            try {
                ConfigUtil.toFileMap(new File(PATH_CONFIG), CONFIG);
                return true;
            } catch (Throwable e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static boolean banPlayer(@NotNull String username) {
        BAN_PLAYER_LIST.add(username);
        try {
            ConfigUtil.toFileList(new File(PATH_BAN_PLAYER_LIST), BAN_PLAYER_LIST);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean banIP(@NotNull String ip) {
        BAN_IP_LIST.add(ip);
        try {
            ConfigUtil.toFileList(new File(PATH_BAN_IP_LIST), BAN_IP_LIST);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean unbanPlayer(@NotNull String username) {
        BAN_PLAYER_LIST.remove(username);
        try {
            ConfigUtil.toFileList(new File(PATH_BAN_PLAYER_LIST), BAN_PLAYER_LIST);
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean unbanIP(@NotNull String ip) {
        BAN_IP_LIST.remove(ip);
        try {
            ConfigUtil.toFileList(new File(PATH_BAN_IP_LIST), BAN_IP_LIST);
            return true;
        } catch (Throwable e) {
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
            if (BAN_PLAYER_LIST.contains(event.getUsername()) || BAN_IP_LIST.contains(event.getPlayer().getPlayerConnection().getRemoteAddress().toString().split(":")[0])) {
                event.getPlayer().getPlayerConnection().sendPacket(new LoginDisconnectPacket(Component.translatable("multiplayer.disconnect.banned")));
                event.getPlayer().getPlayerConnection().disconnect();
            }
            if (MinecraftServer.getConnectionManager().getOnlinePlayers().size() >= MAX_PLAYER_COUNT) {
                event.getPlayer().getPlayerConnection().sendPacket(new LoginDisconnectPacket(Component.translatable("multiplayer.disconnect.server_full")));
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
