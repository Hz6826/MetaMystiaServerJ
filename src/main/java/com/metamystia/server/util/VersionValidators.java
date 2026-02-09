package com.metamystia.server.util;


import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class VersionValidators {
    private static final Pattern VERSION_PATTERN = Pattern.compile("^(\\d+)\\.(\\d+)\\.(\\d+)$");

    public static boolean isMetaMystiaVersionValid(String version) {
        if (ManifestManager.getManifest() == null) return false;
        return isVersionValid(version, ManifestManager.getManifest().metaMystiaVersion());
    }

    public static boolean isVersionValid(String version, String constraint) {
        if (!isValidVersionFormat(version)) {
            return false;
        }


        if (constraint == null || constraint.isBlank()) {
            return true;
        }

        return satisfiesConstraint(version, constraint.trim());
    }

    private static boolean isValidVersionFormat(String version) {
        if (version == null) {
            return false;
        }
        return VERSION_PATTERN.matcher(version).matches();
    }

    private static boolean satisfiesConstraint(String version, String constraint) {
        int[] versionParts = parseVersion(version);
        if (constraint.startsWith("<=")) {
            return compareVersions(versionParts, constraint.substring(2).trim()) <= 0;
        } else if (constraint.startsWith(">=")) {
            return compareVersions(versionParts, constraint.substring(2).trim()) >= 0;
        } else if (constraint.startsWith("<")) {
            return compareVersions(versionParts, constraint.substring(1).trim()) < 0;
        } else if (constraint.startsWith(">")) {
            return compareVersions(versionParts, constraint.substring(1).trim()) > 0;
        } else if (constraint.startsWith("~=") || constraint.startsWith("~")) {
            String constraintVersion = constraint.startsWith("~=") ?
                    constraint.substring(2).trim() : constraint.substring(1).trim();
            return satisfiesCompatible(versionParts, constraintVersion);
        } else {
            return satisfiesExact(versionParts, constraint);
        }
    }

    private static boolean satisfiesExact(int[] versionParts, String expectedVersion) {
        int[] expectedParts = parseVersion(expectedVersion);
        if (expectedParts == null) {
            return false;
        }

        return versionParts[0] == expectedParts[0] &&
                versionParts[1] == expectedParts[1] &&
                versionParts[2] == expectedParts[2];
    }

    private static boolean satisfiesCompatible(int[] versionParts, String constraintVersion) {
        int[] constraintParts = parseVersion(constraintVersion);
        if (constraintParts == null) {
            return false;
        }

        // version > constraint
        if (compareVersions(versionParts, constraintParts) < 0) {
            return false;
        }

        // major version not equal
        if (versionParts[0] > constraintParts[0]) {
            return false;
        }

        // minor version
        if (versionParts[0] == constraintParts[0]) {
            return versionParts[1] == constraintParts[1];
        }

        return false;
    }

    private static int compareVersions(int[] version1Parts, String version2) {
        int[] version2Parts = parseVersion(version2);
        if (version2Parts == null) {
            throw new IllegalArgumentException("Invalid version format: " + version2);
        }
        return compareVersions(version1Parts, version2Parts);
    }

    private static int compareVersions(int[] version1Parts, int[] version2Parts) {
        for (int i = 0; i < 3; i++) {
            if (version1Parts[i] != version2Parts[i]) {
                return Integer.compare(version1Parts[i], version2Parts[i]);
            }
        }
        return 0;
    }

    private static int[] parseVersion(String version) {
        Matcher matcher = VERSION_PATTERN.matcher(version);
        if (!matcher.matches()) {
            return null;
        }

        try {
            int major = Integer.parseInt(matcher.group(1));
            int minor = Integer.parseInt(matcher.group(2));
            int patch = Integer.parseInt(matcher.group(3));

            return new int[]{major, minor, patch};
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
