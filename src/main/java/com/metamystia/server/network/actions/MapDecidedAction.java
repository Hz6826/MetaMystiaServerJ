package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * => ConfirmAction.cs
 */
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class MapDecidedAction extends AbstractNetAction{
    private ActionType type = ActionType.MAP_DECIDED;

    private String mapLabel = "";
    private int level = 0;

    public MapDecidedAction() {
        super();
    }

    public MapDecidedAction(String mapLabel, int level) {
        super();
        this.mapLabel = mapLabel;
        this.level = level;
    }
}
