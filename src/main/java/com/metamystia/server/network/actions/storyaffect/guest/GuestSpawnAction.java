package com.metamystia.server.network.actions.storyaffect.guest;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.GuestInfo;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.storyaffect.SendAffectStoryAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class GuestSpawnAction extends SendAffectStoryAction {
    private ActionType type = ActionType.GUEST_SPAWN;

    private GuestInfo guestInfo;
    private String uuid;

    public GuestSpawnAction() {
        this.guestInfo = new GuestInfo();
    }

    public GuestSpawnAction(GuestInfo guestInfo, String uuid) {
        this.guestInfo = guestInfo;
        this.uuid = uuid;
    }
}
