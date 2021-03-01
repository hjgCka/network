package com.hjg.network.bio;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/1
 */
public class BioServer1 {

    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = new ServerSocket(88);

        while(!serverSocket.isClosed()) {
            Socket socket = serverSocket.accept();
            new Thread(new Worker(socket)).start();
        }

    }
}
