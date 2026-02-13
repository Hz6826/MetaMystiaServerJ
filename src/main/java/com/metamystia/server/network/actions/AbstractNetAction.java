package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.user.User;
import com.metamystia.server.util.LogLevel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
    @ToString.Exclude
    private ActionType type = null;
    private long timestampMs;
    private long senderId;

    public static final long SERVER_SENDER_ID = 0L;

    protected AbstractNetAction() {
        this.timestampMs = Instant.now().toEpochMilli();
        this.senderId = SERVER_SENDER_ID;
    }

    /**
     *
     * @return true to cancel continuous processing (e.g. room)
     */
    public boolean onReceivedDerived(String channelId) {
        return false;
    }

    public boolean onReceived(String channelId) {
        logActionReceived(channelId);
        return onReceivedDerived(channelId);
    }

    protected void logActionReceived(String channelId) {
        LogLevel logLevel = this.getLogLevel();
        if (logLevel == LogLevel.DEBUG) {
            log.debug("Received [{}] from {} - {}", this.getType(), User.getUserOrChannelIdString(channelId), this);
        } else if (logLevel == LogLevel.INFO) {
            log.info("Received [{}] from {} - {}", this.getType(), User.getUserOrChannelIdString(channelId), this);
        } else if (logLevel == LogLevel.WARN) {
            log.warn("Received [{}] from {} - {}", this.getType(), User.getUserOrChannelIdString(channelId), this);
        } else if (logLevel == LogLevel.ERROR) {
            log.error("Received [{}] from {} - {}", this.getType(), User.getUserOrChannelIdString(channelId), this);
        }
    }

    public LogLevel getLogLevel() {
        return LogLevel.INFO;
    }

    public boolean isServerAction() {
        return senderId == SERVER_SENDER_ID;
    }
}
