package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
@NoArgsConstructor
public class ReadyAction extends AbstractNetAction{
    public ActionType type = ActionType.READY;

    public ReadyType readyType;
    public boolean allReady = false;

    public ReadyAction(ReadyType readyType, boolean allReady) {
        super();
        this.readyType = readyType;
        this.allReady = allReady;
    }

    @Override
    public void onReceivedDerived(String channelId) {

    }

    public enum ReadyType {
        DayOver,
        PrepOver
    }
}
