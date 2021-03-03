package com.hjg.network.nio;

import lombok.Data;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.Queue;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/2
 */
@Data
public class Socket {

    private long socketId;
    private SocketChannel socketChannel;

    private Queue<byte[]> outBoundMessageQueue;
    private ByteBuffer writeByteBuffer;
    private ByteBuffer readByteBuffer;

    public Socket(SocketChannel socketChannel, long socketId) {
        this.socketId = socketId;
        this.socketChannel = socketChannel;

        this.readByteBuffer = ByteBuffer.allocate(1024);
        this.writeByteBuffer = ByteBuffer.allocate(1048);
        this.outBoundMessageQueue = new LinkedList<>();
    }
}
