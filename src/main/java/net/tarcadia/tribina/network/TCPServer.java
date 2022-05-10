package net.tarcadia.tribina.network;

import net.tarcadia.util.function.Handler;
import net.tarcadia.util.function.Provider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;

public class TCPServer extends Thread {

    public static final String SERVER_THREAD_NAME = "TCPServer.@%s";

    private final int timeout;
    private final ServerSocket socket;
    private final Handler<Runnable> handler;
    private final Provider<Socket, Runnable> provider;

    private volatile boolean interrupted = false;

    public TCPServer(@NotNull String host, int port, int timeout, @NotNull Handler<Runnable> handler, @NotNull Provider<Socket, Runnable> provider) throws IOException {
        super(SERVER_THREAD_NAME.formatted(host + ":" + port));
        this.timeout = timeout;
        this.socket = new ServerSocket(port, 50, InetAddress.getByName(host));
        this.socket.setSoTimeout(timeout);
        this.handler = handler;
        this.provider = provider;
    }

    public TCPServer(@NotNull ServerSocket socket, int timeout, @NotNull Handler<Runnable> handler, @NotNull Provider<Socket, Runnable> provider) throws IOException {
        super(SERVER_THREAD_NAME.formatted(socket.getInetAddress().toString()));
        this.timeout = timeout;
        this.socket = socket;
        this.socket.setSoTimeout(timeout);
        this.handler = handler;
        this.provider = provider;
    }

    public TCPServer(@NotNull String host, int port, @NotNull Handler<Runnable> handler, @NotNull Provider<Socket, Runnable> provider) throws IOException {
        super(SERVER_THREAD_NAME.formatted(host + ":" + port));
        this.timeout = 3000;
        this.socket = new ServerSocket(port, 50, InetAddress.getByName(host));
        this.socket.setSoTimeout(timeout);
        this.handler = handler;
        this.provider = provider;
    }

    public TCPServer(@NotNull ServerSocket socket, @NotNull Handler<Runnable> handler, @NotNull Provider<Socket, Runnable> provider) throws IOException {
        super(SERVER_THREAD_NAME.formatted(socket.getInetAddress().toString()));
        this.timeout = 3000;
        this.socket = socket;
        this.socket.setSoTimeout(timeout);
        this.handler = handler;
        this.provider = provider;
    }

    @Override
    public void interrupt() {
        this.interrupted = true;
        super.interrupt();
    }

    @Override
    public void run() {
        while (!this.interrupted) {
            try {
                Socket accepted = this.socket.accept();
                this.handler.handle(this.provider.provide(accepted));
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
