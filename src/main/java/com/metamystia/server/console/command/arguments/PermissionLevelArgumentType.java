package com.metamystia.server.console.command.arguments;

import com.metamystia.server.core.user.PermissionLevel;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;

public class PermissionLevelArgumentType implements ArgumentType<PermissionLevel> {
    private static final String TO_STRING_CACHE;

    private PermissionLevelArgumentType() {
    }

    public static PermissionLevelArgumentType permissionLevel() {
        return new PermissionLevelArgumentType();
    }

    public static PermissionLevel getPermissionLevel(final CommandContext<?> context, final String name) {
        return context.getArgument(name, PermissionLevel.class);
    }

    @Override
    public String toString() {
        return TO_STRING_CACHE;
    }

    @Override
    public PermissionLevel parse(StringReader reader) throws CommandSyntaxException {
        try {
            return PermissionLevel.valueOf(reader.readUnquotedString());
        } catch (IllegalArgumentException e) {
            throw CommandSyntaxException.BUILT_IN_EXCEPTIONS.readerInvalidEscape().createWithContext(reader, "Invalid permission level.");
        }
    }

    static {
        PermissionLevel[] levels = PermissionLevel.values();
        StringBuilder sb = new StringBuilder("<");
        for (int i = 0; i < levels.length; i++) {
            sb.append(levels[i].name());
            if (i < levels.length - 1) {
                sb.append("|");
            }
        }
        sb.append(">");
        TO_STRING_CACHE = sb.toString();
    }
}
