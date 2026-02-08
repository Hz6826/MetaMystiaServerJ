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
public class QTEAction extends AffectStoryAction{
    public ActionType type = ActionType.QTE;

    public int gridIndex;
    public float QTEScore;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
