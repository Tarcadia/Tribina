package net.tarcadia.tribina.network;

import net.tarcadia.util.function.Handler;
import net.tarcadia.util.function.Provider;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.*;

public class TCPServer extends Thread {

    public static final String SYS_CONNECTION_ACCEPT_TIMEOUT = "tribina.front.connection-accept-timeout";
    public static final String SYS_CONNECTION_RECV_TIMEOUT = "tribina.front.connection-recv-timeout";
    public static final String SYS_CONNECTION_SEND_BUFF_SIZE = "tribina.front.connection-send-buff-size";
    public static final String SYS_CONNECTION_RECV_BUFF_SIZE = "tribina.front.connection-recv-buff-size";
    public static final String SYS_CONNECTION_MAX_COUNT = "tribina.front.connection-max-count";
    public static final String SYS_PACKET_MAX_SIZE = "tribina.front.packet-max-size";

    public static final String SERVER_THREAD_NAME = "TCPServer.@%s";
    public static final int CONNECTION_ACCEPT_TIMEOUT = Integer.getInteger(SYS_CONNECTION_ACCEPT_TIMEOUT, 3 * 1000);
    public static final int CONNECTION_RECV_TIMEOUT = Integer.getInteger(SYS_CONNECTION_RECV_TIMEOUT, 1000);
    public static final int CONNECTION_SEND_BUFF_SIZE = Integer.getInteger(SYS_CONNECTION_SEND_BUFF_SIZE, 262144);
    public static final int CONNECTION_RECV_BUFF_SIZE = Integer.getInteger(SYS_CONNECTION_RECV_BUFF_SIZE, 32768);
    public static final int CONNECTION_MAX_COUNT = Integer.getInteger(SYS_CONNECTION_MAX_COUNT, 128);
    public static final int PACKET_MAX_SIZE = Integer.getInteger(SYS_PACKET_MAX_SIZE, 2097151);

    private final ServerSocket socket;
    private final Handler<Runnable> handler;
    private final Provider<Socket, Runnable> provider;

    private volatile boolean interrupted = false;

    public TCPServer(@NotNull String host, int port, @NotNull Handler<Runnable> handler, @NotNull Provider<Socket, Runnable> provider) throws IOException {
        super(SERVER_THREAD_NAME.formatted(host + ":" + port));
        this.socket = new ServerSocket(port, 50, InetAddress.getByName(host));
        this.socket.setSoTimeout(CONNECTION_ACCEPT_TIMEOUT);
        this.handler = handler;
        this.provider = provider;
    }

    public TCPServer(@NotNull ServerSocket socket, @NotNull Handler<Runnable> handler, @NotNull Provider<Socket, Runnable> provider) throws IOException {
        super(SERVER_THREAD_NAME.formatted(socket.getInetAddress().toString()));
        this.socket = socket;
        this.socket.setSoTimeout(CONNECTION_ACCEPT_TIMEOUT);
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
                accepted.setTcpNoDelay(true);
                accepted.setSoTimeout(CONNECTION_RECV_TIMEOUT);
                accepted.setSendBufferSize(CONNECTION_SEND_BUFF_SIZE);
                accepted.setReceiveBufferSize(CONNECTION_RECV_BUFF_SIZE);
                this.handler.handle(this.provider.provide(accepted));
            } catch (SocketTimeoutException ignored) {
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
