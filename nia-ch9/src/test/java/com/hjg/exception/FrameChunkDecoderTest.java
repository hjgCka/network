package com.hjg.exception;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import io.netty.handler.codec.TooLongFrameException;
import io.netty.util.CharsetUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/29
 */
public class FrameChunkDecoderTest {

    @Test
    public void testFramesDecoded() {
        ByteBuf origin = Unpooled.buffer();
        for(int i=0; i<9; i++) {
            origin.writeByte(i);
        }
        ByteBuf input = origin.duplicate();

        EmbeddedChannel channel = new EmbeddedChannel(new FrameChunkDecoder(3));

        Assert.assertTrue(channel.writeInbound(input.readBytes(2)));

        try {
            channel.writeInbound(input.readBytes(4));
            Assert.fail();
        } catch (TooLongFrameException e) {
            System.out.println(e);
        }

        Assert.assertTrue(channel.writeInbound(input.readBytes(3)));
        Assert.assertTrue(channel.finish());

        //读取inbound
        ByteBuf read = (ByteBuf) channel.readInbound();
        Assert.assertEquals(origin.readSlice(2), read);
        read.release();

        read = (ByteBuf) channel.readInbound();
        Assert.assertEquals(origin.skipBytes(4).readSlice(3), read);

        read.release();
        origin.release();
    }
}
