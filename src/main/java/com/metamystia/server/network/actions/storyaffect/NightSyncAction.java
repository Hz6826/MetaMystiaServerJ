package com.metamystia.server.network.actions.storyaffect;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.network.actions.ActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "Network")
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class NightSyncAction extends AffectStoryAction{
    private ActionType type = ActionType.NIGHTSYNC;

    private float vx;
    private float vy;
    private float px;
    private float py;

    @Override
    protected void logActionReceived() {
        log.debug("Received [{}] - {}", this.getType(), this);
    }

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
