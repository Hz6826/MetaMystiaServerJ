package com.metamystia.server.core.packet;

import com.metamystia.server.core.plugin.PluginManager;
import com.metamystia.server.core.room.AbstractRoom;
import com.metamystia.server.core.room.RoomManager;
import com.metamystia.server.core.user.User;
import com.metamystia.server.core.user.UserManager;
import com.metamystia.server.network.actions.AbstractNetAction;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
public class PacketDispatcher {
    public static void dispatch(String channelId, AbstractNetAction action) {
        boolean cancelled = action.onReceived(channelId);
        if (cancelled) return;
        Optional<User> optUser = UserManager.getUserByChannelId(channelId);
        if (optUser.isPresent()) {
            User user = optUser.get();
            if (user.getId() != action.getSenderId()) {
                log.warn("Received action {} from user {} with wrong senderId {}, ignoring...", action, user, action.getSenderId());
                return;
            }
            if (!action.isServerAction() && !PluginManager.getAuthProvider().allowAction(user, action)) return;
            Optional<AbstractRoom> optRoom = RoomManager.getRoomByUser(user);
            optRoom.ifPresent(abstractRoom -> abstractRoom.handlePacket(user, action));
        }
    }
}
