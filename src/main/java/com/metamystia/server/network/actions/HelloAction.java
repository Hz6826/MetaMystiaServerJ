package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.core.room.User;
import com.metamystia.server.network.handlers.MainPacketHandler;
import com.metamystia.server.util.ManifestManager;
import com.metamystia.server.util.VersionValidators;
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
    private ActionType type = ActionType.HELLO;

    private String peerId = "";
    private String version = "";
    private String gameVersion = "";
    private Scene currentGameScene;

    private List<String> peerActiveDLCLabel;
    private Set<Integer> peerDLCRecipes;
    private Set<Integer> peerDLCCookers;
    private Set<Integer> peerDLCFoods;
    private Set<Integer> peerDLCBeverages;
    private Set<Integer> peerDLCNormalGuests;
    private Set<Integer> peerDLCSpecialGuests;

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
        User.createUser(this, channelId);  // to avoid no user found in MainPacketHandler.channelInactive method
        if (!VersionValidators.isMetaMystiaVersionValid(this.getVersion())) {
            MainPacketHandler.closeWithReason(channelId, "Invalid version! Supported version(s): " + ManifestManager.getManifest().metaMystiaVersion());
            return;
        }

        log.info("User registered: {}, channel: {}", this.getSenderId(), channelId);
    }
}
