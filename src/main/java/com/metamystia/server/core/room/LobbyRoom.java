package com.metamystia.server.core.room;

import com.metamystia.server.core.user.User;
import com.metamystia.server.network.actions.AbstractNetAction;
import com.metamystia.server.network.actions.ActionType;
import com.metamystia.server.network.actions.HelloAction;
import com.metamystia.server.network.actions.MessageAction;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@ToString(callSuper = true)
@Slf4j
public class LobbyRoom extends AbstractRoom {
    public static final String ROOM_NAME = "Lobby";
    public static final String ROOM_DESCRIPTION = "The lobby.";
    public static final int ROOM_CAPACITY = 100;

    public LobbyRoom() {
        super(ROOM_NAME, ROOM_DESCRIPTION, ROOM_CAPACITY);
        setAutoSetFirstUserAsOwner(false);
    }

    @Override
    public void onUserJoin(User user) {
        broadcastToRoomExcept("User joined lobby: " + user.getPeerId(), user);

        user.sendAction(HelloAction.getServerDefaultWithUser(user));

        log.info("User joined lobby: {}", user.getPeerId());
    }

    @Override
    public void onUserLeave(User user) {
        broadcastToRoomExcept("User left lobby: " + user.getPeerId(), user);
        log.info("User left lobby: {}", user.getPeerId());
    }

    @Override
    public void onOwnerChange(User user) {
        throw new UnsupportedOperationException("Cannot change owner of lobby");
    }

    @Override
    public void onPacketReceived(User user, AbstractNetAction action) {
        if (action.getType() == ActionType.MESSAGE) {
            MessageAction messageAction = (MessageAction) action;
            messageAction.addDecorator("<" + User.getUserOrChannelIdString(user.getChannelId()) + "> ");
            broadcastToRoomExcept(messageAction, user);
        }
    }

    @Override
    public boolean onRoomDestroy() {
        return false;
    }
}
