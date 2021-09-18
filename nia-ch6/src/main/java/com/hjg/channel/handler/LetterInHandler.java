package com.hjg.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 *
 * 在数据首部加上所持有的字节。
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/17
 */
public class LetterInHandler extends ChannelInboundHandlerAdapter {
    private byte letter;

    public LetterInHandler(byte letter) {
        this.letter = letter;
    }

    /**
     * 打印收到的消息，并在首部添加字节。
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        String handlerName = ctx.name();

        System.out.println(handlerName + " received: " + byteBuf.toString(CharsetUtil.UTF_8));

        //事件传递给下一个ChannelHandler
        ctx.fireChannelRead(byteBuf.writeByte(letter));
    }

}
