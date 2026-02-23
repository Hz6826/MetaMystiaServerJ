package com.metamystia.server.core.room;

import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.core.user.User;
import com.metamystia.server.network.actions.*;
import lombok.ToString;

import java.util.concurrent.locks.Lock;

@ToString(callSuper = true)
public class PairRoom extends AbstractRoom {
    private static final int CAPACITY = 2;

    public PairRoom() {
        super(null, null, CAPACITY);
        setAutoTransferOwnerOnLeave(true);
        setAutoDisposeWhenEmpty(true);
        setBroadcastHelloAction(true);
    }

    @Override
    public void onUserJoin(User user) {
        broadcastToRoomExcept("User joined: " + user.getPeerId(), user);

        Lock lock = this.getUserIdsLock().readLock();
        lock.lock();
        try {
            if (getUserCount() == 2) {
                user.sendAction(User.getUserById(this.getUserIds().getFirst()).orElseThrow().getHelloAction());
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onUserLeave(User user) {
        broadcastToRoomExcept("User left: " + user.getPeerId(), user);

        Lock lock = this.getUserIdsLock().readLock();
        lock.lock();
        try {
            if (getUserCount() == 1) {
                User firstUser = User.getUserById(this.getUserIds().getFirst()).orElseThrow();
                if (firstUser.getCurrentGameScene() == Scene.DayScene || firstUser.getCurrentGameScene() == Scene.ResultScene) {
                    firstUser.sendAction(HelloAction.getServerDefaultWithUser(user));
                } else {
                    firstUser.sendMessage("Your partner left the room, so this room will be closed after the night ends.");
                    firstUser.addOneTimeSceneChangeListener(new User.SceneChangeListener() {
                        @Override
                        public void onSceneChange(Scene oldScene, Scene newScene) {
                            if (newScene == Scene.DayScene) {
                                removeUserToDefaultLobby(user);
                            }
                        }
                    });
                }
            }
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void onOwnerChange(User oldOwner, User newOwner) {
        if(oldOwner != null){
            oldOwner.sendAction(new OverrideRoleAction(OverrideRoleAction.Role.CLIENT));
        }
        newOwner.sendAction(new OverrideRoleAction(OverrideRoleAction.Role.HOST));
    }

    @Override
    public void onPacketReceived(User user, AbstractNetAction action) {
        if (action.getType() == ActionType.READY) {
            ReadyAction readyAction = (ReadyAction) action;
            if (readyAction.isAllReady() || isAllReady(readyAction.getReadyType())) {
                clearReadyState(readyAction.getReadyType());
                broadcastToRoom(new ReadyAction(readyAction.getReadyType(), true));
                return;
            }
        } else if (action.getType() == ActionType.OVERRIDE_ROLE) {
            return;  // ignore
        }
        broadcastToRoomExcept(action, user);
    }

    @Override
    public boolean onRoomDestroy() {
        return false;
    }

    @Override
    public String getName() {
        Lock lock = this.getUserIdsLock().readLock();
        lock.lock();
        try {
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
        } finally {
            lock.unlock();
        }
    }

    private boolean isAllReady(ReadyAction.ReadyType readyType) {
        boolean allReady = true;
        Lock lock = this.getUserIdsLock().readLock();
        lock.lock();
        try {
            for (long id : getUserIds()) {
                User user = User.getUserById(id).orElseThrow();
                if (!user.getReadyState().getReadyFor(readyType)) {
                    allReady = false;
                    break;
                }
            }
        } finally {
            lock.unlock();
        }
        return allReady;
    }

    private void clearReadyState(ReadyAction.ReadyType readyType) {
        Lock lock = this.getUserIdsLock().readLock();
        lock.lock();
        try {
            for (long id : getUserIds()) {
                User user = User.getUserById(id).orElseThrow();
                user.getReadyState().setReadyFor(readyType, false);
            }
        } finally {
            lock.unlock();
        }
    }
}
