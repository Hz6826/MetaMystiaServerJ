package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class OverrideRoleAction extends AbstractNetAction {
    private ActionType type = ActionType.OVERRIDE_ROLE;

    /// <summary>
    /// The role to override on the receiving side. null means clear override (follow transport role).
    /// Encoded as: 0 = null, 1 = Host, 2 = Client.
    /// </summary>
    public enum Role {
        CLEAR,
        HOST,
        CLIENT
    }

    private Role role;

    public OverrideRoleAction() {
        super();
    }

    public OverrideRoleAction(Role role) {
        super();
        this.role = role;
    }
}
