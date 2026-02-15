package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class ChangeHostRoleAction extends AbstractNetAction {
    private ActionType type = ActionType.CHANGE_HOST_ROLE;

    public enum ChangeType {
        GRANT,
        REVOKE
    }

    private ChangeType changeType;

    public ChangeHostRoleAction() {
        super();
    }

    public ChangeHostRoleAction(ChangeType changeType) {
        super();
        this.changeType = changeType;
    }
}
