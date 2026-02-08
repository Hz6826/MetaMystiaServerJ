package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class PingAction extends AbstractNetAction{
    public ActionType type = ActionType.PING;

    public int id;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
