package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.Scene;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class SceneTransitAction extends AbstractNetAction{
    public ActionType type = ActionType.SCENE_TRANSIT;

    public Scene scene;

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
