package com.metamystia.server.core.command;

import com.metamystia.server.api.command.CommandSource;
import com.metamystia.server.core.plugin.PluginInfo;
import com.metamystia.server.core.plugin.PluginManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import java.util.Map;

import static com.metamystia.server.api.command.CommandManager.argument;
import static com.metamystia.server.api.command.CommandManager.literal;

public class PluginCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("plugin").requires(commandSource -> commandSource.permissionCheck("command.plugin"))
                        .then(literal("status").executes(PluginCommands::statusCommand)
                                .then(argument("pluginName", StringArgumentType.word()).executes(PluginCommands::specificPluginStatusCommand)))

        );
    }

    private static int statusCommand(CommandContext<CommandSource> context) {
        StringBuilder sb = new StringBuilder("Plugins: \n");
        Map<String, PluginInfo> loadedPlugins = PluginManager.getLoadedPlugins();
        for (PluginInfo info : loadedPlugins.values()) {
            sb.append(info.getName())
                    .append(" v.").append(info.getVersion())
                    .append(" (id:").append(info.getId()).append(")")
                    .append("\n");
        }
        context.getSource().user().sendMessage(sb.toString());
        return 1;
    }

    private static int specificPluginStatusCommand(CommandContext<CommandSource> context) {
        String pluginName = StringArgumentType.getString(context, "pluginName");
        PluginInfo info = PluginManager.getPlugin(pluginName);
        if (info == null) {
            context.getSource().user().sendMessage("Plugin not found");
            return 0;
        }
        context.getSource().user().sendMessage(info.toString());
        return 1;
    }
}
