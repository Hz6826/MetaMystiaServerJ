package com.metamystia.server.network.handlers;

import com.metamystia.server.util.DebugUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class DebugInboundHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        DebugUtils.logBufHex((ByteBuf) msg, "Incoming message");
        super.channelRead(ctx, msg);
    }
}
