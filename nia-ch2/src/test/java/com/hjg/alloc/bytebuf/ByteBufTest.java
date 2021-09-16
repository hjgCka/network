package com.hjg.alloc.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.util.ByteProcessor;
import org.junit.Test;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/15
 */
public class ByteBufTest {

    @Test
    public void byteBufUtil() {

        byte[] bytes = {15, 10};
        System.out.println(ByteBufUtil.hexDump(bytes));
    }

    @Test
    public void readWriteByteBuf() {
        byte[] bytes = {15, 10};

        ByteBuf heapByteBuf = Unpooled.buffer(10);
        heapByteBuf.writeBytes(bytes);

        System.out.println(ByteBufUtil.hexDump(heapByteBuf));

        int writeIndex = heapByteBuf.writerIndex();
        System.out.println("writeIndex = " + writeIndex);

        //随机访问索引
        int length = heapByteBuf.readableBytes();
        for(int i=0; i<length; i++) {
            byte b = heapByteBuf.getByte(i);
            System.out.println(b);
        }
    }

    @Test
    public void searchByte() {
        byte[] bytes = {15, 10};

        ByteBuf heapByteBuf = Unpooled.buffer(10);
        heapByteBuf.writeBytes(bytes);

        int index1 = heapByteBuf.indexOf(0, heapByteBuf.readableBytes(), (byte) 10);
        System.out.println(index1);

        int index2 = heapByteBuf.forEachByte(ByteProcessor.FIND_CR);
        System.out.println(index2);
    }
}
