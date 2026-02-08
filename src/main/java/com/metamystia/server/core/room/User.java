package com.metamystia.server.core.room;

import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.network.actions.AbstractNetAction;
import com.metamystia.server.network.actions.HelloAction;
import com.metamystia.server.network.actions.MessageAction;
import com.metamystia.server.network.handlers.MainPacketHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Data
@AllArgsConstructor
public class User {
    private static final Map<Long, User> userIdMap= new ConcurrentHashMap<>();

    private long id;
    private String peerId;
    private String version;
    private String gameVersion;
    private Scene currentGameScene;

    @ToString.Exclude
    public List<String> peerActiveDLCLabel;
    @ToString.Exclude
    public Set<Integer> peerDLCRecipes;
    @ToString.Exclude
    public Set<Integer> peerDLCCookers;
    @ToString.Exclude
    public Set<Integer> peerDLCFoods;
    @ToString.Exclude
    public Set<Integer> peerDLCBeverages;
    @ToString.Exclude
    public Set<Integer> peerDLCNormalGuests;
    @ToString.Exclude
    public Set<Integer> peerDLCSpecialGuests;

    private String channelId;


    public static void createUser(HelloAction helloAction, String channelId) {
        User user = User.of(helloAction, channelId);
        userIdMap.put(user.getId(), user);
    }

    public static User removeUser(long id) {
        return userIdMap.remove(id);
    }

    public static User getUserById(long id) {
        return userIdMap.get(id);
    }

    public static User getUserByPeerId(String peerId) {
        return userIdMap.values().stream().filter(user -> user.getPeerId().equals(peerId)).findFirst().orElseThrow();
    }

    public static User getUserByChannelId(String channelId) {
        return userIdMap.values().stream().filter(user -> user.getChannelId().equals(channelId)).findFirst().orElseThrow();
    }

    public static User of(HelloAction helloAction, String channelId) {
        return new User(
                helloAction.getSenderId(),
                helloAction.getPeerId(),
                helloAction.getVersion(),
                helloAction.getGameVersion(),
                helloAction.getCurrentGameScene(),

                helloAction.getPeerActiveDLCLabel(),
                helloAction.getPeerDLCRecipes(),
                helloAction.getPeerDLCCookers(),
                helloAction.getPeerDLCFoods(),
                helloAction.getPeerDLCBeverages(),
                helloAction.getPeerDLCNormalGuests(),
                helloAction.getPeerDLCSpecialGuests(),

                channelId
        );
    }

    public void sendAction(AbstractNetAction action) {
        MainPacketHandler.sendAction(this.channelId, action);
    }

    public void sendMessage(String message) {
        this.sendAction(new MessageAction(message));
    }
}
