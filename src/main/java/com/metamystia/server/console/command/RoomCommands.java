package com.metamystia.server.console.command;

import com.metamystia.server.core.room.AbstractRoom;
import com.metamystia.server.core.room.PairRoom;
import com.metamystia.server.core.room.RoomManager;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;

import static com.metamystia.server.console.command.CommandManager.argument;
import static com.metamystia.server.console.command.CommandManager.literal;

public class RoomCommands {
    public static void register(CommandDispatcher<CommandSource> dispatcher) {
        dispatcher.register(
                literal("room")
                        .then(literal("list").executes(RoomCommands::listRoomsCommand))
                        .then(literal("info").executes(RoomCommands::roomInfoCommand))
                        .then(literal("create")
                                .then(literal("pair").executes(RoomCommands::createPairRoomCommand)))
                        .then(literal("inviteCode").executes(RoomCommands::inviteCodeCommand))
                        .then(literal("join")
                                .then(argument("inviteCode", StringArgumentType.string()).executes(RoomCommands::joinRoomCommand))
                        )
                        .then(literal("leave").executes(RoomCommands::leaveRoomCommand))
                        .then(literal("disband").executes(RoomCommands::disbandRoomCommand))
        );
    }

    private static int listRoomsCommand(CommandContext<CommandSource> ctx) {
        StringBuilder sb = new StringBuilder();
        sb.append("\nAvailable rooms:\n");
        RoomManager.getFilteredRooms(room -> room.isVisibleTo(ctx.getSource().user()))
                .forEach(room -> sb.append("- ").append(room.getName()).append("\n"));
        ctx.getSource().user().sendMessage(sb.toString());
        return 1;
    }

    private static int roomInfoCommand(CommandContext<CommandSource> ctx) {
        if (!ctx.getSource().user().isInRoom()) {
            ctx.getSource().user().sendMessage("You are not in a room.");
            return 0;
        }
        ctx.getSource().user().sendMessage(ctx.getSource().user().getRoom().orElseThrow().getInfo());
        return 1;
    }

    private static int createPairRoomCommand(CommandContext<CommandSource> source) {
        AbstractRoom pairRoom = new PairRoom();
        RoomManager.addRoom(pairRoom);
        pairRoom.addUser(source.getSource().user());
        source.getSource().user().sendMessage("Created pair room.");
        return 1;
    }

    private static int inviteCodeCommand(CommandContext<CommandSource> source) {
        if (!source.getSource().user().isInRoom()) {
            source.getSource().user().sendMessage("You are not in a room.");
            return 0;
        }
        AbstractRoom room = source.getSource().user().getRoom().orElseThrow();
        source.getSource().user().sendMessage("Invite code: " + room.getInviteCode());
        return 1;
    }

    private static int joinRoomCommand(CommandContext<CommandSource> source) {
        String inviteCode = StringArgumentType.getString(source, "inviteCode");
        RoomManager.getRoomByInviteCode(inviteCode).ifPresentOrElse(room -> {
            room.addUser(source.getSource().user());
            source.getSource().user().sendMessage("Joined room " + room.getName());
        }, () -> source.getSource().user().sendMessage("Invalid invite code."));
        return 1;
    }

    private static int leaveRoomCommand(CommandContext<CommandSource> source) {
        if (!source.getSource().user().isInRoom()) {
            source.getSource().user().sendMessage("You are not in a room.");
            return 0;
        }
        AbstractRoom room = source.getSource().user().getRoom().orElseThrow();
        room.removeUser(source.getSource().user());
        source.getSource().user().sendMessage("Left room " + room.getName());
        return 1;
    }

    private static int disbandRoomCommand(CommandContext<CommandSource> source) {
        if (!source.getSource().user().isInRoom()) {
            source.getSource().user().sendMessage("You are not in a room.");
            return 0;
        }
        AbstractRoom room = source.getSource().user().getRoom().orElseThrow();
        if (!room.isDisowned() && !room.isOwnedBy(source.getSource().user())) {
            source.getSource().user().sendMessage("You are not the owner of this room.");
            return 0;
        }
        RoomManager.removeRoom(room);
        source.getSource().user().sendMessage("Disbanded room " + room.getName());
        return 1;
    }
}
