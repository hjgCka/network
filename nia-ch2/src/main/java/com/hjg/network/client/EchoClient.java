package com.hjg.network.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/29
 */
@AllArgsConstructor
public class EchoClient {

    private static final Logger logger = LoggerFactory.getLogger(EchoClient.class);

    private final String host;
    private final int port;

    public static void main(String[] args) throws InterruptedException {
        if(args.length != 2) {
            logger.info("Usage " + EchoClient.class.getName() + " <host> <port>");
            return;
        }

        String host = args[0];
        int port = Integer.valueOf(args[1]);
        new EchoClient(host, port).start();
    }

    public void start() throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                    .channel(NioSocketChannel.class)
                    .remoteAddress(new InetSocketAddress(host, port))
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(new ClientInboundHandler());
                        }
                    });

            ChannelFuture channelFuture = bootstrap.connect().sync();
            channelFuture.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully().sync();
        }

    }
}
