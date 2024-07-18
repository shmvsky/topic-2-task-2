package ru.shmvsky;

import ru.shmvsky.exception.ClientException;
import ru.shmvsky.exception.ServerException;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TcpEchoServer {

    private final static Logger logger = Logger.getLogger(TcpEchoServer.class.getName());

    private final int port;

    public TcpEchoServer(int port) {
        this.port = port;
    }

    public void run() throws ServerException {

        try (ServerSocketChannel serverSocket = ServerSocketChannel.open()) {
            serverSocket.bind(new InetSocketAddress(port));
            serverSocket.configureBlocking(false);
            try (Selector selector = Selector.open()) {
                serverSocket.register(selector, SelectionKey.OP_ACCEPT);
                logger.log(Level.INFO, "Server running on a port {0}", port);
                while (true) {
                    selector.select();
                    Set<SelectionKey> selectedKeys = selector.selectedKeys();
                    Iterator<SelectionKey> iter = selectedKeys.iterator();
                    while (iter.hasNext()) {

                        SelectionKey key = iter.next();

                        if (key.isAcceptable()) {
                            acceptConnection(selector, serverSocket);
                        }

                        if (key.isReadable()) {
                            writeResponse(key);
                        }
                        iter.remove();
                    }
                }
            }
        } catch (Exception e) {
            throw new ServerException("Something went wrong. Port may be already in use", e);
        }
    }

    private static void acceptConnection(Selector selector, ServerSocketChannel serverSocket) {
        try {
            SocketChannel client = serverSocket.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
            logger.log(Level.INFO, "New connection accepted: {0}", client);
        } catch (IOException e) {
            throw new ClientException("Error accepting a connection", e);
        }
    }

    private static void writeResponse(SelectionKey key) {
        ByteBuffer buffer = ByteBuffer.allocate(256);
        SocketChannel client = (SocketChannel) key.channel();
        try {
            int r = client.read(buffer);
            if (r == -1 || new String(buffer.array()).trim().equals("EXIT")) {
                client.close();
            }
            else {
                buffer.flip();
                client.write(buffer);
                logger.log(Level.INFO, "New message from {0}: {1}", new Object[] {client, new String(buffer.array()).trim()});
                buffer.clear();
            }
        } catch (IOException e) {
            throw new ClientException("Error writing a response to client: " + client, e);
        }
    }


}
