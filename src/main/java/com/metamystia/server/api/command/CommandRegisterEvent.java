package com.metamystia.server.api.command;

import com.mojang.brigadier.CommandDispatcher;
import lombok.Getter;

import java.util.EventObject;

public class CommandRegisterEvent extends EventObject {
    @Getter
    private final CommandDispatcher<CommandSource> dispatcher;

    public CommandRegisterEvent(Object source, CommandDispatcher<CommandSource> dispatcher) {
        super(source);
        this.dispatcher = dispatcher;
    }
}