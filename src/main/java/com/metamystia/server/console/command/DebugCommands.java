package com.metamystia.server.console.command;

import com.metamystia.server.network.GameServer;
import com.metamystia.server.network.actions.ReadyAction;
import com.metamystia.server.network.actions.SelectAction;
import com.metamystia.server.util.DebugUtils;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.tree.CommandNode;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.Map;

@Slf4j
public class DebugCommands {
    public static int helpCommandNoArgs(CommandContext<CommandSource> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAvailable commands:");

        String[] allUsage = CommandManager.dispatcher.getAllUsage(CommandManager.dispatcher.getRoot(), context.getSource(), true);
        for (String usage : allUsage) {
            sb.append("\n").append(CommandManager.COMMAND_PREFIX).append(usage);
        }

        context.getSource().user().sendMessage(sb.toString());
        return 1;
    }

    public static int helpCommand(CommandContext<CommandSource> context) {
        String command = StringArgumentType.getString(context, "command");

        context.getSource().user().sendMessage("Not implemented yet!");  // TODO
        return 1;
    }

    public static int stopCommand(CommandContext<CommandSource> context) {
        GameServer.getInstance().stop();
        return 1;
    }

    public static int sendReadyCommand(CommandContext<CommandSource> context) {
        context.getSource().user().sendAction(new ReadyAction(ReadyAction.ReadyType.DayOver, true));
        return 1;
    }

    public static int sendSelectCommand(CommandContext<CommandSource> context) {
        String mapLabel = StringArgumentType.getString(context, "mapLabel");
        int mapLevel = IntegerArgumentType.getInteger(context, "mapLevel");
        context.getSource().user().sendAction(new SelectAction(mapLabel, mapLevel));
        return 1;
    }

    public static int closeWithReasonCommand(CommandContext<CommandSource> context) {
        String reason = StringArgumentType.getString(context, "reason");
        context.getSource().user().closeWithReason(reason);
        return 1;
    }

    public static int switchEchoCommand(CommandContext<CommandSource> context) {
        DebugUtils.echo = !DebugUtils.echo;
        context.getSource().user().sendMessage("Echo mode switched to: " + DebugUtils.echo);
        return 1;
    }
}
