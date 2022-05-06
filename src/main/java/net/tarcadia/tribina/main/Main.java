package net.tarcadia.tribina.main;

import net.minestom.server.MinecraftServer;
import net.minestom.server.extras.MojangAuth;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static net.tarcadia.tribina.main.ServerGamePlay.initServerGamePlay;
import static net.tarcadia.tribina.main.ServerInfo.initServerInfo;
import static net.tarcadia.tribina.main.ServerCommand.initServerCommand;

public class Main {

    public static final int COMPRESSION_THRESHOLD = Integer.getInteger("minestom.compression-threshold", 256);
    public static final String BRAND_NAME = "Tribina";
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        // Init the server
        MinecraftServer server = MinecraftServer.init();
        MinecraftServer.setCompressionThreshold(COMPRESSION_THRESHOLD);
        MinecraftServer.setBrandName(BRAND_NAME);
        MojangAuth.init();

        initServerInfo();
        initServerCommand();
        initServerGamePlay();

        // Start the server on port 25565
        server.start("127.0.0.1", 25565);
    }

}
