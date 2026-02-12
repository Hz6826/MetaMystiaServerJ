package com.metamystia.server.core.user;

import com.metamystia.server.network.actions.storyaffect.NightSyncAction;
import com.metamystia.server.network.actions.storyaffect.SyncAction;
import lombok.Data;

@Data
public class UserPos {
    private float vx;
    private float vy;
    private float px;
    private float py;
    private boolean isSprinting;
    private String mapLabel;

    private long lastUpdateTs = -1L;  // not yet initialized

    public void updateFromSyncAction(SyncAction syncAction) {
        this.vx = syncAction.getVx();
        this.vy = syncAction.getVy();
        this.px = syncAction.getPx();
        this.py = syncAction.getPy();
        this.isSprinting = syncAction.isSprinting();
        this.mapLabel = syncAction.getMapLabel();

        this.lastUpdateTs = syncAction.getTimestampMs();
    }

    public void updateFromNightSyncAction(NightSyncAction nightSyncAction) {
        this.vx = nightSyncAction.getVx();
        this.vy = nightSyncAction.getVy();
        this.px = nightSyncAction.getPx();
        this.py = nightSyncAction.getPy();

        this.isSprinting = false;
        this.mapLabel = null;

        this.lastUpdateTs = nightSyncAction.getTimestampMs();
    }

    public boolean isInitialized() {
        return this.lastUpdateTs != -1L;
    }
}
