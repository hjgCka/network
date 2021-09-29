package com.hjg.exception;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.TooLongFrameException;

import java.util.List;

/**
 * @description:
 * @author: hjg
 * @createdOn: 2021/9/29
 */
public class FrameChunkDecoder extends ByteToMessageDecoder {
    private final int maxFrameSize;

    public FrameChunkDecoder(int maxFrameSize) {
        this.maxFrameSize = maxFrameSize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int readableBytes = in.readableBytes();
        if(readableBytes > maxFrameSize) {
            //丢弃字节
            in.clear();
            //如果实现了exceptionCaught()，异常不会被抛出
            throw new TooLongFrameException();
        }

        ByteBuf buf = in.readBytes(readableBytes);
        out.add(buf);
    }
}
