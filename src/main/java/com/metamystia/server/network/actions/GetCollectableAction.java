package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class GetCollectableAction extends AbstractNetAction{
    private ActionType type = ActionType.GET_COLLECTABLE;

    private String Collectable;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
