package com.hjg.outbound;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * 测试Outbound，这里相当于编码器。
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/29
 */
public class AbsIntegerEncoderTest {

    @Test
    public void testEncoded() {
        ByteBuf origin = Unpooled.buffer();
        for(int i=1;  i<10; i++) {
            origin.writeInt(i*-1);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new AbsIntegerEncoder());
        Assert.assertTrue(channel.writeOutbound(origin));
        Assert.assertTrue(channel.finish());

        //读取字节
        for(int i=1; i<10; i++) {
            int value = (int) channel.readOutbound();
            System.out.println(value);
            Assert.assertEquals(i, value);
        }

        Assert.assertNull(channel.readOutbound());
    }
}
