package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.core.room.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
@NoArgsConstructor
public class HelloAction extends AbstractNetAction{
    public ActionType type = ActionType.HELLO;

    public String peerId = "";
    public String version = "";
    public String gameVersion = "";
    public Scene currentGameScene;

    public List<String> peerActiveDLCLabel;
    public Set<Integer> peerDLCRecipes;
    public Set<Integer> peerDLCCookers;
    public Set<Integer> peerDLCFoods;
    public Set<Integer> peerDLCBeverages;
    public Set<Integer> peerDLCNormalGuests;
    public Set<Integer> peerDLCSpecialGuests;

    public HelloAction(String peerId, String version, String gameVersion, Scene currentGameScene, List<String> peerActiveDLCLabel, Set<Integer> peerDLCRecipes, Set<Integer> peerDLCCookers, Set<Integer> peerDLCFoods, Set<Integer> peerDLCBeverages, Set<Integer> peerDLCNormalGuests, Set<Integer> peerDLCSpecialGuests) {
        this.peerId = peerId;
        this.version = version;
        this.gameVersion = gameVersion;
        this.currentGameScene = currentGameScene;

        this.peerActiveDLCLabel = peerActiveDLCLabel;
        this.peerDLCRecipes = peerDLCRecipes;
        this.peerDLCCookers = peerDLCCookers;
        this.peerDLCFoods = peerDLCFoods;
        this.peerDLCBeverages = peerDLCBeverages;
        this.peerDLCNormalGuests = peerDLCNormalGuests;
        this.peerDLCSpecialGuests = peerDLCSpecialGuests;
    }

    @Override
    public void onReceivedDerived(String channelId) {
        User.createUser(this, channelId);
        log.info("User registered: {}, channel: {}", this.getSenderId(), channelId);
    }
}
