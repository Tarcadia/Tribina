package net.tarcadia.tribina.main;

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
import net.tarcadia.tribina.util.configuration.Configuration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.CopyOnWriteArraySet;

public class ServerInfo {

    private static final String PATH_CONFIG = "./tribina.properties";
    private static final String PATH_FAVICON = "./favicon.png";

    private static final String CFG_FAVICON = "favicon";
    private static final String CFG_MOTD = "motd";
    private static final String CFG_MAX_PLAYER_COUNT = "max-player-count";
    private static final String CFG_BAN_PLAYER_LIST = "ban-player-list";
    private static final String CFG_BAN_IP_LIST = "ban-ip-list";

    private static final Configuration CONFIG = new Configuration(new File(PATH_CONFIG));
    private static String FAVICON = FaviconUtil.fromFile(new File(CONFIG.getString(CFG_FAVICON, PATH_FAVICON)));
    private static Component MOTD = Component.text(CONFIG.getString(CFG_MOTD, "Tribina comes here."));
    private static int MAX_PLAYER_COUNT = CONFIG.getInteger(CFG_MAX_PLAYER_COUNT, 100);
    private static final Set<String> BAN_PLAYER_LIST = new CopyOnWriteArraySet<>(CONFIG.getList(CFG_BAN_PLAYER_LIST));
    private static final Set<String> BAN_IP_LIST = new CopyOnWriteArraySet<>(CONFIG.getList(CFG_BAN_IP_LIST));

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
            CONFIG.modify(CFG_FAVICON, filename);
            FAVICON = favicon;
            try {
                CONFIG.save();
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
        CONFIG.modify(CFG_MOTD, text);
        try {
            CONFIG.save();
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
            CONFIG.modify(CFG_MAX_PLAYER_COUNT, Integer.toString(MAX_PLAYER_COUNT));
            try {
                CONFIG.save();
                return true;
            } catch (Throwable e) {
                return false;
            }
        } else {
            return false;
        }
    }

    public static List<String> getPlayingList() {
        List<String> list = new LinkedList<>();
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            list.add(player.getUsername());
        }
        return list;
    }

    public static List<String> getBanList() {
        List<String> list = new LinkedList<>();
        list.addAll(BAN_IP_LIST);
        list.addAll(BAN_PLAYER_LIST);
        return list;
    }

    public static boolean banPlayer(@NotNull String username) {
        BAN_PLAYER_LIST.add(username);
        CONFIG.modifyAdd(CFG_BAN_PLAYER_LIST, username);
        Player player = MinecraftServer.getConnectionManager().getPlayer(username);
        if (player != null) player.kick(Component.translatable("multiplayer.disconnect.banned"));
        try {
            CONFIG.save();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean banIP(@NotNull String ip) {
        BAN_IP_LIST.add(ip);
        CONFIG.modifyAdd(CFG_BAN_IP_LIST, ip);
        for (Player player : MinecraftServer.getConnectionManager().getOnlinePlayers()) {
            if (isBanned(player)) player.kick(Component.translatable("multiplayer.disconnect.banned"));
        }
        try {
            CONFIG.save();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean unbanPlayer(@NotNull String username) {
        BAN_PLAYER_LIST.remove(username);
        CONFIG.modifyRemove(CFG_BAN_PLAYER_LIST, username);
        try {
            CONFIG.save();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean unbanIP(@NotNull String ip) {
        BAN_IP_LIST.remove(ip);
        CONFIG.modifyRemove(CFG_BAN_IP_LIST, ip);
        try {
            CONFIG.save();
            return true;
        } catch (Throwable e) {
            return false;
        }
    }

    public static boolean isBanned(@NotNull Player player) {
        return (
                BAN_PLAYER_LIST.contains(player.getUsername()) ||
                (
                        player.getPlayerConnection().getRemoteAddress() instanceof InetSocketAddress &&
                        BAN_IP_LIST.contains(((InetSocketAddress)player.getPlayerConnection().getRemoteAddress()).getHostName())
                )
        );
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
            if (isBanned(event.getPlayer())) {
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
