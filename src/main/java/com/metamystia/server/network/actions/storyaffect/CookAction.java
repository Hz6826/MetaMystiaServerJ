package com.metamystia.server.network.actions.storyaffect;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.SellableFood;
import com.metamystia.server.network.actions.ActionType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class CookAction extends AffectStoryAction{
    private ActionType type = ActionType.COOK;

    private int gridIndex;
    private int recipeId;

    private SellableFood food;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
