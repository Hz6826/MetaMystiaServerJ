package com.metamystia.server.core.user;

import com.metamystia.server.network.actions.HelloAction;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class UserManager {
    private static final Map<Long, User> userIdMap= new ConcurrentHashMap<>();

    public static User createUser(HelloAction helloAction, String channelId) {
        User user = User.of(helloAction, channelId);
        if (userIdMap.containsKey(user.getId())) {
            throw new IllegalArgumentException("User with ID " + user.getId() + " already exists");
        }
        userIdMap.put(user.getId(), user);
        return user;
    }

    public static void removeUser(User user) {
        userIdMap.remove(user.getId());
    }

    public static Optional<User> getUserById(long id) {
        return Optional.ofNullable(userIdMap.get(id));
    }

    public static Optional<User> getUserByPeerId(String peerId) {
        return userIdMap.values().stream().filter(user -> user.getPeerId().equals(peerId)).findFirst();
    }

    public static Optional<User> getUserByChannelId(String channelId) {
        return userIdMap.values().stream().filter(user -> user.getChannelId().equals(channelId)).findFirst();
    }

    public static String getUserOrChannelIdString(String channelId) {
        return getUserByChannelId(channelId).map(User::getPeerId).orElse(channelId);
    }
}
