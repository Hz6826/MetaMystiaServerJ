package com.metamystia.server;

import com.hz6826.memorypack.util.MemoryPackInitializerWrapper;
import com.metamystia.server.console.command.CommandManager;
import com.metamystia.server.network.GameServer;
import com.metamystia.server.util.BootstrapTips;
import com.metamystia.server.util.DebugUtils;
import com.metamystia.server.util.ManifestManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.List;

@Slf4j
public class Main {
    public static void main(String[] args) {
        List<String> argsList = Arrays.asList(args);
        log.info("Starting MetaMystiaServerJ...");
        if (!argsList.contains("--no-tip")) {
            log.info("Tip: {}", BootstrapTips.getTip());
        }

        ManifestManager.loadManifest();
        if (argsList.contains("--show-manifest")) {
            log.info(ManifestManager.getManifest().toString());
        } else {
            log.info(ManifestManager.getManifest().versionInfo());
        }

        if (argsList.contains("--debug")) {
            DebugUtils.debug = true;
            log.info("Debug mode enabled");
        }

        if (argsList.contains("--log-hex")) {
            DebugUtils.logHex = true;
            log.info("Hex dump enabled");
        }

        MemoryPackInitializerWrapper.registerAll();
        CommandManager.init();

        try {
            GameServer.getInstance().run();
        } catch (Exception e) {
            log.error("Failed to start game server", e);
        }
    }
}