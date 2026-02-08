package com.metamystia.server.network.actions.storyaffect;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.SellableFood;
import com.metamystia.server.network.actions.ActionType;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class CookAction extends AffectStoryAction{
    public ActionType type = ActionType.COOK;

    public int gridIndex;
    public int recipeId;

    public SellableFood food;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
