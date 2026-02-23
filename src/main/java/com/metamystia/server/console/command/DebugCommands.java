package com.metamystia.server.console.command;

import com.metamystia.server.config.ConfigManager;
import com.metamystia.server.console.command.arguments.PermissionLevelArgumentType;
import com.metamystia.server.core.user.PermissionLevel;
import com.metamystia.server.network.GameServer;
import com.metamystia.server.network.actions.OverrideRoleAction;
import com.metamystia.server.network.actions.ReadyAction;
import com.metamystia.server.network.actions.SelectAction;
import com.metamystia.server.util.DebugUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import lombok.extern.slf4j.Slf4j;

import static com.metamystia.server.console.command.CommandManager.argument;
import static com.metamystia.server.console.command.CommandManager.literal;

@Slf4j
public class DebugCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("debug").requires(commandSource -> ConfigManager.getConfig().isDebug() || commandSource.user().hasPermissionAtLeast(PermissionLevel.ADMIN))
                        .then(literal("stop").executes(DebugCommands::stopCommand))
                        .then(literal("sendReady").executes(DebugCommands::sendReadyCommand))
                        .then(literal("sendPrepReady").executes(DebugCommands::sendPrepReadyCommand))
                        .then(literal("sendSelect")
                                .then(argument("mapLabel", StringArgumentType.word())
                                        .then(argument("mapLevel", IntegerArgumentType.integer(1, 3))
                                                .executes(DebugCommands::sendSelectCommand))))
                        .then(literal("closeWithReason")
                                .then(argument("reason", StringArgumentType.greedyString()).executes(DebugCommands::closeWithReasonCommand)))
                        .then(literal("switchEcho").executes(DebugCommands::switchEchoCommand))
                        .then(literal("setPermissionLevel")
                                .then(argument("permissionLevel", PermissionLevelArgumentType.permissionLevel())
                                        .executes(DebugCommands::setPermissionLevelCommand)))
                        .then(literal("getPermissionLevel")
                                .executes(DebugCommands::getPermissionLevelCommand))
                        .then(literal("writeConfig")
                                .executes(DebugCommands::writeConfigCommand))
                        .then(literal("hostRole")
                                .then(literal("grant")
                                        .executes(DebugCommands::grantHostRoleCommand))
                                .then(literal("revoke")
                                        .executes(DebugCommands::revokeHostRoleCommand)))
        );
    }

    private static int stopCommand(CommandContext<CommandSource> context) {
        GameServer.getInstance().stop();
        return 1;
    }

    private static int sendReadyCommand(CommandContext<CommandSource> context) {
        context.getSource().user().sendAction(new ReadyAction(ReadyAction.ReadyType.DayOver, true));
        return 1;
    }

    private static int sendPrepReadyCommand(CommandContext<CommandSource> context) {
        context.getSource().user().sendAction(new ReadyAction(ReadyAction.ReadyType.PrepOver, true));
        return 1;
    }

    private static int sendSelectCommand(CommandContext<CommandSource> context) {
        String mapLabel = StringArgumentType.getString(context, "mapLabel");
        int mapLevel = IntegerArgumentType.getInteger(context, "mapLevel");
        context.getSource().user().sendAction(new SelectAction(mapLabel, mapLevel));
        return 1;
    }

    private static int closeWithReasonCommand(CommandContext<CommandSource> context) {
        String reason = StringArgumentType.getString(context, "reason");
        context.getSource().user().closeWithReason(reason);
        return 1;
    }

    private static int switchEchoCommand(CommandContext<CommandSource> context) {
        DebugUtils.echo = !DebugUtils.echo;
        context.getSource().user().sendMessage("Echo mode switched to: " + DebugUtils.echo);
        return 1;
    }

    private static int setPermissionLevelCommand(CommandContext<CommandSource> context) {
        PermissionLevel permissionLevel = PermissionLevelArgumentType.getPermissionLevel(context, "permissionLevel");
        context.getSource().user().setPermissionLevel(permissionLevel);
        context.getSource().user().sendMessage("Permission level set to: " + permissionLevel);
        return 1;
    }

    private static int getPermissionLevelCommand(CommandContext<CommandSource> context) {
        PermissionLevel permissionLevel = context.getSource().user().getPermissionLevel();
        context.getSource().user().sendMessage("Permission level: " + permissionLevel);
        return 1;
    }

    private static int writeConfigCommand(CommandContext<CommandSource> context) {
        ConfigManager.saveConfigToFile();
        context.getSource().user().sendMessage("Config has been written to disk.");
        return 1;
    }

    private static int grantHostRoleCommand(CommandContext<CommandSource> context) {
        context.getSource().user().sendAction(new OverrideRoleAction(OverrideRoleAction.Role.HOST));
        context.getSource().user().sendMessage("Host role is granted.");
        return 1;
    }

    private static int revokeHostRoleCommand(CommandContext<CommandSource> context) {
        context.getSource().user().sendAction(new OverrideRoleAction(OverrideRoleAction.Role.CLIENT));
        context.getSource().user().sendMessage("Client role is granted.");
        return 1;
    }
}
