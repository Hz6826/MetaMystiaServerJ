package com.metamystia.server.network.actions.storyaffect.guest;

import com.hz6826.memorypack.annotation.MemoryPackAllowSerialize;
import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.EventManager;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.storyaffect.SendAffectStoryAction;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class GuestPayAction extends SendAffectStoryAction {
    public ActionType type = ActionType.GUEST_PAY;

    public enum GuestPayType {
        FUND,
        TIP,
        COMBO
    }

    public GuestPayType payType;
    public int amount;

    @MemoryPackAllowSerialize
    public EventManager.ServeType serveType;

    @MemoryPackAllowSerialize
    public EventManager.MathOperation mathOperation;

    public float ComboBuff;
    public float MoodBuff;
    public float ExtraBuff;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
