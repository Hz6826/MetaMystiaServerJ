package com.metamystia.server.network.actions.storyaffect;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.network.actions.ActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class NightSyncAction extends AffectStoryAction{
    public ActionType type = ActionType.NIGHTSYNC;

    public float vx;
    public float vy;
    public float px;
    public float py;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
