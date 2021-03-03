package com.hjg.network.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/1
 */
public class NioServer {

    private static final Logger logger = LoggerFactory.getLogger(NioServer.class);

    private int port;
    private BlockingQueue<Socket> queue = new ArrayBlockingQueue<>(1024);

    public NioServer(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));

        Runnable acceptor = () -> {
            long socketId = 0;

            while(true) {
                SocketChannel socketChannel = null;
                try {
                    socketChannel = serverSocketChannel.accept();

                    queue.put(new Socket(socketChannel, ++socketId));
                    logger.info("接收客户端链接, socketId={}", socketId);
                } catch (IOException e) {
                    logger.error("接收客户端socket失败", e);
                } catch (InterruptedException e) {
                    logger.error("放入阻塞队列失败", e);
                }
            }
        };

        new Thread(acceptor).start();

        new Thread(new Processor(queue)).start();
    }

}
