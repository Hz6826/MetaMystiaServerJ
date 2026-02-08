package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class IzakayaCloseAction extends AbstractNetAction{
    public ActionType type = ActionType.IZAKAYA_CLOSE;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
