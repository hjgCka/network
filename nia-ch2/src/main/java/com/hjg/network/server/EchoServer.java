package com.hjg.network.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/3/29
 */
public class EchoServer {

    private static final Logger logger = LoggerFactory.getLogger(EchoServer.class);
    private final int port;

    public EchoServer(int port) {
        this.port = port;
    }

    public static void main(String[] args) throws InterruptedException {
        if(args.length != 1) {
            logger.error(EchoServer.class.getName() + " <port>");
            return;
        }
        int port = Integer.valueOf(args[0]);

        new EchoServer(port).start();
    }

    public void start() throws InterruptedException {
        //接收关于入境消息的通知
        final ServerInboundHandler inboundHandler = new ServerInboundHandler();
        EventLoopGroup group = new NioEventLoopGroup();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(group)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))

                //新的连接到来时，会创建一个新的子Channel
                //添加Handler到channel的ChannelPipeline
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(inboundHandler);
                    }
                });


            //绑定服务器，默认为异步，这里使用同步等待绑定完成
            ChannelFuture channelFuture = serverBootstrap.bind().sync();
            //得到Channel的CloseFuture，并阻塞线程直到完成。等待服务器Channel的关闭
            channelFuture.channel().closeFuture().sync();
        } finally {
            //关闭EventLoopGroup，释放所有资源
            group.shutdownGracefully().sync();
        }
    }
}
