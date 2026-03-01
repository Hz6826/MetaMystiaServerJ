package com.metamystia.server.core.room;

import com.metamystia.server.core.user.User;
import com.metamystia.server.core.user.UserManager;
import com.metamystia.server.network.actions.AbstractNetAction;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Data
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractRoom {
    public static long NO_OWNER = -1L;

    @EqualsAndHashCode.Include
    private int roomId;
    @Getter(AccessLevel.PROTECTED)
    private String customName;
    private String description;
    private int capacity;

    @Setter(AccessLevel.PROTECTED)
    private volatile boolean autoDisposeWhenEmpty = false;

    @Setter(AccessLevel.PROTECTED)
    private volatile boolean autoTransferOwnerOnLeave = false;

    @Setter(AccessLevel.PROTECTED)
    private volatile boolean autoSetFirstUserAsOwner = true;

    @Setter(AccessLevel.PROTECTED)
    private volatile boolean broadcastHelloAction = false;

    @Setter(AccessLevel.PROTECTED)
    private volatile boolean locked = false;

    @Setter(AccessLevel.PRIVATE)
    private List<Long> userIds = new ArrayList<>();
    private ReadWriteLock userIdsLock = new ReentrantReadWriteLock();
    private long ownerId = NO_OWNER;

    private String inviteCode;

    public AbstractRoom(String customName, String description, int roomCapacity) {
        this.roomId = RoomManager.nextRoomId();
        this.customName = customName;
        this.description = description;
        this.capacity = roomCapacity;
    }

    public abstract void onUserJoin(User user);
    public abstract void onUserLeave(User user);
    public abstract void onOwnerChange(User oldOwner, User newOwner);
    public abstract void onPacketReceived(User user, AbstractNetAction action);
    /**
     * Called when the room is destroyed.
     * @return true to cancel the room destroy
     */
    public abstract boolean onRoomDestroy();

    public boolean isVisibleTo(User user) {
        return true;
    }


    // == Helper methods ==

    public final void broadcastToRoom(AbstractNetAction action) {
        userIdsLock.readLock().lock();
        try {
            for (Long userId : userIds) {
                UserManager.getUserById(userId).ifPresent(user -> user.sendAction(action));
            }
        } finally {
            userIdsLock.readLock().unlock();
        }
    }

    public final void broadcastToRoomExcept(AbstractNetAction action, User excluded) {
        userIdsLock.readLock().lock();
        try {
            for (Long userId : userIds) {
                if (userId != excluded.getId()) {
                    UserManager.getUserById(userId).ifPresent(user2 -> user2.sendAction(action));
                }
            }
        } finally {
            userIdsLock.readLock().unlock();
        }
    }

    public final void broadcastToRoom(String message) {
        userIdsLock.readLock().lock();
        try {
            for (Long userId : userIds) {
                UserManager.getUserById(userId).ifPresent(user -> user.sendMessage(message));
            }
        } finally {
            userIdsLock.readLock().unlock();
        }
    }

    public final void broadcastToRoomExcept(String message, User excluded) {
        userIdsLock.readLock().lock();
        try {
            for (Long userId : userIds) {
                if (userId != excluded.getId()) {
                    UserManager.getUserById(userId).ifPresent(user2 -> user2.sendMessage(message));
                }
            }
        } finally {
            userIdsLock.readLock().unlock();
        }
    }

    public final void handlePacket(User user, AbstractNetAction action) {
        onPacketReceived(user, action);
    }

    public final void changeOwner(User newOwner) {
        userIdsLock.writeLock().lock();
        try {
            User oldOwner = UserManager.getUserById(ownerId).orElse(null);
            this.ownerId = newOwner.getId();
            onOwnerChange(oldOwner, newOwner);
        } finally {
            userIdsLock.writeLock().unlock();
        }
    }

    public final void addUser(User user) {
        userIdsLock.writeLock().lock();
        try {
            if (isFull()) {
                throw new IllegalStateException("Cannot add " + user + ", room " + roomId + " is full");
            }
            if (isUserInRoom(user)) {
                throw new IllegalStateException("User " + user + " is already in room " + roomId);
            }
            if (locked) {
                throw new IllegalStateException("Cannot add " + user + ", room " + roomId + " is locked");
            }

            if (autoSetFirstUserAsOwner && userIds.isEmpty() && isDisowned()) {
                changeOwner(user);
            }

            this.userIds.add(user.getId());
            user.setRoomId(this.roomId);

            if (broadcastHelloAction) {
                broadcastToRoomExcept(user.getHelloAction(), user);
            }

            onUserJoin(user);
        } finally {
            userIdsLock.writeLock().unlock();
        }
    }

    public final void removeUser(User user, int toRoom) {
        userIdsLock.writeLock().lock();
        try {
            if(!this.userIds.remove(user.getId())) {
                throw new IllegalStateException("Cannot remove " + user + ", user is not in room " + roomId);
            }

            if (autoTransferOwnerOnLeave && user.getId() == ownerId) {
                userIds.stream().findFirst().flatMap(UserManager::getUserById).ifPresentOrElse(this::changeOwner, () -> this.ownerId = NO_OWNER);
            }

            onUserLeave(user);
            if (toRoom == RoomManager.NO_ROOM) {
                user.setRoomId(RoomManager.NO_ROOM);
            } else {
                RoomManager.getRoom(toRoom).orElseThrow().addUser(user);
            }

            if (autoDisposeWhenEmpty && userIds.isEmpty()) {
                RoomManager.removeRoom(this, false, toRoom);
            }
        } finally {
            userIdsLock.writeLock().unlock();
        }
    }

    public final void removeUserToDefaultLobby(User user) {
        removeUser(user, RoomManager.DEFAULT_LOBBY_ID);
    }

    @Deprecated
    public final void removeUser(User user) {
        removeUser(user, RoomManager.NO_ROOM);
    }

    public final boolean isUserInRoom(User user) {
        userIdsLock.readLock().lock();
        try {
            return userIds.contains(user.getId());
        } finally {
            userIdsLock.readLock().unlock();
        }
    }

    public String getName() {
        return customName;
    }

    public int getUserCount() {
        return userIds.size();
    }

    public boolean isFull() {
        return getUserCount() >= capacity;
    }

    public boolean isDisowned() {
        return ownerId == NO_OWNER;
    }

    public boolean isOwnedBy(User user) {
        return user.getId() == ownerId;
    }

    public boolean isInviteCodeGenerated() {
        return inviteCode != null;
    }

    public String getInviteCode() {
        if(inviteCode == null) {
            String pendingInviteCode = RoomManager.assignInviteCode(this);
            if(RoomManager.getRoomByInviteCode(pendingInviteCode).isPresent()) {
                inviteCode = pendingInviteCode;
            } else {
                throw new IllegalStateException("Failed to assign invite code for room " + roomId);
            }
        }
        return inviteCode;
    }

    public String renewInviteCode() {
        RoomManager.removeInviteCode(getInviteCode());
        inviteCode = null;
        return getInviteCode();
    }

    public String getUserNames() {
        return getUserNames(-1);
    }

    public String getUserNames(int limit) {
        userIdsLock.readLock().lock();
        try {
            if (limit == 0) {
                return "";
            }
            StringBuilder sb = new StringBuilder();
            int count = 0;
            boolean hasMore = false;
            for (Long userId : userIds) {
                Optional<User> userOpt = UserManager.getUserById(userId);
                if (userOpt.isPresent()) {
                    if (limit > 0 && count >= limit) {
                        hasMore = true;
                        break;
                    }
                    User user = userOpt.get();
                    if (count > 0) {
                        sb.append(", ");
                    }
                    sb.append(user.getPeerId());
                    count++;
                }
            }
            if (hasMore) {
                sb.append("...");
            }
            return sb.toString();
        } finally {
            userIdsLock.readLock().unlock();
        }
    }

    public String getInfo() {
        return "Name: " + getName() + "\n" +
                "Description: " + description + "\n" +
                "Capacity: " + capacity + "\n" +
                "Owner: " + (isDisowned() ? "None" : UserManager.getUserById(ownerId).map(User::getPeerId).orElse("Unknown")) + "\n" +
                "Users(" + getUserCount() + "): " + getUserNames(3) + "\n" +
                "Invite Code: " + (isInviteCodeGenerated() ? getInviteCode() : "None");
    }
}
