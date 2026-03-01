package com.metamystia.server.core.plugin;

import lombok.Data;

@Data
public class PluginInfo {
    private String id;
    private String name;
    private String version;
    private String author;
    private String description;
    private String website;

    private String entrypoint;
    private String authProvider;

    private String[] dependencies;
}
