package com.hjg.network.server;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ChannelHandler.Sharable表明这个ChannelHandler可以安全地被多个channels所共享。
 *
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/26
 */
@ChannelHandler.Sharable
public class ServerInboundHandler extends ChannelInboundHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(ServerInboundHandler.class);

    /**
     * 对每个到来的消息进行调用。
     * @param ctx
     * @param msg
     * @throws Exception
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;

        String received = in.toString(CharsetUtil.UTF_8);
        logger.info("server received {}", received);

        //将收到的消息发送出去，由于write操作是异步的，channelRead()返回时write()操作可能还未完成
        //所以ChannelInboundHandlerAdapter，不会在这个时间点上释放消息
        ctx.write(in);
    }

    /**
     * 通知Handler，最后一次对channelRead()的调用，是来自当前批次的最后一个消息。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //将待定的消息发送到远程对等点，
        //并为Channel添加一个监听器，在write完成后，关闭channel
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 在读操作时抛出异常时调用。
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        logger.error("读取时发生异常", cause);

        //关闭连接
        ctx.close();
    }
}
