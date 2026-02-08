package com.metamystia.server;

import com.hz6826.memorypack.util.MemoryPackInitializerWrapper;
import com.metamystia.server.network.GameServer;
import com.metamystia.server.util.BootstrapTips;
import com.metamystia.server.util.ManifestManager;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;

@Slf4j
public class Main {
    public static void main(String[] args) throws Exception {
        log.info("Starting MetaMystiaServerJ...");
        if (!Arrays.asList(args).contains("--no-tip"))
            log.info("Tip: {}", BootstrapTips.getTip());

        ManifestManager.loadManifest();
        if (Arrays.asList(args).contains("--show-manifest"))
            log.info(ManifestManager.getManifest().toString());
        else
            log.info(ManifestManager.getManifest().versionInfo());

        MemoryPackInitializerWrapper.registerAll();

        try {
            GameServer.getInstance().run();
        } catch (Exception e) {
            log.error("Failed to start game server", e);
        }
    }
}