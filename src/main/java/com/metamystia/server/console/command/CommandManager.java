package com.metamystia.server.console.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Slf4j
public class CommandManager {
    public static final String COMMAND_PREFIX = "!";

    public static final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

    private static final ExecutorService commandExecutor =
            Executors.newFixedThreadPool(2, r -> {
                Thread t = new Thread(r, "Command-Worker");
                t.setDaemon(true);
                return t;
            });

    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1);

    public static void init() {
        DebugCommands.register(dispatcher);
        RoomCommands.register(dispatcher);
        AuthCommands.register(dispatcher);
        MiscCommands.register(dispatcher);

    }

    private static void parse(String command, CommandSource source){
        if(command.startsWith(COMMAND_PREFIX)) command = command.substring(1);
        try {
            dispatcher.execute(dispatcher.parse(command, source));
        } catch (Exception e) {
            log.error("Failed to parse command: \"{}{}\" from {}", COMMAND_PREFIX, command, source.user().getPeerId(), e);
            source.user().sendMessage("Failed to parse command: " + e.getMessage());
        }
    }

    public static void parseAsync(String command, CommandSource source) {
        commandExecutor.submit(() -> parse(command, source));
    }

    public static void shutdown() {
        commandExecutor.shutdown();
        SCHEDULER.shutdown();
    }

    public static LiteralArgumentBuilder<CommandSource> literal(final String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
