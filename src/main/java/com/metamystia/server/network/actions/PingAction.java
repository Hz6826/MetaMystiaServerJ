package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.core.user.User;
import com.metamystia.server.util.LogLevel;
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
    private ActionType type = ActionType.PING;

    private int id;

    public PingAction() {
        super();
    }

    public PingAction(int id) {
        super();
        this.id = id;
    }

    @Override
    public LogLevel getLogLevel() {
        return LogLevel.DEBUG;
    }

    @Override
    public boolean onReceivedDerived(String channelId) {
        User.getUserByChannelId(channelId).ifPresent(user -> {
            user.setLatency(System.currentTimeMillis() - this.getTimestampMs());
            user.sendAction(new PongAction(this.id));
        });
        return true;
    }
}
