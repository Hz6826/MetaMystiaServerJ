package com.metamystia.server.network.handlers;

import com.hz6826.memorypack.serializer.MemoryPackSerializer;
import com.hz6826.memorypack.serializer.SerializerRegistry;
import com.metamystia.server.core.config.AccessControlManager;
import com.metamystia.server.core.config.ConfigManager;
import com.metamystia.server.core.packet.PacketDispatcher;
import com.metamystia.server.core.plugin.PluginManager;
import com.metamystia.server.core.room.RoomManager;
import com.metamystia.server.core.user.UserManager;
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
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * Handles game server communication.
 */
@Slf4j
public class MainPacketHandler extends ChannelInboundHandlerAdapter {
    private static final Map<String, Channel> channels = new ConcurrentHashMap<>();
    private final AtomicInteger activeConnections = new AtomicInteger(0);

    private volatile ScheduledFuture<?> helloTimeoutFuture;
    private volatile boolean helloReceived = false;

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

            if (actionType == ActionType.HELLO) {
                helloReceived = true;
                if (helloTimeoutFuture != null && !helloTimeoutFuture.isDone()) {
                    helloTimeoutFuture.cancel(false);
                }
            }

            PacketDispatcher.dispatch(channelId, action);

            if (DebugUtils.echo) {
                sendAction(channelId, action);
            }

            if (in.readableBytes() > 0) {
                log.warn("Unexpected bytes after action {}, total hex below:", action);
                in.resetReaderIndex();
                DebugUtils.logBufHex(in, "Hex for action " + action);
            }
        } finally {
            in.release();
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        if (cause instanceof SocketException && cause.getMessage().contains("Connection reset")) {
            log.warn("Connection reset by peer: {}", cause.getMessage());
            return;
        }
        log.error(cause.getMessage(), cause);
        String reason = ConfigManager.getConfig().isDebug() ? cause.getMessage() : "Server internal error occurred!";
        try {
            if (ctx.channel().isActive()) {
                sendAction(NetworkUtils.getChannelId(ctx), MessageAction.ofServerMessage(reason));
            }
        } catch (Exception e) {
            log.error("Error closing channel: ", e);
        }
        ctx.close();
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
        helloTimeoutFuture = ctx.executor().schedule(() -> {
            if (!helloReceived && ctx.channel().isActive()) {
                log.warn("No HelloAction received within {}s, closing connection from {}", ConfigManager.getConfig().getHelloTimeoutSeconds(), ctx.channel().remoteAddress());
                ctx.writeAndFlush(MessageAction.ofServerMessage("Connection timeout: no hello message"))
                        .addListener(f -> ctx.close());
            }
        }, ConfigManager.getConfig().getHelloTimeoutSeconds(), TimeUnit.SECONDS);
        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        String channelId = NetworkUtils.getChannelId(ctx);
        log.info("Connection closed from {}", ctx.channel().remoteAddress());
        try {
            UserManager.getUserByChannelId(channelId).ifPresentOrElse(
                    user -> {
                        PluginManager.getAuthProvider().onUserLeave(user);
                        try {
                            user.getRoom().ifPresent(room -> {
                                try {
                                    room.removeUser(user, RoomManager.NO_ROOM);
                                } catch (Exception e) {
                                    log.error("Error removing user from room: ", e);
                                }
                            });
                        } finally {
                            UserManager.removeUser(user);
                        }
                    },
                    () -> log.warn("User not found for channel {}", channelId)
            );
        } catch (Exception e) {
            log.error("Error during channel inactive cleanup: ", e);
        } finally {
            activeConnections.decrementAndGet();
            if (helloTimeoutFuture != null && !helloTimeoutFuture.isDone()) {
                helloTimeoutFuture.cancel(false);
            }
            channels.remove(channelId);
            super.channelInactive(ctx);
        }
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

    public static String getIp(String channelId) {
        return ((InetSocketAddress) getChannel(channelId).remoteAddress()).getAddress().getHostAddress();
    }

    public static void sendAction(String channelId, AbstractNetAction action) {
        withChannel(channelId, channel -> channel.writeAndFlush(action));
    }

    public static void closeWithReason(String channelId, String reason) {
        withChannel(channelId, channel -> {
            channel.writeAndFlush(MessageAction.ofServerMessage("Connection closed: " + reason))
                    .addListener(future -> channel.close());
            channel.close();
        });
    }
}
