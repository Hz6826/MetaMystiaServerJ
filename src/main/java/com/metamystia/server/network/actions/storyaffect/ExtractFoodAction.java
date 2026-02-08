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
public class ExtractFoodAction extends AffectStoryAction{
    public ActionType type = ActionType.EXTRACT_FOOD;
    public SellableFood food;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
