package com.metamystia.server.network.actions.storyaffect.guest;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.storyaffect.SendAffectStoryAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class GuestSeatedAction extends SendAffectStoryAction {
    public ActionType type = ActionType.GUEST_SEATED;

    public String guestUUID;
    public int deskId;
    public int seatId;
    public boolean firstSpawn;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
