package com.metamystia.server.console.command;

import com.metamystia.server.util.DebugUtils;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class CommandManager {
    public static final String COMMAND_PREFIX = "!";
    private static final Map<String, ParseResults<CommandSource>> PARSE_CACHE = new ConcurrentHashMap<>();

    public static final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

    public static void init() {
        dispatcher.register(
                literal("help").executes(DebugCommands::helpCommandNoArgs)
                        .then(argument("command", StringArgumentType.greedyString()).executes(DebugCommands::helpCommand))
        );
        dispatcher.register(
                literal("debug").requires(commandSource -> DebugUtils.debug)
                        .then(literal("stop").executes(DebugCommands::stopCommand))
                        .then(literal("sendReady").executes(DebugCommands::sendReadyCommand))
                        .then(literal("sendSelect")
                                .then(argument("mapLabel", StringArgumentType.word())
                                        .then(argument("mapLevel", IntegerArgumentType.integer(1, 3))
                                                .executes(DebugCommands::sendSelectCommand))))
                        .then(literal("closeWithReason")
                                .then(argument("reason", StringArgumentType.greedyString()).executes(DebugCommands::closeWithReasonCommand)))
                        .then(literal("switchEcho").executes(DebugCommands::switchEchoCommand))
        );
    }

    public static void parse(String command, CommandSource source){
        if(command.startsWith(COMMAND_PREFIX)) command = command.substring(1);
        try {
            ParseResults<CommandSource> parseResults;
            if(PARSE_CACHE.containsKey(command)) {
                parseResults = PARSE_CACHE.get(command);
            } else {
                parseResults = dispatcher.parse(command, source);
                PARSE_CACHE.put(command, parseResults);
            }
            dispatcher.execute(parseResults);
        } catch (Exception e) {
            log.error("Failed to parse command: {} from {}", command, source.user(), e);
            source.user().sendMessage("Failed to parse command: " + e.getMessage());
        }
    }

    public static void clearCache() {
        PARSE_CACHE.clear();
    }

    public static LiteralArgumentBuilder<CommandSource> literal(final String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<CommandSource, T> argument(final String name, final ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }
}
