package com.metamystia.server;

import com.hz6826.memorypack.util.MemoryPackInitializerWrapper;
import com.metamystia.server.config.AccessControlManager;
import com.metamystia.server.config.ConfigManager;
import com.metamystia.server.console.command.CommandManager;
import com.metamystia.server.core.room.RoomManager;
import com.metamystia.server.network.GameServer;
import com.metamystia.server.util.BootstrapTips;
import com.metamystia.server.util.ManifestManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Main {
    public static final String SERVER_NAME = "MetaMystiaServerJ";

    public static void main(String[] args) {
        ManifestManager.loadManifest();
        log.info("Starting {}...", ManifestManager.getManifest().name());

        List<String> argsList = Arrays.asList(args);
        if (argsList.contains("--config")) {
            ConfigManager.loadConfigFromFile(argsList.get(argsList.indexOf("--config") + 1));
        } else {
            ConfigManager.loadConfigFromFile();
        }

        if (ConfigManager.getConfig().isShowManifest()) {
            log.info(ManifestManager.getManifest().toString());
        } else {
            log.info(ManifestManager.getManifest().versionInfo());
        }

        if (!ConfigManager.getConfig().isNoTip()) {
            log.info("Tip: {}", BootstrapTips.getTip());
        }

        if (ConfigManager.getConfig().isDebug()) {
            log.warn("Debug mode enabled! Don't enable it in production environment!");
        }

        AccessControlManager.loadLists();
        MemoryPackInitializerWrapper.registerAll();
        CommandManager.init();
        RoomManager.init();

        try {
            GameServer.getInstance().run();
        } catch (Exception e) {
            log.error("Failed to start game server", e);
        }
    }
}