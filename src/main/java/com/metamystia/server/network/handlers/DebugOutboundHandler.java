package com.metamystia.server.network.handlers;

import com.metamystia.server.core.config.ConfigManager;
import com.metamystia.server.util.DebugUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;

public class DebugOutboundHandler extends ChannelOutboundHandlerAdapter {
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (ConfigManager.getConfig().isLogHex()) DebugUtils.logBufHex((ByteBuf) msg, "Outgoing message");
        super.write(ctx, msg, promise);
    }
}
