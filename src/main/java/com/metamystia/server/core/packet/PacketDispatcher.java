package com.metamystia.server.core.packet;

import com.metamystia.server.core.room.AbstractRoom;
import com.metamystia.server.core.room.RoomManager;
import com.metamystia.server.core.user.User;
import com.metamystia.server.network.actions.AbstractNetAction;

import java.util.Optional;


public class PacketDispatcher {
    public static void dispatch(String channelId, AbstractNetAction action) {
        boolean cancelled = action.onReceived(channelId);
        if (cancelled) return;
        Optional<User> optUser = User.getUserByChannelId(channelId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            Optional<AbstractRoom> optRoom = RoomManager.getRoomByUser(user);
            optRoom.ifPresent(abstractRoom -> abstractRoom.onPacketReceived(user, action));
        }
    }
}
