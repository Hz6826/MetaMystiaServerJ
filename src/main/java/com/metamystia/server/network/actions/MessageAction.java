package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.config.ConfigManager;
import com.metamystia.server.console.command.CommandManager;
import com.metamystia.server.console.command.CommandSource;
import com.metamystia.server.core.user.User;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Data
@MemoryPackable
public class MessageAction extends AbstractNetAction {
    private ActionType type = ActionType.MESSAGE;

    private static final int MAX_MESSAGE_LEN = 1024;

    private String message;

    public MessageAction() {
        super();
    }

    public MessageAction(String message) {
        super();
        this.message = message;
    }

    public static MessageAction ofServerMessage(String message) {
        MessageAction messageAction = new MessageAction(message);
        messageAction.addDecorator("[" + ConfigManager.getConfig().getServerName() + "] ");
        return messageAction;
    }

    @Override
    public boolean onReceivedDerived(String channelId) {
        if (message.startsWith(CommandManager.COMMAND_PREFIX)) {
            CommandManager.parseAsync(message, new CommandSource(User.getUserById(getSenderId()).orElseThrow(), getTimestampMs()));
            return true;
        }
        this.message = message.substring(0, Math.min(message.length(), MAX_MESSAGE_LEN));
        return false;

    }

    public void addDecorator(String decorator) {
        this.message = decorator + this.message;
    }
}
