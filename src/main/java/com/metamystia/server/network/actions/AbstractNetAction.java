package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;

/**
 * The base class for all network actions.
 */
@Data
@EqualsAndHashCode
@Slf4j(topic = "Network")
@MemoryPackable
public abstract class AbstractNetAction {
    @Getter @Setter @ToString.Exclude
    public ActionType type = null;
    @Getter @Setter
    private long timestampMs;
    @Getter @Setter
    private long senderId;

    protected AbstractNetAction() {
        this.timestampMs = Instant.now().toEpochMilli();
        this.senderId = 0L;
    }

    protected boolean skipReceiveOnStory() {
        return false;
    }

    protected boolean skipSendOnStory() {
        return false;
    }

    public abstract void onReceivedDerived(String channelId);
    public void onReceived(String channelId) {
        logActionReceived();
        onReceivedDerived(channelId);
    }

    protected void logActionReceived() {
        log.info("Received [{}] - {}", this.getType(), this);
    }
}
