package com.hjg.ws.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.stream.ChunkedNioFile;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/10/2
 */
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private final String wsUri;
    private static final File INDEX;

    static {
        URL location = HttpRequestHandler.class
                .getProtectionDomain()
                .getCodeSource().getLocation();
        try {
            String path = location.toURI() + "index.html";
            path = !path.contains("file:") ? path : path.substring(5);
            INDEX = new File(path);
        } catch (URISyntaxException e) {
            throw new IllegalStateException(
                    "Unable to locate index.html", e);
        }
    }

    public HttpRequestHandler (String wsUri) {
        this.wsUri = wsUri;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if(wsUri.equalsIgnoreCase(request.uri())) {
            ctx.fireChannelRead(request.retain());
        } else {
            if(HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }

            //读取index.html
            RandomAccessFile file = new RandomAccessFile(INDEX, "r");

            HttpResponse response = new DefaultHttpResponse(
                    request.protocolVersion(), HttpResponseStatus.OK);
            response.headers().set(
                    HttpHeaderNames.CONTENT_TYPE,
                    "text/html; charset=UTF-8");

            //如果请求是keepalive，添加对应的头部
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if(keepAlive) {
                response.headers().set(
                        HttpHeaderNames.CONTENT_LENGTH, file.length());
                response.headers().set( HttpHeaderNames.CONNECTION,
                        HttpHeaderValues.KEEP_ALIVE);
            }

            ctx.write(response);

            //将index.html写到客户端
            if (ctx.pipeline().get(SslHandler.class) == null) {
                ctx.write(new DefaultFileRegion(
                        file.getChannel(), 0, file.length()));
            } else {
                ctx.write(new ChunkedNioFile(file.getChannel()));
            }

            //最后发送LAST_CONTENT，标记response结束
            ChannelFuture future = ctx.writeAndFlush(
                    LastHttpContent.EMPTY_LAST_CONTENT);
            //如果不是keepalive，写完之后就关闭Channel
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(
                HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
