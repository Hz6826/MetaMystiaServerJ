package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.gamedata.Scene;
import com.metamystia.server.core.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class SceneTransitAction extends AbstractNetAction{
    private ActionType type = ActionType.SCENE_TRANSIT;

    private Scene scene;

    public SceneTransitAction() {
        super();
    }

    public SceneTransitAction(Scene scene) {
        super();
        this.scene = scene;
    }

    @Override
    public boolean onReceivedDerived(String channelId) {
        User.getUserByChannelId(channelId).ifPresent(user -> {
            user.setCurrentGameScene(scene);
        });
        return false;
    }
}
