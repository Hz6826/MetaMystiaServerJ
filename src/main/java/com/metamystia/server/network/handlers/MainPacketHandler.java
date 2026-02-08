package com.metamystia.server.network.handlers;

import com.hz6826.memorypack.serializer.MemoryPackSerializer;
import com.hz6826.memorypack.serializer.SerializerRegistry;
import com.metamystia.server.core.room.User;
import com.metamystia.server.network.actions.AbstractNetAction;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.HelloAction;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Handles game server communication.
 */
@Slf4j
public class MainPacketHandler extends ChannelInboundHandlerAdapter {
    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();

    public static boolean echo = false;  // debug

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;

        in.skipBytes(5);  // 01 01 00 00 00 TODO

        int index = in.readByte();
        if(index < 0 || index >= ActionType.values().length) {
            log.error("Invalid action type index: {}", index);
            ctx.close();
            return;
        }
        ActionType actionType = ActionType.values()[index];
        Class<?> clazz = actionType.getRelatedActionClass();
        if(clazz == null) {
            log.error("Action type {} has no related action class", actionType);
            ctx.close();
            return;
        }
        MemoryPackSerializer<? extends AbstractNetAction> serializer = (MemoryPackSerializer<? extends AbstractNetAction>) SerializerRegistry.getInstance().getSerializer(clazz);

        AbstractNetAction action = serializer.deserialize(in);

        action.onReceived(ctx.channel().id().asLongText());

        if (action instanceof HelloAction helloAction) {
            User.createUser(helloAction, ctx.channel().id().asLongText());
            log.info("User registered: {}, channel: {}", helloAction.getSenderId(), ctx.channel().id().asLongText());
        }

        if (echo) {
            sendAction(ctx.channel().id().asLongText(), action);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("New connection from {}", ctx.channel().remoteAddress());
        channels.put(ctx.channel().id().asLongText(), ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Connection closed from {}", ctx.channel().remoteAddress());
        channels.remove(ctx.channel().id().asLongText());
        super.channelInactive(ctx);
    }

    private static Channel getChannel(String channelId) {
        return channels.get(channelId);
    }

    public static void sendAction(String channelId, AbstractNetAction action) {
        Channel channel = getChannel(channelId);
        if(channel == null) {
            log.error("Channel {} not found", channelId);
            return;
        }
        channel.writeAndFlush(action);
    }
}
