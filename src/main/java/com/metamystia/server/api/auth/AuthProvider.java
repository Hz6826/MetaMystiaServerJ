package com.metamystia.server.api.auth;

import com.metamystia.server.core.user.User;
import com.metamystia.server.network.actions.AbstractNetAction;

public interface AuthProvider {
    void init();
    void shutdown();

    void onUserJoin(User user);
    void onUserLeave(User user);

    boolean permissionCheck(User user, String permissionNode);
    boolean allowAction(User user, AbstractNetAction action);
}
