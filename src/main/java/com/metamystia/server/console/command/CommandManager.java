package com.metamystia.server.console.command;

import com.metamystia.server.network.GameServer;
import com.metamystia.server.network.actions.ReadyAction;
import com.metamystia.server.network.actions.SelectAction;
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

    private final CommandDispatcher<CommandSource> dispatcher = new CommandDispatcher<>();

    private CommandManager() {
        this.dispatcher.register(
                literal("debug").requires(commandSource -> DebugUtils.debug)
                        .then(literal("help").executes(context -> {
                            context.getSource().user().sendMessage("Hello, world!");
                            log.info("Help command executed.");
                            return 1;
                        }))
                        .then(literal("stop").executes( context -> {
                            GameServer.getInstance().stop();
                            return 1;
                        }))
                        .then(literal("sendReady").executes(context -> {
                            context.getSource().user().sendAction(new ReadyAction(ReadyAction.ReadyType.DayOver, true));
                            return 1;
                        }))
                        .then(literal("sendSelect")
                                .then(argument("mapLabel", StringArgumentType.word())
                                        .then(argument("mapLevel", IntegerArgumentType.integer(1, 3)).executes(context -> {
                                            String mapLabel = StringArgumentType.getString(context, "mapLabel");
                                            int mapLevel = IntegerArgumentType.getInteger(context, "mapLevel");
                                            context.getSource().user().sendAction(new SelectAction(mapLabel, mapLevel));
                                            return 1;
                        }))))
                        .then(literal("switchEcho").executes(context -> {
                            DebugUtils.echo = !DebugUtils.echo;
                            context.getSource().user().sendMessage("Echo mode switched to: " + DebugUtils.echo);
                            return 1;
                        }))

        );
    }

    public void parse(String command, CommandSource source){
        if(command.startsWith(COMMAND_PREFIX)) command = command.substring(1);
        try {
            ParseResults<CommandSource> parseResults;
            if(PARSE_CACHE.containsKey(command)) {
                parseResults = PARSE_CACHE.get(command);
            } else {
                parseResults = this.dispatcher.parse(command, source);
                PARSE_CACHE.put(command, parseResults);
            }
            dispatcher.execute(parseResults);
        } catch (Exception e) {
            log.error("Failed to parse command: {}", e.getMessage(), e);
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

    private static final CommandManager INSTANCE = new CommandManager();

    public static CommandManager getInstance() {
        return INSTANCE;
    }
}
