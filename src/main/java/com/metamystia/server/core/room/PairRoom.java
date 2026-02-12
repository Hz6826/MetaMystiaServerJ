package com.metamystia.server.core.room;

import com.metamystia.server.core.user.User;
import com.metamystia.server.network.actions.AbstractNetAction;
import lombok.ToString;

@ToString(callSuper = true)
public class PairRoom extends AbstractRoom {
    private static final int CAPACITY = 2;

    public PairRoom() {
        super(null, null, CAPACITY);
        setAutoTransferOwnerOnLeave(true);
        setAutoDisposeWhenEmpty(true);
    }

    @Override
    public void onUserJoin(User user) {

    }

    @Override
    public void onUserLeave(User user) {

    }

    @Override
    public void onOwnerChange(User user) {

    }

    @Override
    public void onPacketReceived(User user, AbstractNetAction action) {
        broadcastToRoomExcept(action, user);
    }

    @Override
    public boolean onRoomDestroy() {
        return false;
    }

    @Override
    public String getName() {
        if(getCustomName() != null) {
            return getCustomName();
        }

        if(getUserCount() == 0) {
            return "PairRoom";
        } else if (getUserCount() == 1) {
            return "PairRoom - " + User.getUserById(getUserIds().getFirst()).orElseThrow().getPeerId();
        } else {
            return "PairRoom - " + User.getUserById(getUserIds().getFirst()).orElseThrow().getPeerId() + " & " + User.getUserById(getUserIds().get(1)).orElseThrow().getPeerId();
        }
    }
}
