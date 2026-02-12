package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class SelectAction extends AbstractNetAction{
    private ActionType type = ActionType.SELECT;

    private String mapLabel = "";
    private int mapLevel = 0;

    public SelectAction() {
        super();
    }

    public SelectAction(String mapLabel, int mapLevel) {
        super();
        this.mapLabel = mapLabel;
        this.mapLevel = mapLevel;
    }
}
