package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
@Slf4j
public class PingAction extends AbstractNetAction{
    public ActionType type = ActionType.PING;

    public int id;

    @Override
    protected void logActionReceived() {
        log.debug("Received [{}] - {}", this.getType(), this);
    }

    @Override
    public void onReceivedDerived(String channelId) {

    }
}
