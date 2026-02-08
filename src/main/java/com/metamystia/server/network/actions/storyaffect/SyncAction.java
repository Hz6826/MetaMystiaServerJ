package com.metamystia.server.network.actions.storyaffect;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.network.actions.ActionType;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Network")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class SyncAction extends AffectStoryAction{
    public ActionType type = ActionType.SYNC;

    public float vx;
    public float vy;
    public float px;
    public float py;
    public boolean isSprinting;
    public String mapLabel;

    @Override
    protected void logActionReceived() {
        log.debug("Received [{}] - {}", this.getType(), this);
    }

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
