package com.hjg.network.nio;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

/**
 * nio的事件不是主动触发，而是需要不停去查询
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/2
 */
public class Processor implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    private BlockingQueue<Socket> queue;

    private Selector readSelector;
    private Selector writeSelector;

    public Processor(BlockingQueue<Socket> queue) throws IOException {
        this.queue = queue;
        readSelector = Selector.open();
        writeSelector = Selector.open();
    }

    @Override
    public void run() {
        while(true) {
            try {
                register();

                //这个方法不能阻塞，因为读写操作在同一个线程
                int readReadyCount = this.readSelector.selectNow();
                if(readReadyCount > 0) {
                    readData();
                }

                //这个方法不能阻塞，因为读写操作在同一个线程
                int writeReadyCount = this.writeSelector.selectNow();
                if(writeReadyCount > 0) {
                    writeData();
                }
            } catch (InterruptedException | IOException e) {
                logger.error("处理SocketChannel失败", e);
            }
        }
    }

    private void register() throws InterruptedException, IOException {
        //由于注册和读写放在同一个线程，为了不阻塞读写，不能用take，因为拿不到对象它会一直阻塞
        Socket socket = queue.poll();

        if(socket != null) {
            SocketChannel socketChannel = socket.getSocketChannel();
            socketChannel.configureBlocking(false);

            SelectionKey readKey = socketChannel.register(readSelector, SelectionKey.OP_READ);
            readKey.attach(socket);

            SelectionKey writeKey = socketChannel.register(writeSelector, SelectionKey.OP_WRITE);
            writeKey.attach(socket);
        }
    }

    private void readData() throws IOException {
        Set<SelectionKey> set = this.readSelector.selectedKeys();
        Iterator<SelectionKey> it = set.iterator();
        while(it.hasNext()) {
            SelectionKey selectionKey = it.next();

            if(selectionKey.isReadable()) {
                Socket socket = (Socket) selectionKey.attachment();


                SocketChannel socketChannel = socket.getSocketChannel();

                //这里传来的数据，可能不完整，有可能只有部分，也有可能超过一个完整的消息
                try {
                    //将数据从channel读取到buffer
                    ByteBuffer readByteBuffer = socket.getReadByteBuffer();
                    int readByte = socketChannel.read(readByteBuffer);
                    if(readByte == 0) {
                        continue;
                    }
                    logger.info("socketId={}, 读取到{}字节", socket.getSocketId(), readByte);

                    readByteBuffer.flip();
                    //readByteBuffer.limit(readByte);

                    byte[] bytes = new byte[readByte];
                    readByteBuffer.get(bytes, 0, readByte);
                    socket.getOutBoundMessageQueue().offer(bytes);
                    readByteBuffer.clear();
                } catch (IOException e) {
                    logger.error("socketId={}, 读取字节时异常", socket.getSocketId(), e);

                    selectionKey.cancel();
                    socketChannel.close();
                }
            }

            it.remove();
        }

        set.clear();
    }

    private void writeData() throws IOException {
        Set<SelectionKey> set = this.writeSelector.selectedKeys();
        Iterator<SelectionKey> it = set.iterator();
        while(it.hasNext()) {
            SelectionKey selectionKey = it.next();

            if(selectionKey.isWritable()) {
                Socket socket = (Socket) selectionKey.attachment();
                Queue<byte[]> queue = socket.getOutBoundMessageQueue();
                if(queue.size() == 0) {
                    continue;
                }

                byte[] prefix = "Hello, ".getBytes(StandardCharsets.UTF_8);

                ByteBuffer writeByteBuffer = socket.getWriteByteBuffer();
                writeByteBuffer.put(prefix);
                byte[] bytes = queue.poll();
                writeByteBuffer.put(bytes);

                SocketChannel socketChannel = socket.getSocketChannel();
                try {
                    writeByteBuffer.flip();
                    //writeByteBuffer.limit(prefix.length + bytes.length);
                    int writeByte = socketChannel.write(writeByteBuffer);
                    writeByteBuffer.clear();
                    logger.info("socketId={}, 发送字节数{}", socket.getSocketId(), writeByte);
                } catch (IOException e) {
                    logger.error("socketId={}, 发送字节时报错", socket.getSocketId(), e);
                }
            }

            it.remove();
        }

        set.clear();
    }
}
