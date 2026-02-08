package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jspecify.annotations.NonNull;

/**
 * => ConfirmAction.cs
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class MapDecidedAction extends AbstractNetAction{
    public ActionType type = ActionType.MAP_DECIDED;

    public String mapLabel = "";
    public int level = 0;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
