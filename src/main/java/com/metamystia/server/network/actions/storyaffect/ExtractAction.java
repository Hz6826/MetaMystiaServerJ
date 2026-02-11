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
public class ExtractAction extends AffectStoryAction{
    private ActionType type = ActionType.EXTRACT;

    private int gridIndex;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
