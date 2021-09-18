package com.hjg.channel.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/18
 */
public class EchoLetterHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        String str = byteBuf.toString(CharsetUtil.UTF_8);

        String handlerName = ctx.name();
        System.out.println("handlerName : " + handlerName + ", write: " + str);

        //Echo这个ChannelHandler开始，往前找ChannelOutboundHandler
        //ctx.writeAndFlush(msg);

        //从pipeline的尾部开始，找ChannelOutboundHandler
        ctx.pipeline().writeAndFlush(msg);
    }

}
