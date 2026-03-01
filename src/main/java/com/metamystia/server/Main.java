package com.metamystia.server;

import com.hz6826.memorypack.util.MemoryPackInitializerWrapper;
import com.metamystia.server.api.command.CommandManager;
import com.metamystia.server.core.command.CommandRegistry;
import com.metamystia.server.core.config.AccessControlManager;
import com.metamystia.server.core.config.ConfigManager;
import com.metamystia.server.core.plugin.PluginManager;
import com.metamystia.server.core.room.RoomManager;
import com.metamystia.server.network.GameServer;
import com.metamystia.server.util.BootstrapTips;
import com.metamystia.server.util.ManifestManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) {
        ManifestManager.loadManifest();
        log.info("Starting {}...", ManifestManager.getManifest().name());

        List<String> argsList = Arrays.asList(args);

        if (argsList.contains("--config")) {
            ConfigManager.loadConfigFromFile(argsList.get(argsList.indexOf("--config") + 1));
        } else {
            ConfigManager.loadConfigFromFile();
        }
        ConfigManager.mergeFromArgsList(argsList);

        if (ConfigManager.getConfig().isShowManifest()) {
            log.info(ManifestManager.getManifest().toString());
        } else {
            log.info(ManifestManager.getManifest().versionInfo());
        }

        if (!ConfigManager.getConfig().isNoTip()) {
            log.info("Tip: {}", BootstrapTips.getTip());
        }

        if (ConfigManager.getConfig().isDebug()) {
            log.warn("Debug mode is enabled! This is not recommended in production environment!");
        }

        if (ConfigManager.getConfig().isDisableAuth()) {
            log.warn("Auth is disabled! This is not recommended in production environment!");
        }

        CommandRegistry.registerCommands();
        PluginManager.init();

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