package com.metamystia.server.api.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;

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
        eventBus.post(new CommandRegisterEvent(CommandManager.class, dispatcher));
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

    private static class EventBus {
        private final List<Consumer<CommandRegisterEvent>> listeners = new CopyOnWriteArrayList<>();

        public void register(Consumer<CommandRegisterEvent> listener) {
            listeners.add(listener);
        }

        public void post(CommandRegisterEvent event) {
            listeners.forEach(l -> l.accept(event));
        }
    }

    private static final EventBus eventBus = new EventBus();

    public static void subscribe(Consumer<CommandRegisterEvent> listener) {
        eventBus.register(listener);
    }
}
