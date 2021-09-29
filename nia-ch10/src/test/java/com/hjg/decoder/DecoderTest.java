package com.hjg.decoder;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.embedded.EmbeddedChannel;
import org.junit.Test;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/29
 */
public class DecoderTest {

    @Test
    public void decoderTest() {
        ByteBuf origin = Unpooled.buffer();
        for(int i=0; i<10; i++) {
            //如果是写字节，就是4个字节一个int
            //origin.writeByte(i);
            origin.writeInt(i);
        }

        EmbeddedChannel channel = new EmbeddedChannel(new ChannelInitializer<EmbeddedChannel>() {
            @Override
            protected void initChannel(EmbeddedChannel ch) throws Exception {
                ch.pipeline().addLast(new ToIntegerDecoder());
                ch.pipeline().addLast(new IntegerToStringDecoder());
            }
        });

        channel.writeInbound(origin);

        for(int i=0; i<10; i++) {
            String value = (String) channel.readInbound();
            System.out.print(value);
        }
    }
}
