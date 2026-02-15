package com.metamystia.server.console.command;

import com.metamystia.server.config.AccessControlManager;
import com.metamystia.server.config.ConfigManager;
import com.metamystia.server.core.user.PermissionLevel;
import com.metamystia.server.core.user.User;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import static com.metamystia.server.console.command.CommandManager.argument;
import static com.metamystia.server.console.command.CommandManager.literal;

public class AuthCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("auth").requires(commandSource -> !ConfigManager.getConfig().isDisableAuth())
                        .then(literal("login")
                                .executes(AuthCommands::loginCommandWithNoArgs)
                                .then(argument("password", StringArgumentType.string())
                                        .executes(AuthCommands::loginCommand)))
                        .then(literal("logout").executes(AuthCommands::logoutCommand))
        );
    }


    private static int loginCommandWithNoArgs(CommandContext<CommandSource> context) {
        User user = context.getSource().user();
        user.sendMessage("Please enter your password!");
        return 1;
    }

    private static int loginCommand(CommandContext<CommandSource> context) {  // TODO: Implement login
        User user = context.getSource().user();
        user.cancelLoginTimeout();
        if (AccessControlManager.isIpOp(user.getIp()) || AccessControlManager.isIdOp(context.getSource().user().getId())) {
            user.setPermissionLevel(PermissionLevel.ADMIN);
            user.sendMessage("Welcome back, Admin!");
        } else {
            user.sendMessage("Logged in.");
            user.setPermissionLevel(PermissionLevel.USER);
        }
        return 1;
    }

    private static int logoutCommand(CommandContext<CommandSource> context) {
        context.getSource().user().closeWithReason("Logged out.");
        return 1;
    }
}
