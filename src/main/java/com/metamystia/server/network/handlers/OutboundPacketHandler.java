package com.metamystia.server.network.handlers;

import com.hz6826.memorypack.serializer.MemoryPackSerializer;
import com.hz6826.memorypack.serializer.SerializerRegistry;
import com.metamystia.server.core.user.UserManager;
import com.metamystia.server.network.actions.AbstractNetAction;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.util.LogLevel;
import com.metamystia.server.util.NetworkUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;

@Slf4j
public class OutboundPacketHandler extends ChannelOutboundHandlerAdapter {
    @SuppressWarnings("unchecked")
    @Override
    public void write(ChannelHandlerContext ctx, Object msg, io.netty.channel.ChannelPromise promise) throws Exception {
        AbstractNetAction action = (AbstractNetAction) msg;

        // action.setSenderId(1L);

        ActionType actionType = action.getType();

        MemoryPackSerializer<? extends AbstractNetAction> serializer =
                (MemoryPackSerializer<? extends AbstractNetAction>) SerializerRegistry.getInstance().getSerializer(actionType.getRelatedActionClass());

        ByteBuf out = ctx.alloc().buffer();

        try {
            out.writeByte(0x01);
            out.writeByte(0x01);
            out.writeByte(0x00);
            out.writeByte(0x00);
            out.writeByte(0x00);

            out.writeByte(actionType.ordinal());

            serializeAction(serializer, action, out);
        } catch (Exception e) {
            log.error("Failed to serialize and send action: {}", e.getMessage(), e);
            out.release();
        }

        super.write(ctx, out, promise);

        LogLevel logLevel = action.getLogLevel();
        if (logLevel == LogLevel.DEBUG) {
            log.debug("Sent [{}] to {} - {}", actionType, UserManager.getUserOrChannelIdString(NetworkUtils.getChannelId(ctx)), action);
        } else if (logLevel == LogLevel.INFO) {
            log.info("Sent [{}] to {} - {}", actionType, UserManager.getUserOrChannelIdString(NetworkUtils.getChannelId(ctx)), action);
        } else if (logLevel == LogLevel.WARN) {
            log.warn("Sent [{}] to {} - {}", actionType, UserManager.getUserOrChannelIdString(NetworkUtils.getChannelId(ctx)), action);
        } else if (logLevel == LogLevel.ERROR) {
            log.error("Sent [{}] to {} - {}", actionType, UserManager.getUserOrChannelIdString(NetworkUtils.getChannelId(ctx)), action);
        }
    }

    private <T extends AbstractNetAction> void serializeAction(
            @NonNull MemoryPackSerializer<T> serializer, AbstractNetAction action, ByteBuf out) {
        @SuppressWarnings("unchecked")
        T typedAction = (T) action;
        serializer.serialize(typedAction, out);
    }
}
