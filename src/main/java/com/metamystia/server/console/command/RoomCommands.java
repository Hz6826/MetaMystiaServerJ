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
                        .then(literal("info").executes(RoomCommands::roomInfoCommand)
                                .then(literal("users").executes(RoomCommands::roomUserInfoCommand)))
                        .then(literal("create")
                                .then(literal("pair").executes(RoomCommands::createPairRoomCommand)))
                        .then(literal("inviteCode").executes(RoomCommands::inviteCodeCommand)
                                .then(literal("renew").executes(RoomCommands::renewInviteCodeCommand)))
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
        if (notInRoom(ctx.getSource())) {
            return 0;
        }
        ctx.getSource().user().sendMessage(ctx.getSource().user().getRoom().orElseThrow().getInfo());
        return 1;
    }

    private static int roomUserInfoCommand(CommandContext<CommandSource> ctx) {
        if (notInRoom(ctx.getSource())) {
            return 0;
        }
        ctx.getSource().user().sendMessage("\nUsers in room:\n" + ctx.getSource().user().getRoom().orElseThrow().getUserNames());
        return 1;
    }

    private static int createPairRoomCommand(CommandContext<CommandSource> context) {
        AbstractRoom pairRoom = new PairRoom();
        AbstractRoom oldRoom = context.getSource().user().getRoom().orElseThrow();
        if (oldRoom instanceof PairRoom && oldRoom.isOwnedBy(context.getSource().user())) {
            context.getSource().user().sendMessage("You are already in a pair room.");
            return 0;
        }

        if (oldRoom != RoomManager.getLobbyRoom()) {
            context.getSource().user().sendMessage("You are already in room " + oldRoom.getName() + ". Leaving...");
        }
        oldRoom.removeUser(context.getSource().user());
        RoomManager.addRoom(pairRoom);
        pairRoom.addUser(context.getSource().user());
        context.getSource().user().sendMessage("Created pair room.");
        return 1;
    }

    private static int inviteCodeCommand(CommandContext<CommandSource> context) {
        if (notInRoom(context.getSource())) {
            return 0;
        }
        if (!context.getSource().user().getRoom().orElseThrow().isOwnedBy(context.getSource().user())) {
            context.getSource().user().sendMessage("You are not the owner of this room.");
            return 0;
        }
        AbstractRoom room = context.getSource().user().getRoom().orElseThrow();
        context.getSource().user().sendMessage("Invite code: " + room.getInviteCode());
        return 1;
    }

    private static int renewInviteCodeCommand(CommandContext<CommandSource> context) {
        if (notInRoom(context.getSource())) {
            return 0;
        }
        if (!context.getSource().user().getRoom().orElseThrow().isOwnedBy(context.getSource().user())) {
            context.getSource().user().sendMessage("You are not the owner of this room.");
            return 0;
        }
        AbstractRoom room = context.getSource().user().getRoom().orElseThrow();
        String inviteCode = room.renewInviteCode();
        context.getSource().user().sendMessage("Renewed invite code. New code: " + inviteCode);
        return 1;
    }

    private static int joinRoomCommand(CommandContext<CommandSource> context) {
        String inviteCode = StringArgumentType.getString(context, "inviteCode");
        RoomManager.getRoomByInviteCode(inviteCode).ifPresentOrElse(room -> {
            if (!room.isVisibleTo(context.getSource().user())) {
                context.getSource().user().sendMessage("You cannot join this room.");
                return;
            }
            context.getSource().user().getRoom().ifPresent(roomOld -> roomOld.removeUser(context.getSource().user()));
            room.addUser(context.getSource().user());
            context.getSource().user().sendMessage("Joined room " + room.getName());
        }, () -> context.getSource().user().sendMessage("Invalid invite code."));
        return 1;
    }

    private static int leaveRoomCommand(CommandContext<CommandSource> context) {
        if (notInRoom(context.getSource())) {
            return 0;
        }
        if (context.getSource().user().getRoom().orElseThrow().equals(RoomManager.getLobbyRoom())) {
            context.getSource().user().sendMessage("You cannot leave the lobby.");
            return 0;
        }
        AbstractRoom room = context.getSource().user().getRoom().orElseThrow();
        room.removeUser(context.getSource().user());
        context.getSource().user().sendMessage("Left room " + room.getName());
        if (!context.getSource().user().isInRoom()) {
            RoomManager.getLobbyRoom().addUser(context.getSource().user());
        }
        return 1;
    }

    private static int disbandRoomCommand(CommandContext<CommandSource> context) {
        if (notInRoom(context.getSource())) {
            return 0;
        }
        AbstractRoom room = context.getSource().user().getRoom().orElseThrow();
        if (!room.isDisowned() && !room.isOwnedBy(context.getSource().user())) {
            context.getSource().user().sendMessage("You are not the owner of this room.");
            return 0;
        }
        RoomManager.removeRoom(room);
        context.getSource().user().sendMessage("Disbanded room " + room.getName());
        return 1;
    }

    // ==== helper methods ====

    private static boolean notInRoom(CommandSource context) {
        if (!context.user().isInRoom()) {
            context.user().sendMessage("You are not in a room.");
            return true;
        }
        return false;
    }
}
