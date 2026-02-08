package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.SellableFood;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class StoreSellableAction extends AbstractNetAction{
    public ActionType type = ActionType.STORE_SELLABLE;

    public enum StoreType {
        Food,
        Beverage
    }

    public int gridIndex;
    public SellableFood sellableFood;
    public int beverageId;
    public StoreType foodType;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
