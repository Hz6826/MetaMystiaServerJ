package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class ReadyAction extends AbstractNetAction{
    private ActionType type = ActionType.READY;

    private ReadyType readyType;
    private boolean allReady = false;

    public ReadyAction() {
        super();
    }

    public ReadyAction(ReadyType readyType, boolean allReady) {
        super();
        this.readyType = readyType;
        this.allReady = allReady;
    }

    public enum ReadyType {
        DayOver,
        PrepOver
    }
}
