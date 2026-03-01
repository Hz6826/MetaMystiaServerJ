package com.metamystia.server.core.plugin;

import com.metamystia.server.api.auth.AuthProvider;
import com.metamystia.server.core.user.User;
import com.metamystia.server.network.actions.AbstractNetAction;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public final class EmptyAuthProvider implements AuthProvider {
    @Override
    public void init() {
        log.info("EmptyAuthProvider initialized");
    }

    @Override
    public void shutdown() {
        log.info("EmptyAuthProvider shutting down");
    }

    @Override
    public void onUserJoin(User user) {

    }

    @Override
    public void onUserLeave(User user) {

    }

    @Override
    public boolean permissionCheck(User user, String permissionNode) {
        if (permissionNode.startsWith("command.debug")) return false;
        return true;
    }

    @Override
    public boolean allowAction(User user, AbstractNetAction action) {
        return true;
    }
}
