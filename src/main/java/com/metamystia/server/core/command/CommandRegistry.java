package com.metamystia.server.core.command;

import com.metamystia.server.api.command.CommandManager;

public class CommandRegistry {
    public static void registerCommands() {
        CommandManager.subscribe(event -> DebugCommands.register(event.getDispatcher()));
        CommandManager.subscribe(event -> RoomCommands.register(event.getDispatcher()));
        CommandManager.subscribe(event -> MiscCommands.register(event.getDispatcher()));
        CommandManager.subscribe(event -> PluginCommands.register(event.getDispatcher()));
    }
}
