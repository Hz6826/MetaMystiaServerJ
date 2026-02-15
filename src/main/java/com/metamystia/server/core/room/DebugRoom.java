package com.metamystia.server.core.room;

import com.metamystia.server.core.user.User;
import com.metamystia.server.network.actions.AbstractNetAction;
import lombok.ToString;

@ToString(callSuper = true)
public class DebugRoom extends AbstractRoom {
    public DebugRoom(String roomName, String roomDescription, int roomCapacity) {
        super(roomName, roomDescription, roomCapacity);
    }

    @Override
    public void onUserJoin(User user) {

    }

    @Override
    public void onUserLeave(User user) {

    }

    @Override
    public void onOwnerChange(User oldOwner, User newOwner) {

    }

    @Override
    public void onPacketReceived(User user, AbstractNetAction action) {

    }

    @Override
    public boolean onRoomDestroy() {
        return false;
    }
}
