package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class PlayerIdChangeAction extends AbstractNetAction{
    private ActionType type = ActionType.PLAYER_ID_CHANGE;

    private String newPlayerId;

    public PlayerIdChangeAction() {
        super();
    }

    public PlayerIdChangeAction(String newPlayerId) {
        super();
        this.newPlayerId = newPlayerId;
    }
}
