package com.metamystia.server.util;

import io.netty.channel.ChannelHandlerContext;

public class NetworkUtils {
    public static String getChannelId(ChannelHandlerContext ctx) {
        return ctx.channel().id().asLongText();
    }
}
