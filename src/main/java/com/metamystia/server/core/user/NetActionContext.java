package com.metamystia.server.core.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "of")
public class NetActionContext {
    private String channelId;
    private int sourceRoomId;
}
