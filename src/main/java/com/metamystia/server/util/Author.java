package com.metamystia.server.util;

import lombok.NonNull;

public record Author(String name, String url) {
    @NonNull
    public String toString() {
        return name + " (" + url + ")";
    }
}
