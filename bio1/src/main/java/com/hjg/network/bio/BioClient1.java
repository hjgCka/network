package com.hjg.network.bio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/1
 */
public class BioClient1 {

    private static final Logger logger = LoggerFactory.getLogger(BioClient1.class);

    public static void main(String[] args) throws IOException {

        Socket socket = new Socket("127.0.0.1", 88);

        PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

        Runnable sender = () -> {
            try {
                String line = null;
                Scanner scanner = new Scanner(System.in);
                while(!"done".equals(line=scanner.nextLine())) {
                    //发送到服务端
                    System.out.println("send to server");
                    printWriter.println(line);
                }
                System.out.println("client exit");

                socket.close();
            } catch (Exception e) {
                logger.error("客户端发送线程遇到异常", e);
            }
        };

        Runnable receiver = () -> {
            try {
                String line = null;
                while(!"processed".equals(line=br.readLine())) {
                    System.out.println("receive from server : " + line);
                }
                System.out.println("client exit");

                socket.close();
            } catch (Exception e) {
                logger.error("客户端接收线程遇到异常", e);
            }
        };

        new Thread(receiver).start();
        new Thread(sender).start();
    }
}
