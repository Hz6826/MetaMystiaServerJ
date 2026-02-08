package com.metamystia.server.network.actions.storyaffect.guest;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.storyaffect.SendAffectStoryAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class GuestInviteAction extends SendAffectStoryAction {
    public ActionType type = ActionType.GUEST_INVITE;

    public List<Integer> invitedGuestIDs;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
