package com.metamystia.server.core.config;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
public class AccessControlManager {
    private record Lists(Set<String> whitelistIps, Set<String> blacklistIps) {
            public Lists(Set<String> whitelistIps, Set<String> blacklistIps) {
                this.whitelistIps = Set.copyOf(whitelistIps);
                this.blacklistIps = Set.copyOf(blacklistIps);
            }
        }

    private static volatile Lists lists = new Lists(
            Collections.emptySet(),
            Collections.emptySet()
    );

    public static void loadLists() {
        ConfigManager.Config config = ConfigManager.getConfig();

        Set<String> newWhitelistIps = loadStringSet(config.getWhitelistIpFile());
        Set<String> newBlacklistIps = loadStringSet(config.getBlacklistIpFile());

        lists = new Lists(newWhitelistIps, newBlacklistIps);

        log.info("Loaded whitelist IPs: {}, blacklist IPs: {}", newWhitelistIps.size(), newBlacklistIps.size());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private static Set<String> loadStringSet(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            try {
                File parent = file.getParentFile();
                if (parent != null && !parent.exists()) {
                    parent.mkdirs();
                }
                file.createNewFile();
                log.info("Created empty file: {}", filename);
            } catch (IOException e) {
                log.error("Failed to create file: {}", filename, e);
            }
            return new HashSet<>();
        }
        try (var lines = Files.lines(file.toPath())) {
            return lines.map(String::trim)
                    .filter(line -> !line.isEmpty() && !line.startsWith("#"))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            log.error("Failed to read file: {}", filename, e);
            return new HashSet<>();
        }
    }

    public static boolean isIpWhitelisted(String ip) {
        return lists.whitelistIps.contains(ip);
    }
    public static boolean isIpBlacklisted(String ip) {
        return lists.blacklistIps.contains(ip);
    }

    public static Set<String> getWhitelistIps() {
        return lists.whitelistIps;
    }
    public static Set<String> getBlacklistIps() {
        return lists.blacklistIps;
    }
}