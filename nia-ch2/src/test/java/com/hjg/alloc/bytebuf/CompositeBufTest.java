package com.hjg.alloc.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/15
 */
public class CompositeBufTest {

    @Test
    public void compositeTest() {
        ByteBuf heapByteBuf = Unpooled.buffer(10);

        ByteBuf directBuffer = Unpooled.directBuffer(16);

        CompositeByteBuf compositeBuffer = Unpooled.compositeBuffer();
        //不增加write索引
        compositeBuffer.addComponents(heapByteBuf, directBuffer);
        System.out.println(compositeBuffer.readableBytes());

        //写入数据
        byte[] bytes = {15, 10};
        compositeBuffer.writeBytes(bytes);
        System.out.println(compositeBuffer.readableBytes());

        //说明这种CompositeByteBuf并未修改原来的ByteBuf，它是全新的一个对象
        System.out.println(ByteBufUtil.hexDump(heapByteBuf));
        System.out.println(ByteBufUtil.hexDump(compositeBuffer));
    }
}
