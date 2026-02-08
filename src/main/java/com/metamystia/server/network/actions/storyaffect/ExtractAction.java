package com.metamystia.server.network.actions.storyaffect;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.network.actions.ActionType;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class ExtractAction extends AffectStoryAction{
    public ActionType type = ActionType.EXTRACT;

    public int gridIndex;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
