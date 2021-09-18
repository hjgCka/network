package com.hjg.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.util.CharsetUtil;

/**
 * 在数据的尾部加上所持有的字节。
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/17
 */
public class LetterOutHandler extends ChannelOutboundHandlerAdapter {
    private byte letter;

    public LetterOutHandler(byte letter) {
        this.letter = letter;
    }

    /**
     * 从尾到头执行ChannelOutboundHandler的read方法，然后执行ChannelInboundHandler的channelRead方法。
     * 所以这里能够控制，是否读取数据到ChannelInboundHandler。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void read(ChannelHandlerContext ctx) throws Exception {

        String handlerName = ctx.name();
        System.out.println(handlerName + " read: ");
        super.read(ctx);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {

        ByteBuf byteBuf = (ByteBuf) msg;

        String handlerName = ctx.name();
        System.out.println(handlerName + " write: " + byteBuf.toString(CharsetUtil.UTF_8));

        super.write(ctx, byteBuf.writeByte(letter), promise);
    }
}
