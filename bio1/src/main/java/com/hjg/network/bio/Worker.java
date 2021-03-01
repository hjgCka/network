package com.hjg.network.bio;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/1
 */
@AllArgsConstructor
public class Worker implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Worker.class);

    private Socket socket;

    @Override
    public void run() {
        try {
            PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String line = null;
            while(!"exit".equals(line=br.readLine())) {
                printWriter.println("processing");

                String hostAddress = socket.getInetAddress().getHostAddress();
                int port = socket.getPort();
                System.out.println("收到客户端消息, hostAddress=" + hostAddress + ", port = " + port + ", message = " + line);
            }

            System.out.println("客户端连接断开");
            printWriter.println("processed");
            socket.close();
        } catch (Exception e) {
            logger.error("处理请求时发生了异常", e);
        }
    }
}
