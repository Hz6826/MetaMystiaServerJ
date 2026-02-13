package com.metamystia.server.core.room;

import com.metamystia.server.core.user.User;
import com.metamystia.server.network.actions.AbstractNetAction;
import com.metamystia.server.network.actions.HelloAction;
import lombok.ToString;

import java.util.concurrent.locks.Lock;

@ToString(callSuper = true)
public class PairRoom extends AbstractRoom {
    private static final int CAPACITY = 2;

    public PairRoom() {
        super(null, null, CAPACITY);
        setAutoTransferOwnerOnLeave(true);
        setAutoDisposeWhenEmpty(true);
        setBroadcastHelloAction(false);
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
                firstUser.sendAction(HelloAction.getServerDefaultWithUser(user));
            }
        } finally {
            lock.unlock();
        }
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
}
