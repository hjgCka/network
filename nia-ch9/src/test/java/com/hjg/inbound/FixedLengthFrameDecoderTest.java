package com.hjg.inbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.util.ByteProcessor;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/28
 */
public class FixedLengthFrameDecoderTest {

    @Test
    public void testFramesDecoded() {
        //在heap上申请一个空间，默认为256个字节
        ByteBuf origin = Unpooled.buffer();
        for(int i=0; i<9; i++) {
            origin.writeByte(i);
        }
        System.out.println(origin.capacity());

        ByteBuf input = origin.duplicate();

        int length = 3;
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(length));
        //input.retain()将引用计数+1
        Assert.assertTrue(channel.writeInbound(input.retain()));
        //将channel标记为finish
        Assert.assertTrue(channel.finish());

        //调用readInbound方法后，才会被channelHandler处理
        ByteBuf read = (ByteBuf) channel.readInbound();
        //这里的read使用的是直接内存，没有支撑数组
        read.forEachByte(new ByteProcessor() {
            @Override
            public boolean process(byte value) throws Exception {
                System.out.print(value);
                return true;
            }
        });
        System.out.println();
        //返回新的ByteBuf，并且将readerIndex加上length
        Assert.assertEquals(origin.readSlice(length), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        read.forEachByte(new ByteProcessor() {
            @Override
            public boolean process(byte value) throws Exception {
                System.out.print(value);
                return true;
            }
        });
        System.out.println();
        Assert.assertEquals(origin.readSlice(length), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        read.forEachByte(new ByteProcessor() {
            @Override
            public boolean process(byte value) throws Exception {
                System.out.print(value);
                return true;
            }
        });
        System.out.println();
        Assert.assertEquals(origin.readSlice(length), read);
        read.release();

        Assert.assertNull(channel.readInbound());
        origin.release();
    }

    @Test
    public void testFramesDecoded2() {
        //在heap上申请一个空间，默认为256个字节
        ByteBuf origin = Unpooled.buffer();
        for(int i=0; i<9; i++) {
            origin.writeByte(i);
        }
        System.out.println(origin.capacity());

        ByteBuf input = origin.duplicate();

        int length = 3;
        EmbeddedChannel channel = new EmbeddedChannel(new FixedLengthFrameDecoder(length));
        //第一次添加2个字节会返回false，因为只添加2个字节的话，调用channel.readInbound不会读取到数据
        Assert.assertFalse(channel.writeInbound(input.readBytes(2)));
        Assert.assertTrue(channel.writeInbound(input.readBytes(7)));

        //将channel标记为finish
        Assert.assertTrue(channel.finish());

        //调用readInbound方法后，才会被channelHandler处理
        ByteBuf read = (ByteBuf) channel.readInbound();
        //这里的read使用的是直接内存，没有支撑数组
        read.forEachByte(new ByteProcessor() {
            @Override
            public boolean process(byte value) throws Exception {
                System.out.print(value);
                return true;
            }
        });
        System.out.println();
        //返回新的ByteBuf，并且将readerIndex加上length
        Assert.assertEquals(origin.readSlice(length), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        read.forEachByte(new ByteProcessor() {
            @Override
            public boolean process(byte value) throws Exception {
                System.out.print(value);
                return true;
            }
        });
        System.out.println();
        Assert.assertEquals(origin.readSlice(length), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        read.forEachByte(new ByteProcessor() {
            @Override
            public boolean process(byte value) throws Exception {
                System.out.print(value);
                return true;
            }
        });
        System.out.println();
        Assert.assertEquals(origin.readSlice(length), read);
        read.release();

        Assert.assertNull(channel.readInbound());
        origin.release();
    }
}
