package com.hjg.network.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/3
 */
public class Client {

    private static final Logger logger = LoggerFactory.getLogger(Client.class);

    public static void main(String[] args) throws IOException {
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 99));

        ByteBuffer writeBuffer =  ByteBuffer.allocate(1024);
        //开启一个线程监控System.in进行输入
        new Thread(() -> {
            while(true) {
                BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
                try {
                    String line = br.readLine();
                    if(line.equalsIgnoreCase("exit")) {
                        socketChannel.close();
                        logger.info("断开链接");
                        break;
                    }

                    byte[] bytes = line.getBytes(StandardCharsets.UTF_8);
                    writeBuffer.put(bytes);

                    writeBuffer.flip();
                    //writeBuffer.limit(bytes.length);
                    int count = socketChannel.write(writeBuffer);
                    logger.info("发送{}字节", count);
                    writeBuffer.clear();
                } catch (IOException e) {
                    logger.error("发送数据时遭遇IO异常", e);
                }
            }
        }).start();

        ByteBuffer readBuffer =  ByteBuffer.allocate(1048);
        //开启一个线程接收服务端返回的信息
        new Thread(() -> {
            while(true) {
                try {
                    if(!socketChannel.isConnected()) {
                        break;
                    }

                    int count = socketChannel.read(readBuffer);
                    logger.info("客户端读取到了{}字节", count);

                    readBuffer.flip();

                    byte[] bytes = new byte[count];
                    readBuffer.get(bytes, 0, count);

                    String result = new String(bytes, StandardCharsets.UTF_8);
                    logger.info("接收result={}", result);
                    readBuffer.clear();
                } catch (IOException e) {
                    logger.error("接收数据时遭遇IO异常", e);
                }
            }
        }).start();
    }
}
