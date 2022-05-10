package net.tarcadia.tribina.api.front.je758;

import net.tarcadia.tribina.network.TCPServer;
import net.tarcadia.util.function.impl.ThreadPoolRunHandler;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public final class JE758Server extends ThreadPoolRunHandler<Runnable> {

    public static final String SYS_CONNECTION_TIMEOUT = "tribina.front.connection-timeout";
    public static final String SYS_CONNECTION_SEND_BUFF_SIZE = "tribina.front.connection-send-buff-size";
    public static final String SYS_CONNECTION_RECV_BUFF_SIZE = "tribina.front.connection-recv-buff-size";
    public static final String SYS_CONNECTION_MAX_COUNT = "tribina.front.connection-max-count";
    public static final String SYS_CONNECTION_MAX_THREAD_COUNT = "tribina.front.connection-max-thread-count";
    public static final String SYS_PACKET_MAX_SIZE = "tribina.front.packet-max-size";

    public static final int CONNECTION_TIMEOUT = Integer.getInteger(SYS_CONNECTION_TIMEOUT, 1000);
    public static final int CONNECTION_SEND_BUFF_SIZE = Integer.getInteger(SYS_CONNECTION_SEND_BUFF_SIZE, 262144);
    public static final int CONNECTION_RECV_BUFF_SIZE = Integer.getInteger(SYS_CONNECTION_RECV_BUFF_SIZE, 32768);
    public static final int CONNECTION_MAX_COUNT = Integer.getInteger(SYS_CONNECTION_MAX_COUNT, 128);
    public static final int CONNECTION_MAX_THREAD_COUNT = Integer.getInteger(SYS_CONNECTION_MAX_THREAD_COUNT, 2 * CONNECTION_MAX_COUNT);
    public static final int PACKET_MAX_SIZE = Integer.getInteger(SYS_PACKET_MAX_SIZE, 2097151);

    private final TCPServer server;

    private boolean interrupted = false;

    public JE758Server(@NotNull String host, int port) throws IOException {
        super(CONNECTION_MAX_THREAD_COUNT);
        this.server = new TCPServer(host, port, CONNECTION_TIMEOUT, this, s -> () -> this.run(s));
        this.server.start();
    }

    public JE758Server(@NotNull ServerSocket socket) throws IOException {
        super(CONNECTION_MAX_THREAD_COUNT);
        this.server = new TCPServer(socket, CONNECTION_TIMEOUT, this, s -> () -> this.run(s));
        this.server.start();
    }

    @Override
    public void interrupt() {
        this.interrupted = true;
        this.server.interrupt();
        super.interrupt();
    }

    private void run(Socket socket) {
        try {
            socket.setTcpNoDelay(true);
            socket.setSoTimeout(CONNECTION_TIMEOUT);
            socket.setSendBufferSize(CONNECTION_SEND_BUFF_SIZE);
            socket.setReceiveBufferSize(CONNECTION_RECV_BUFF_SIZE);
        } catch (SocketException e) {
            //socket.close();
        }
        while (!this.interrupted) {
            // TODO: socket.getInputStream().readAllBytes(); and handle it
        }
    }

}
