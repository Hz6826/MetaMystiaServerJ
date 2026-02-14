package com.metamystia.server.config;

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
    private record Lists(Set<Long> whitelistIds, Set<String> whitelistIps, Set<Long> blacklistIds,
                         Set<String> blacklistIps) {
            public Lists(Set<Long> whitelistIds, Set<String> whitelistIps,
                          Set<Long> blacklistIds, Set<String> blacklistIps) {
                this.whitelistIds = Set.copyOf(whitelistIds);
                this.whitelistIps = Set.copyOf(whitelistIps);
                this.blacklistIds = Set.copyOf(blacklistIds);
                this.blacklistIps = Set.copyOf(blacklistIps);
            }
        }

    private static volatile Lists lists = new Lists(
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptySet(),
            Collections.emptySet()
    );

    public static void loadLists() {
        ConfigManager.Config config = ConfigManager.getConfig();

        Set<Long> newWhitelistIds = loadLongSet(config.getWhitelistFile());
        Set<String> newWhitelistIps = loadStringSet(config.getWhitelistIpFile());
        Set<Long> newBlacklistIds = loadLongSet(config.getBlacklistFile());
        Set<String> newBlacklistIps = loadStringSet(config.getBlacklistIpFile());

        lists = new Lists(newWhitelistIds, newWhitelistIps, newBlacklistIds, newBlacklistIps);

        log.info("Loaded whitelist IDs: {}, whitelist IPs: {}, blacklist IDs: {}, blacklist IPs: {}",
                newWhitelistIds.size(), newWhitelistIps.size(), newBlacklistIds.size(), newBlacklistIps.size());
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

    private static Set<Long> loadLongSet(String filename) {
        Set<String> strings = loadStringSet(filename);
        Set<Long> longs = new HashSet<>();
        for (String s : strings) {
            try {
                longs.add(Long.parseLong(s));
            } catch (NumberFormatException e) {
                log.warn("Invalid ID in {}: {}", filename, s);
            }
        }
        return longs;
    }

    public static boolean isIdWhitelisted(long id) {
        return lists.whitelistIds.contains(id);
    }

    public static boolean isIpWhitelisted(String ip) {
        return lists.whitelistIps.contains(ip);
    }

    public static boolean isIdBlacklisted(long id) {
        return lists.blacklistIds.contains(id);
    }

    public static boolean isIpBlacklisted(String ip) {
        return lists.blacklistIps.contains(ip);
    }

    public static Set<Long> getWhitelistIds() {
        return lists.whitelistIds;
    }

    public static Set<String> getWhitelistIps() {
        return lists.whitelistIps;
    }

    public static Set<Long> getBlacklistIds() {
        return lists.blacklistIds;
    }

    public static Set<String> getBlacklistIps() {
        return lists.blacklistIps;
    }
}