package com.metamystia.server.core.room;

import com.metamystia.server.core.user.User;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class RoomManager {
    public static final int NO_ROOM = -1;
    public static int DEFAULT_LOBBY_ID = RoomManager.NO_ROOM;

    @Getter
    private static AbstractRoom lobbyRoom;

    private static final Map<Integer, AbstractRoom> rooms = new ConcurrentHashMap<>();
    private static final AtomicInteger nextRoomId = new AtomicInteger(0);
    private static final Map<String, Integer> inviteCodes = new ConcurrentHashMap<>();

    public static synchronized void init() {
        lobbyRoom = new LobbyRoom();
        addRoom(lobbyRoom);
        DEFAULT_LOBBY_ID = lobbyRoom.getRoomId();
    }

    public static void addRoom(AbstractRoom room) {
        AbstractRoom old = rooms.putIfAbsent(room.getRoomId(), room);
        if (old != null) {
            throw new IllegalStateException("Room ID already exists: " + room.getRoomId());
        }
    }

    public static void removeRoom(AbstractRoom room, boolean force, int toRoomId) {
        if (room.getRoomId() == NO_ROOM) {
            throw new IllegalArgumentException("Cannot remove room with ID " + NO_ROOM);
        }
        if (room.getRoomId() == lobbyRoom.getRoomId() && !force) {
            throw new IllegalArgumentException("Cannot remove lobby room");
        }
        if (room.onRoomDestroy() && !force) {
            return;
        }
        room.broadcastToRoom("Room " + room.getName() + " has been closed.");

        if (room.isInviteCodeGenerated()) {
            inviteCodes.remove(room.getInviteCode());
        }
        List<Long> userIdsCopy = room.getUserIds().stream().toList();
        userIdsCopy.forEach(userId ->
                User.getUserById(userId).ifPresent(user -> room.removeUser(user, toRoomId))
        );
        rooms.remove(room.getRoomId());
    }

    public static void removeRoom(AbstractRoom room) {
        removeRoom(room, false, DEFAULT_LOBBY_ID);
    }

    public static List<AbstractRoom> getAllRooms() {
        return new ArrayList<>(rooms.values());
    }

    public static List<AbstractRoom> getFilteredRooms(Predicate<AbstractRoom> predicate) {
        return rooms.values().stream().filter(predicate).collect(Collectors.toList());
    }

    public static void shutdown() {
        rooms.values().forEach(room -> removeRoom(room, true, NO_ROOM));
    }

    public static Optional<AbstractRoom> getRoom(int roomId) {
        return Optional.ofNullable(rooms.get(roomId));
    }

    public static Optional<AbstractRoom> getRoomByName(String roomName) {
        return rooms.values().stream().filter(room -> room.getCustomName().equals(roomName)).findFirst();
    }

    public static Optional<AbstractRoom> getRoomByUser(User user) {
        return rooms.values().stream().filter(room -> room.isUserInRoom(user)).findFirst();
    }

    public static int nextRoomId() {
        return nextRoomId.getAndIncrement();
    }

    private static String getInviteCodeString() {
        String chars = "ABCDEFGHIJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz0123456789";  // no O or l
        int length = 6;
        int maxAttempts = 100;
        ThreadLocalRandom random = ThreadLocalRandom.current();

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            StringBuilder sb = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                sb.append(chars.charAt(random.nextInt(chars.length())));
            }
            String code = sb.toString();
            if (!inviteCodes.containsKey(code)) {
                return code;
            }
        }
        throw new RuntimeException("Failed to generate unique invite code after " + maxAttempts + " attempts");
    }

    public static String assignInviteCode(AbstractRoom room) {
        String inviteCode = getInviteCodeString();
        inviteCodes.put(inviteCode, room.getRoomId());
        return inviteCode;
    }

    public static void removeInviteCode(String inviteCode) {
        inviteCodes.remove(inviteCode);
    }

    public static Optional<AbstractRoom> getRoomByInviteCode(String inviteCode) {
        Integer roomId = inviteCodes.get(inviteCode);
        if (roomId == null) {
            return Optional.empty();
        }
        return getRoom(roomId);
    }
}
