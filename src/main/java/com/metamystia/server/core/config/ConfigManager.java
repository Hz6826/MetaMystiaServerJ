package com.metamystia.server.core.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class ConfigManager {
    public static final String DEFAULT_CONFIG_PATH = "config.json";

    private static String lastConfigPath = DEFAULT_CONFIG_PATH;

    private static final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

    @Data
    public static class Config {
        private String serverName = "MetaMystia Server";
        private String serverDescription = "A server for the MetaMystia mod.";

        private int port = 40815;
        private int maxPlayers = 20;
        private int helloTimeoutSeconds = 5;

        private boolean whitelist = false;
        private String whitelistIpFile = "whitelist_ip.txt";

        private boolean blacklist = false;
        private String blacklistIpFile = "blacklist_ip.txt";

        private String pluginFolder = "plugins";
        private String authProviderPluginId = "";

        private boolean noTip = false;
        private boolean showManifest = false;
        private boolean debug = false;
        private boolean logHex = false;
        private boolean disableAuth = false;
    }

    @Getter
    private static Config config = new Config();

    public static void loadConfigFromFile() {
        loadConfigFromFile(lastConfigPath);
    }

    public static void loadConfigFromFile(String path) {
        log.info("Loading config from {}", path);
        lastConfigPath = path;
        File file = new File(path);
        if (file.exists()) {
            try {
                config = mapper.readValue(file, Config.class);
                log.info("Config loaded successfully.");
            } catch (IOException e) {
                log.error("Failed to load config from file, using default config.", e);
            }
        } else {
            log.info("Config file not found, creating default config file.");
            saveConfigToFile(path);
        }
    }

    public static void saveConfigToFile() {
        saveConfigToFile(lastConfigPath);
    }

    public static void saveConfigToFile(String path) {
        log.info("Saving config to {}", path);
        lastConfigPath = path;
        try {
            mapper.writeValue(new File(path), config);
            log.info("Config saved successfully.");
        } catch (IOException e) {
            log.error("Failed to save config to file.", e);
        }
    }

    public static void mergeFromArgsList(List<String> argsList) {
        if (!argsList.contains("--no-tip")) config.setNoTip(true);
        if (argsList.contains("--show-manifest")) config.setShowManifest(true);
        if (argsList.contains("--debug")) config.setDebug(true);
        if (argsList.contains("--log-hex")) config.setLogHex(true);
        if (argsList.contains("--port")) config.setPort(Integer.parseInt(argsList.get(argsList.indexOf("--port") + 1)));
        if (argsList.contains("-p")) config.setPort(Integer.parseInt(argsList.get(argsList.indexOf("-p") + 1)));
        if (argsList.contains("--plugin-folder")) config.setPluginFolder(argsList.get(argsList.indexOf("--plugin-folder") + 1));
    }
}
