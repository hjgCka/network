package com.hjg.network.client;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * 标记这个类可以被多个Channel所共享。
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/29
 */
@ChannelHandler.Sharable
public class ClientInboundHandler extends SimpleChannelInboundHandler<ByteBuf> {

    private static final Logger logger = LoggerFactory.getLogger(ClientInboundHandler.class);

    /**
     * 从服务器收到消息之后调用。
     * 服务器发来的消息可能是分块传输(Transfer-Encoding:chunked)，客户端无法保证一次性收到服务器发来的所有字节。
     * 这个方法可能会被调用多次。
     * TCP作为面向流的协议，保证收到的字节与服务器发送的字节一致。
     * @param channelHandlerContext
     * @param byteBuf
     * @throws Exception
     */
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
        //已经处理完成了这个消息，
        // 该方法返回时SimpleChannelInboundHandler会释放指向保存该消息的ByteBuf的内存引用
        logger.info("client received: {}", byteBuf.toString(CharsetUtil.UTF_8));
    }

    /**
     * 连接到服务器之后进行调用。
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        //通知channel激活之后，向服务器发送这条消息
        ctx.writeAndFlush(Unpooled.copiedBuffer("Netty rocks!", CharsetUtil.UTF_8));
    }

    /**
     * 处理时出现异常时调用。
     * @param ctx
     * @param cause
     * @throws Exception
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        logger.error("处理时出错", cause);
        ctx.close();
    }
}
