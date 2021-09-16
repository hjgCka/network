package com.hjg.alloc.bytebuf;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.CompositeByteBuf;
import io.netty.buffer.Unpooled;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * 使用Unpooled工具类进行分配，这个类使得ByteBuf在非网络环境下也能用，并能
 * 受益于ByteBuf的高性能和可扩展的API，而不用其它Netty组件。
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/15
 */
public class UnpooledTest {

    @Test
    public void alloc() {
        ByteBuf heapByteBuf = Unpooled.buffer(10);
        //分配在heap上的有支撑数组
        Assert.assertTrue(heapByteBuf.hasArray());
        //可以直接返回支撑字节数组的引用
        //heapByteBuf.array();

        ByteBuf directBuffer = Unpooled.directBuffer(16);
        //分配在直接内存上的没有支撑数组
        Assert.assertFalse(directBuffer.hasArray());

        CompositeByteBuf compositeBuffer = Unpooled.compositeBuffer();
        //不增加write索引
        compositeBuffer.addComponents(heapByteBuf, directBuffer);
        System.out.println(compositeBuffer.readableBytes());
    }

    @Test
    public void alloc2() {
        byte[] bytes = {1, 2, 3};
        //对wrappedBuffer的修改会同步反应到字节数组
        ByteBuf wrappedBuffer = Unpooled.wrappedBuffer(bytes);

        //这里的复制，完全新建了一个对象，并没有指向老的内存区域
        ByteBuf copiedBuffer = Unpooled.copiedBuffer(wrappedBuffer);
    }
}
