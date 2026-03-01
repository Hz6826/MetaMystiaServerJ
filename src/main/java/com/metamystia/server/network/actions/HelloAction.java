package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.config.ConfigManager;
import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.core.plugin.PluginManager;
import com.metamystia.server.core.user.User;
import com.metamystia.server.core.user.UserManager;
import com.metamystia.server.network.handlers.MainPacketHandler;
import com.metamystia.server.util.ManifestManager;
import com.metamystia.server.util.VersionValidators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;

@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class HelloAction extends AbstractNetAction{
    private ActionType type = ActionType.HELLO;

    private String peerId = "";
    private String version = "";
    private String gameVersion = "";
    private Scene currentGameScene;

    private List<String> peerActiveDLCLabel;
    @ToString.Exclude private Set<Integer> peerDLCRecipes;
    @ToString.Exclude private Set<Integer> peerDLCCookers;
    @ToString.Exclude private Set<Integer> peerDLCFoods;
    @ToString.Exclude private Set<Integer> peerDLCBeverages;
    @ToString.Exclude private Set<Integer> peerDLCNormalGuests;
    @ToString.Exclude private Set<Integer> peerDLCSpecialGuests;

    // ResourceEx (rEx) resource sets
    private Set<Integer> peerRExRecipes;
    private Set<Integer> peerRExFoods;
    private Set<Integer> peerRExBeverages;
    private Set<Integer> peerRExSpecialGuests;

    public HelloAction() {
        super();
    }

    public HelloAction(String peerId,
                       String version,
                       String gameVersion,
                       Scene currentGameScene,
                       List<String> peerActiveDLCLabel,
                       Set<Integer> peerDLCRecipes,
                       Set<Integer> peerDLCCookers,
                       Set<Integer> peerDLCFoods,
                       Set<Integer> peerDLCBeverages,
                       Set<Integer> peerDLCNormalGuests,
                       Set<Integer> peerDLCSpecialGuests,
                       Set<Integer> peerRExRecipes,
                       Set<Integer> peerRExFoods,
                       Set<Integer> peerRExBeverages,
                       Set<Integer> peerRExSpecialGuests) {
        super();

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

        this.peerRExRecipes = peerRExRecipes;
        this.peerRExFoods = peerRExFoods;
        this.peerRExBeverages = peerRExBeverages;
        this.peerRExSpecialGuests = peerRExSpecialGuests;
    }

    @Override
    public boolean onReceivedDerived(String channelId) {
        User user = UserManager.createUser(this, channelId);  // to avoid no user found in MainPacketHandler.channelInactive method
        if (!VersionValidators.isMetaMystiaVersionValid(this.getVersion())) {
            MainPacketHandler.closeWithReason(channelId, "Invalid version! Supported version(s): " + ManifestManager.getManifest().metaMystiaVersion());
            return true;
        }

        if (user.getRoom().isEmpty()) {
            user.sendAction(getServerDefaultWithHelloAction(this));
            user.sendMessage("Welcome to " + ConfigManager.getConfig().getServerName() + "! Version: " + ManifestManager.getManifest().version());
            PluginManager.getAuthProvider().onUserJoin(user);
        }

        log.info("User registered: {}, channel: {}", this.getSenderId(), channelId);
        return false;
    }

    public static HelloAction getServerDefaultWithUser(User user) {
        return new HelloAction(
                ConfigManager.getConfig().getServerName(),
                user.getVersion(),
                user.getGameVersion(),
                user.getCurrentGameScene(),

                user.getDlcInfo().getActiveDLCLabel(),
                user.getDlcInfo().getDLCRecipes(),
                user.getDlcInfo().getDLCCookers(),
                user.getDlcInfo().getDLCFoods(),
                user.getDlcInfo().getDLCBeverages(),
                user.getDlcInfo().getDLCNormalGuests(),
                user.getDlcInfo().getDLCSpecialGuests(),

                user.getRExInfo().getRExRecipes(),
                user.getRExInfo().getRExFoods(),
                user.getRExInfo().getRExBeverages(),
                user.getRExInfo().getRExSpecialGuests()
        );
    }

    public static HelloAction getServerDefaultWithHelloAction(HelloAction helloAction) {
        return new HelloAction(
                ConfigManager.getConfig().getServerName(),
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

                helloAction.getPeerRExRecipes(),
                helloAction.getPeerRExFoods(),
                helloAction.getPeerRExBeverages(),
                helloAction.getPeerRExSpecialGuests()
        );
    }
}
