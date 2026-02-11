package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.SellableFood;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class StoreSellableAction extends AbstractNetAction{
    private ActionType type = ActionType.STORE_SELLABLE;

    public enum StoreType {
        Food,
        Beverage
    }

    private int gridIndex;
    private SellableFood sellableFood;
    private int beverageId;
    private StoreType foodType;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
