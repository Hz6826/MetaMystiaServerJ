package com.metamystia.server.util;

import io.netty.buffer.ByteBuf;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DebugUtils {
    public static void logBufHex(ByteBuf buf, String message) {
        ByteBuf copy = buf.copy();
        StringBuilder sb = new StringBuilder();
        sb.append(message).append(": ");
        // print all in hex
        while (copy.readableBytes() > 0) {
            sb.append(String.format("%02X ", copy.readByte()));
        }
        copy.release();
        log.debug(sb.toString());
    }
}
