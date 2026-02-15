package com.metamystia.server.network.handlers;

import com.hz6826.memorypack.serializer.MemoryPackSerializer;
import com.hz6826.memorypack.serializer.SerializerRegistry;
import com.metamystia.server.config.AccessControlManager;
import com.metamystia.server.config.ConfigManager;
import com.metamystia.server.core.packet.PacketDispatcher;
import com.metamystia.server.core.user.User;
import com.metamystia.server.network.actions.AbstractNetAction;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.MessageAction;
import com.metamystia.server.util.DebugUtils;
import com.metamystia.server.util.NetworkUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Handles game server communication.
 */
@Slf4j
public class MainPacketHandler extends ChannelInboundHandlerAdapter {
    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();
    private final AtomicInteger activeConnections = new AtomicInteger(0);

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

            String channelId = NetworkUtils.getChannelId(ctx);

            PacketDispatcher.dispatch(channelId, action);

            if (DebugUtils.echo) {
                sendAction(channelId, action);
            }
        } finally {
            in.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        log.error(cause.getMessage(), cause);
        if(ConfigManager.getConfig().isDebug()) {
            closeWithReason(NetworkUtils.getChannelId(ctx), cause.getMessage());
        } else {
            closeWithReason(NetworkUtils.getChannelId(ctx), "Server internal error occurred!");
        }
        if(ctx.channel().isActive()) {
            ctx.close();  // just in case
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress remoteAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        String clientIp = remoteAddress.getAddress().getHostAddress();
        log.info("New connection from {}", remoteAddress);

        int current = activeConnections.incrementAndGet();
        if (current > ConfigManager.getConfig().getMaxPlayers()) {
            ctx.writeAndFlush(MessageAction.ofServerMessage("Server is full!"))
                    .addListener(future -> ctx.close());
            return;
        }

        if (ConfigManager.getConfig().isWhitelist() && !AccessControlManager.isIpWhitelisted(clientIp)) {
            ctx.writeAndFlush(MessageAction.ofServerMessage("You are not whitelisted!"))
                    .addListener(future -> ctx.close());
        }
        if (ConfigManager.getConfig().isBlacklist() && AccessControlManager.isIpBlacklisted(clientIp)) {
            ctx.writeAndFlush(MessageAction.ofServerMessage("You are blacklisted!"))
                    .addListener(future -> ctx.close());
        }

        channels.put(NetworkUtils.getChannelId(ctx), ctx.channel());
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("Connection closed from {}", ctx.channel().remoteAddress());
        User.getUserByChannelId(NetworkUtils.getChannelId(ctx)).ifPresentOrElse(
                user -> {
                    user.getRoom().ifPresent(room -> room.removeUser(user));
                    User.removeUser(user);
                },
                () -> log.warn("User not found for channel {}", NetworkUtils.getChannelId(ctx))
        );
        activeConnections.decrementAndGet();
        channels.remove(NetworkUtils.getChannelId(ctx));
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
        } else {
            log.warn("Channel {} not found", channelId);
        }
    }

    public static void sendAction(String channelId, AbstractNetAction action) {
        withChannel(channelId, channel -> channel.writeAndFlush(action));
    }

    public static void closeWithReason(String channelId, String reason) {
        withChannel(channelId, channel -> {
            sendAction(channelId, MessageAction.ofServerMessage("Connection closed with reason: \n" + reason));
            User.getUserByChannelId(channelId).ifPresent(
                    user -> {
                        User.removeUser(user);
                        user.getRoom().ifPresent(room -> room.removeUser(user));
                    }
            );
            channel.close();
        });
    }
}
