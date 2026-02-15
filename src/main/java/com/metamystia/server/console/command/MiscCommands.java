package com.metamystia.server.console.command;

import com.metamystia.server.core.user.PermissionLevel;
import com.metamystia.server.util.ManifestManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import static com.metamystia.server.console.command.CommandManager.argument;
import static com.metamystia.server.console.command.CommandManager.literal;

public class MiscCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("help").executes(MiscCommands::helpCommandNoArgs)
                        .then(argument("command", StringArgumentType.greedyString()).executes(MiscCommands::helpCommand))
        );
        dispatcher.register(
                literal("version").requires(commandSource -> commandSource.user().hasPermissionAtLeast(PermissionLevel.USER))
                        .executes(MiscCommands::versionCommand)
        );
    }

    private static int helpCommandNoArgs(CommandContext<CommandSource> context) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAvailable commands:");

        String[] allUsage = CommandManager.dispatcher.getAllUsage(CommandManager.dispatcher.getRoot(), context.getSource(), true);
        for (String usage : allUsage) {
            sb.append("\n").append(CommandManager.COMMAND_PREFIX).append(usage);
        }

        context.getSource().user().sendMessage(sb.toString());
        return 1;
    }

    private static int helpCommand(CommandContext<CommandSource> context) {
        String command = StringArgumentType.getString(context, "command");

        context.getSource().user().sendMessage("Not implemented yet!");  // TODO
        return 1;
    }

    private static int versionCommand(CommandContext<CommandSource> context) {
        context.getSource().user().sendMessage(ManifestManager.getManifest().versionInfo());
        return 1;
    }
}
