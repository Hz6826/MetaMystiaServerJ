package com.metamystia.server.api.command;

import com.metamystia.server.core.plugin.PluginManager;
import com.metamystia.server.core.user.User;

public record CommandSource(User user, long timestamp) {
    public boolean permissionCheck(String permissionNode) {
        return PluginManager.getAuthProvider().permissionCheck(user(), permissionNode);
    }
}
