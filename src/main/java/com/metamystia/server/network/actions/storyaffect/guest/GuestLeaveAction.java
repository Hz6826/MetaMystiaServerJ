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
public class GuestLeaveAction extends SendAffectStoryAction {
    private ActionType type = ActionType.GUEST_LEAVE;

    public enum LeaveType {
        PayAndLeave,            // Host only
        ExBadLeave,             // Host only
        RepelAndLeavePay,       // Both ok
        RepelAndLeaveNoPay,     // Both ok
        PatientDepletedLeave,   // Host only
        PlayerRepel,            // Both ok
        LeaveFromDesk,          // the last function if all above failed, Host only
        LeaveFromQueue          // Both ok
        // FIXME: Any other leave method?
    }

    private String guestUUID;
    private LeaveType lType;

    public GuestLeaveAction() {
        super();
    }

    public GuestLeaveAction(String guestUUID, LeaveType lType) {
        super();
        this.guestUUID = guestUUID;
        this.lType = lType;
    }
}
