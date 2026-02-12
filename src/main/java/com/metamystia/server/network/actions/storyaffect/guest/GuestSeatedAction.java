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
    private ActionType type = ActionType.GUEST_SEATED;

    private String guestUUID;
    private int deskId;
    private int seatId;
    private boolean firstSpawn;

    public GuestSeatedAction() {
        super();
    }

    public GuestSeatedAction(String guestUUID, int deskId, int seatId, boolean firstSpawn) {
        super();
        this.guestUUID = guestUUID;
        this.deskId = deskId;
        this.seatId = seatId;
        this.firstSpawn = firstSpawn;
    }
}
