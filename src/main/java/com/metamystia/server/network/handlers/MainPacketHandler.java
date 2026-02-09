package com.metamystia.server.network.handlers;

import com.hz6826.memorypack.serializer.MemoryPackSerializer;
import com.hz6826.memorypack.serializer.SerializerRegistry;
import com.metamystia.server.core.room.User;
import com.metamystia.server.network.actions.AbstractNetAction;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.MessageAction;
import com.metamystia.server.util.DebugUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

/**
 * Handles game server communication.
 */
@Slf4j
public class MainPacketHandler extends ChannelInboundHandlerAdapter {
    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf in = (ByteBuf) msg;
        try {
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

            String channelId = ctx.channel().id().asLongText();

            action.onReceived(channelId);

            if (DebugUtils.echo) {
                sendAction(channelId, action);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            ctx.close();
        } finally {
            in.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        closeWithReason(ctx.channel().id().asLongText(), cause.getMessage());
        if(ctx.channel().isActive()) {
            ctx.close();  // just in case
        }
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
        User.removeUser(User.getUserByChannelId(ctx.channel().id().asLongText()));
        channels.remove(ctx.channel().id().asLongText());
        super.channelInactive(ctx);
    }

    private static Channel getChannel(String channelId) {
        Channel channel = channels.get(channelId);
        if (channel == null) {
            log.error("Channel {} not found", channelId);
        }
        return channel;
    }

    private static void withChannel(String channelId, Consumer<Channel> action) {
        Channel channel = getChannel(channelId);
        if (channel != null) {
            action.accept(channel);
        }
    }

    public static void sendAction(String channelId, AbstractNetAction action) {
        withChannel(channelId, channel -> channel.writeAndFlush(action));
    }

    public static void closeWithReason(String channelId, String reason) {
        withChannel(channelId, channel -> {
            sendAction(channelId, new MessageAction("Connection closed with reason: \n" + reason));
            channel.close();
        });
    }
}
