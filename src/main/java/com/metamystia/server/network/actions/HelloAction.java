package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.config.AccessControlManager;
import com.metamystia.server.config.ConfigManager;
import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.core.room.RoomManager;
import com.metamystia.server.core.user.PermissionLevel;
import com.metamystia.server.core.user.User;
import com.metamystia.server.network.handlers.MainPacketHandler;
import com.metamystia.server.util.ManifestManager;
import com.metamystia.server.util.VersionValidators;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    private Set<Integer> peerDLCRecipes;
    private Set<Integer> peerDLCCookers;
    private Set<Integer> peerDLCFoods;
    private Set<Integer> peerDLCBeverages;
    private Set<Integer> peerDLCNormalGuests;
    private Set<Integer> peerDLCSpecialGuests;

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
        User user = User.createUser(this, channelId);  // to avoid no user found in MainPacketHandler.channelInactive method
        if (!VersionValidators.isMetaMystiaVersionValid(this.getVersion())) {
            MainPacketHandler.closeWithReason(channelId, "Invalid version! Supported version(s): " + ManifestManager.getManifest().metaMystiaVersion());
            return true;
        }

        if (user.getRoom().isEmpty()) {
            user.sendAction(getServerDefaultWithHelloAction(this));
            user.sendMessage("Welcome to " + ConfigManager.getConfig().getServerName() + "! Version: " + ManifestManager.getManifest().version());
            RoomManager.getLobbyRoom().addUser(user);

            if (ConfigManager.getConfig().isDisableAuth()) {
                if (AccessControlManager.isIpOp(user.getIp()) || AccessControlManager.isIdOp(user.getId())) {
                    user.setPermissionLevel(PermissionLevel.ADMIN);
                    user.sendMessage("Welcome back, Admin!");
                } else {
                    user.setPermissionLevel(PermissionLevel.USER);
                }
            } else {
                user.setPermissionLevel(PermissionLevel.GUEST);
                user.sendMessage("You are not logged in! Use: !auth login <password> to log in.");
                int timeoutSeconds = ConfigManager.getConfig().getLoginTimeoutSeconds();
                user.scheduleLoginTimeout(timeoutSeconds, TimeUnit.SECONDS);
            }
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
