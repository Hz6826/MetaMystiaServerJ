package com.metamystia.server.core.user;

import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.core.room.AbstractRoom;
import com.metamystia.server.core.room.RoomManager;
import com.metamystia.server.network.actions.AbstractNetAction;
import com.metamystia.server.network.actions.HelloAction;
import com.metamystia.server.network.actions.MessageAction;
import com.metamystia.server.network.actions.OverrideRoleAction;
import com.metamystia.server.network.handlers.MainPacketHandler;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@Data
@AllArgsConstructor
public class User {
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
    private RExInfo rExInfo;
    @ToString.Exclude
    private UserPos userPos;

    private int roomId;

    private String channelId;

    private ReadyState readyState;

    private OverrideRoleAction.Role overrideRole;

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

                new RExInfo(helloAction.getPeerRExRecipes(),
                        helloAction.getPeerRExFoods(),
                        helloAction.getPeerRExBeverages(),
                        helloAction.getPeerRExSpecialGuests()),

                new UserPos(),

                RoomManager.NO_ROOM,

                channelId,
                new ReadyState(),
                OverrideRoleAction.Role.NULL
        );
    }

    public void sendAction(AbstractNetAction action) {
        MainPacketHandler.sendAction(this.channelId, action);
    }

    public void sendMessage(String message) {
        this.sendAction(MessageAction.ofServerMessage(message));
    }

    public void sendOverrideRoleAction(OverrideRoleAction.Role role) {
        if  (this.overrideRole == role) return;
        this.sendAction(new OverrideRoleAction(role));
        this.overrideRole = role;
    }

    public void closeWithReason(String reason) {
        MainPacketHandler.closeWithReason(this.getChannelId(), reason);
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
                this.dlcInfo.getDLCSpecialGuests(),
                this.rExInfo.getRExRecipes(),
                this.rExInfo.getRExFoods(),
                this.rExInfo.getRExBeverages(),
                this.rExInfo.getRExSpecialGuests()
        );
        helloAction.setSenderId(this.id);
        return helloAction;
    }
}
