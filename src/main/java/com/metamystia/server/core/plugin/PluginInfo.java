package com.metamystia.server.core.plugin;

import com.metamystia.server.util.Author;
import lombok.Data;

import java.util.List;

@Data
public class PluginInfo {
    private String id;
    private String version;

    private String name;
    private String description;
    private String license;
    private String source;
    private String issueTracker;

    private List<Author> authors;

    private String entrypoint;
    private String authProvider;

    private String[] dependencies;
}
