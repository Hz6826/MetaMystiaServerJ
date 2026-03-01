package com.metamystia.server.network.actions;

import com.hz6826.memorypack.annotation.MemoryPackable;
import com.metamystia.server.api.command.CommandManager;
import com.metamystia.server.api.command.CommandSource;
import com.metamystia.server.core.config.ConfigManager;
import com.metamystia.server.core.user.UserManager;
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

    @ToString.Exclude
    private String message;

    @ToString.Include(name = "message")
    private String getMessageForToString() {
        if (message == null) return null;
        String escaped = message
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
        return "\"" + escaped + "\"";
    }

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
            CommandManager.parseAsync(message, new CommandSource(UserManager.getUserById(getSenderId()).orElseThrow(), getTimestampMs()));
            return true;
        }
        this.message = message.substring(0, Math.min(message.length(), MAX_MESSAGE_LEN));
        return false;

    }

    public void addDecorator(String decorator) {
        this.message = decorator + this.message;
    }
}
