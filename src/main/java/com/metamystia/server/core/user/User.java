package com.metamystia.server.core.user;

import com.metamystia.server.console.command.CommandManager;
import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.core.room.AbstractRoom;
import com.metamystia.server.core.room.RoomManager;
import com.metamystia.server.network.actions.AbstractNetAction;
import com.metamystia.server.network.actions.HelloAction;
import com.metamystia.server.network.actions.MessageAction;
import com.metamystia.server.network.handlers.MainPacketHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Data
@AllArgsConstructor
public class User {
    private static final PermissionLevel DEFAULT_PERMISSION_LEVEL = PermissionLevel.GUEST;

    private static final Map<Long, User> userIdMap= new ConcurrentHashMap<>();

    private long id;
    private String peerId;
    private String version;
    private String gameVersion;
    private Scene currentGameScene;

    private String ip;

    private long latency;

    @ToString.Exclude
    private DLCInfo dlcInfo;
    @ToString.Exclude
    private UserPos userPos;

    private int roomId;

    private String channelId;
    private PermissionLevel permissionLevel;

    @ToString.Exclude
    private ScheduledFuture<?> loginTimeoutTask;


    public static User createUser(HelloAction helloAction, String channelId) {
        User user = User.of(helloAction, channelId);
        if (userIdMap.containsKey(user.getId())) {
            throw new IllegalArgumentException("User with ID " + user.getId() + " already exists");
        }
        userIdMap.put(user.getId(), user);
        return user;
    }

    public static void removeUser(User user) {
        userIdMap.remove(user.getId());
    }

    public static Optional<User> getUserById(long id) {
        return Optional.ofNullable(userIdMap.get(id));
    }

    public static Optional<User> getUserByPeerId(String peerId) {
        return userIdMap.values().stream().filter(user -> user.getPeerId().equals(peerId)).findFirst();
    }

    public static Optional<User> getUserByChannelId(String channelId) {
        return userIdMap.values().stream().filter(user -> user.getChannelId().equals(channelId)).findFirst();
    }

    public static String getUserOrChannelIdString(String channelId) {
        return getUserByChannelId(channelId).map(User::getPeerId).orElse(channelId);
    }

    public static User of(HelloAction helloAction, String channelId) {
        return new User(
                helloAction.getSenderId(),
                helloAction.getPeerId(),
                helloAction.getVersion(),
                helloAction.getGameVersion(),
                helloAction.getCurrentGameScene(),

                MainPacketHandler.getIp(channelId),

                -1,

                new DLCInfo(helloAction.getPeerActiveDLCLabel(),
                        helloAction.getPeerDLCRecipes(),
                        helloAction.getPeerDLCCookers(),
                        helloAction.getPeerDLCFoods(),
                        helloAction.getPeerDLCBeverages(),
                        helloAction.getPeerDLCNormalGuests(),
                        helloAction.getPeerDLCSpecialGuests()),

                new UserPos(),

                RoomManager.NO_ROOM,

                channelId,
                DEFAULT_PERMISSION_LEVEL,
                null
        );
    }

    public void sendAction(AbstractNetAction action) {
        MainPacketHandler.sendAction(this.channelId, action);
    }

    public void sendMessage(String message) {
        this.sendAction(MessageAction.ofServerMessage(message));
    }

    public void closeWithReason(String reason) {
        MainPacketHandler.closeWithReason(this.getChannelId(), reason);
    }

    public boolean hasPermissionAtLeast(PermissionLevel permissionLevel) {
        return this.permissionLevel.isAtLeast(permissionLevel);
    }

    public Optional<AbstractRoom> getRoom() {
        return RoomManager.getRoom(this.roomId);
    }

    public boolean isInRoom() {
        return this.roomId != RoomManager.NO_ROOM;
    }

    public AbstractNetAction getHelloAction() {
        AbstractNetAction helloAction = new HelloAction(
                this.peerId,
                this.version,
                this.gameVersion,
                this.currentGameScene,
                this.dlcInfo.getActiveDLCLabel(),
                this.dlcInfo.getDLCRecipes(),
                this.dlcInfo.getDLCCookers(),
                this.dlcInfo.getDLCFoods(),
                this.dlcInfo.getDLCBeverages(),
                this.dlcInfo.getDLCNormalGuests(),
                this.dlcInfo.getDLCSpecialGuests()
        );
        helloAction.setSenderId(this.id);
        return helloAction;
    }

    public void scheduleLoginTimeout(long delay, TimeUnit unit) {
        cancelLoginTimeout();
        this.loginTimeoutTask = CommandManager.SCHEDULER.schedule(() -> {
            if (User.getUserById(this.id).isPresent() && this.permissionLevel == PermissionLevel.GUEST) {
                closeWithReason("Login timeout");
            }
        }, delay, unit);
    }

    public void cancelLoginTimeout() {
        if (loginTimeoutTask != null && !loginTimeoutTask.isDone()) {
            loginTimeoutTask.cancel(false);
        }
        loginTimeoutTask = null;
    }
}
