package com.metamystia.server.network.actions.storyaffect.guest;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.GuestOrder;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.storyaffect.SendAffectStoryAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class GuestGenNormalOrderAction extends SendAffectStoryAction {
    private ActionType type = ActionType.GUEST_GEN_NORMAL_ORDER;

    private String guestUUID;
    private GuestOrder order;
    private String message;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
