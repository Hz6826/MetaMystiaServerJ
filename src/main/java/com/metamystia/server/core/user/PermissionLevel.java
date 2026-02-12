package com.metamystia.server.core.user;

public enum PermissionLevel {
    GUEST,
    USER,
    MODERATOR,
    ADMIN;

    public boolean isAtLeast(PermissionLevel other) {
        return this.ordinal() >= other.ordinal();
    }
}
