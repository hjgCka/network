package com.hjg.channel.server;

import com.hjg.channel.handler.EchoLetterHandler;
import com.hjg.channel.handler.LetterInHandler;
import com.hjg.channel.handler.LetterOutHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/17
 */
public class EchoServer {

    public static void main(String[] args) throws InterruptedException {
        int port = 9005;

        new EchoServer().start(port);
    }

    public void start(int port) throws InterruptedException {
        NioEventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(group)
                    .channel(NioServerSocketChannel.class)
                    .localAddress(port)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline channelPipeline = ch.pipeline();
                            //添加3个入站处理器
                            channelPipeline.addLast("1", new LetterInHandler((byte)'A'));
                            channelPipeline.addLast("2", new LetterInHandler((byte)'B'));
                            channelPipeline.addLast("3", new LetterInHandler((byte)'C'));
                            channelPipeline.addLast("Echo", new EchoLetterHandler());

                            //添加2个出站处理器
                            channelPipeline.addLast("4", new LetterOutHandler((byte)'D'));
                            channelPipeline.addLast("5", new LetterOutHandler((byte)'E'));
                        }
                    });

            ChannelFuture future = bootstrap.bind().sync();
            future.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }
}
