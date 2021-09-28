package com.hjg.bootstrap;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.net.InetSocketAddress;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/21
 */
public class BootStrapClient {

    public static void main(String[] args) throws InterruptedException {
        BootStrapClient bootStrapClient = new BootStrapClient();
        bootStrapClient.bootstrap();
    }

    public void bootstrap() throws InterruptedException {
        NioEventLoopGroup nioEventLoopGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(nioEventLoopGroup)
                    .channel(NioSocketChannel.class)
                    .handler(new SimpleChannelInboundHandler<ByteBuf>() {
                        @Override
                        protected void channelRead0(ChannelHandlerContext ctx, ByteBuf msg) throws Exception {
                            System.out.println("Received data");
                        }
                    });

            ChannelFuture future = bootstrap.connect(
                    new InetSocketAddress("www.manning.com", 80));

            future.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if(channelFuture.isSuccess()) {
                        System.out.println("Connection established");
                    } else {
                        System.out.println("Connection attempt failed");
                        channelFuture.cause().printStackTrace();
                    }
                }//这里的关闭需要用同步的方式，不然连接没建立就开始关闭了
            }).channel().closeFuture().sync();


        } finally {
            nioEventLoopGroup.shutdownGracefully();
        }
    }
}
