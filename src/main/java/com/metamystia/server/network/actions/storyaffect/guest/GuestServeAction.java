package com.metamystia.server.network.actions.storyaffect.guest;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.SellableFood;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.storyaffect.SendAffectStoryAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class GuestServeAction extends SendAffectStoryAction {
    private ActionType type = ActionType.GUEST_SERVE;

    public enum ServeType {
        FOOD,
        BEVERAGE,
        BOTH
    }

    private String guestUUID;
    private SellableFood food;
    private int beverageId;
    private ServeType foodType;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
