package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class PrepAction extends AbstractNetAction{
    private ActionType type = ActionType.PREP;

    private Table prepTable;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
