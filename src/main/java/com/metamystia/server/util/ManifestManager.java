package com.metamystia.server.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.util.List;

@Slf4j
public class ManifestManager {
    public record ManifestInfo(String name, String description, String license, String source, String issueTracker,
                               List<Author> authors, String version, String buildTime, String gitCommit,
                               String metaMystiaVersion) {
        @NonNull
        public String toString() {
            return "\n" +
                    "========MANIFEST========\n" +
                    "Name: " + name + "\n" +
                    "Description: " + description + "\n" +
                    "License: " + license + "\n" +
                    "Source: " + source + "\n" +
                    "Issue Tracker: " + issueTracker + "\n" +
                    "Authors: " + authors + "\n" +
                    "Version: " + version + "\n" +
                    "Build Time: " + buildTime + "\n" +
                    "Git Commit: " + gitCommit + "\n" +
                    "MetaMystia Version: " + metaMystiaVersion + "\n" +
                    "========================";
        }

        public String versionInfo() {
            return "Version: " + version + " built at " + buildTime + " with commit " + gitCommit + " for MetaMystia " + metaMystiaVersion;
        }
    }

    @Getter
    static private ManifestInfo manifest;

    public static void loadManifest() {
        log.debug("Loading manifest...");
        ObjectMapper objectMapper = new ObjectMapper();
        try (InputStream in = ManifestInfo.class.getClassLoader().getResourceAsStream("manifest.json")){
            if(in == null) throw new IllegalAccessException("File not found");
            manifest = objectMapper.readValue(in, ManifestInfo.class);
        } catch (Exception e) {
            log.error("Failed to load manifest", e);
        }
    }
}
