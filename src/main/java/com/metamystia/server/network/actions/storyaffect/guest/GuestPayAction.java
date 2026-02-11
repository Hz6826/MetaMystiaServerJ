package com.metamystia.server.network.actions.storyaffect.guest;

import com.hz6826.memorypack.annotation.MemoryPackAllowSerialize;
import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.EventManager;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.storyaffect.SendAffectStoryAction;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class GuestPayAction extends SendAffectStoryAction {
    private ActionType type = ActionType.GUEST_PAY;

    public enum GuestPayType {
        FUND,
        TIP,
        COMBO
    }

    private GuestPayType payType;
    private int amount;

    @MemoryPackAllowSerialize
    private EventManager.ServeType serveType;

    @MemoryPackAllowSerialize
    private EventManager.MathOperation mathOperation;

    private float ComboBuff;
    private float MoodBuff;
    private float ExtraBuff;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
