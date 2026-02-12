package com.metamystia.server.console.command;

import com.metamystia.server.core.user.User;

public record CommandSource(User user, long timestamp) { }
