package com.metamystia.server.network.actions.storyaffect;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.user.UserManager;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.util.LogLevel;
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

    public NightSyncAction() {
        super();
    }

    public NightSyncAction(float vx, float vy, float px, float py) {
        super();
        this.vx = vx;
        this.vy = vy;
        this.px = px;
        this.py = py;
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.DEBUG;
    }

    @Override
    public boolean onReceivedDerived(String channelId) {
        UserManager.getUserByChannelId(channelId).ifPresent(
                user -> user.getUserPos().updateFromNightSyncAction(this)
        );
        return false;
    }
}
