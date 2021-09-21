package com.hjg.channel.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/17
 */
public class EchoClient {

    public static void main(String[] args) throws InterruptedException {
        String host = "127.0.0.1";
        int port = 9005;

        new EchoClient().start(host, port);
    }

    public void start(String host, int port) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(host, port)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            channelPipeline.addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect().sync();
            //channel关闭时，会通知这个Future。然后这一行就不再阻塞能够放行。
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
