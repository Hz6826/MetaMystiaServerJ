package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.util.LogLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class PongAction extends AbstractNetAction{
    private ActionType type = ActionType.PONG;

    private int id;

    public PongAction() {
        super();
    }

    public PongAction(int id) {
        super();
        this.id = id;
    }

    public LogLevel getLogLevel() {
        return LogLevel.DEBUG;
    }
}
