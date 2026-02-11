package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.console.command.CommandManager;
import com.metamystia.server.console.command.CommandSource;
import com.metamystia.server.core.room.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
@NoArgsConstructor
public class MessageAction extends AbstractNetAction {
    private ActionType type = ActionType.MESSAGE;

    private static final int MAX_MESSAGE_LEN = 1024;

    private String message;

    public MessageAction(String message) {
        super();
        this.message = message;
    }

    @Override
    public void onReceivedDerived(String channelId) {
        if (message.startsWith(CommandManager.COMMAND_PREFIX)) {
            CommandManager.parse(message, new CommandSource(User.getUserById(getSenderId()).orElseThrow(), getTimestampMs()));
        }
    }
}
