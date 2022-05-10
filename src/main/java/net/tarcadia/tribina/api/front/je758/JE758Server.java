package net.tarcadia.tribina.api.front.je758;

import net.tarcadia.tribina.api.front.je758.connection.ConnectionManager;
import net.tarcadia.tribina.network.TCPServer;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;

public final class JE758Server {

    private final TCPServer server;
    private final ConnectionManager manager;

    public JE758Server(@NotNull String host, int port) throws IOException {
        this.manager = new ConnectionManager();
        this.server = new TCPServer(host, port, manager, manager.connections);
    }

    public JE758Server(@NotNull ServerSocket socket) throws IOException {
        this.manager = new ConnectionManager();
        this.server = new TCPServer(socket, manager, manager.connections);
    }

}
