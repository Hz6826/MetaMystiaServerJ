package com.metamystia.server.network.actions;

import com.metamystia.server.core.gamedata.Scene;
import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.room.User;
import io.netty.channel.ChannelHandlerContext;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
