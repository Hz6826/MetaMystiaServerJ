package com.metamystia.server.core.plugin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.metamystia.server.api.auth.AuthProvider;
import com.metamystia.server.api.plugin.IPlugin;
import com.metamystia.server.core.config.ConfigManager;
import com.metamystia.server.util.ManifestManager;
import com.metamystia.server.util.VersionValidators;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Slf4j
public class PluginManager {
    private static final String MANIFEST_FILE = "mmsj.plugin.json";
    private static final String CORE_ID = "mmsj";

    private static final Map<String, PluginInfo> loadedPlugins = new HashMap<>();
    private static final Map<String, IPlugin> pluginInstances = new HashMap<>();
    private static final Map<String, Class<?>> authProviderClasses = new HashMap<>();
    private static final ObjectMapper mapper = new ObjectMapper();
    @Getter
    private static URLClassLoader sharedLoader;
    private static AuthProvider authProviderInstance;

    private static class PluginEntry {
        File jarFile;
        PluginInfo info;
        PluginEntry(File jarFile, PluginInfo info) {
            this.jarFile = jarFile;
            this.info = info;
        }
    }

    public static void init() {
        File pluginFolder = new File(ConfigManager.getConfig().getPluginFolder());
        if (!pluginFolder.exists()) {
            if (!pluginFolder.mkdirs()) {
                log.error("Failed to create plugin folder: {}", pluginFolder.getAbsolutePath());
                setEmptyAuthProvider();
                return;
            }
        }

        File[] jarFiles = pluginFolder.listFiles((dir, name) -> name.endsWith(".jar"));
        if (jarFiles == null || jarFiles.length == 0) {
            log.info("No plugin jars found in {}", pluginFolder.getAbsolutePath());
            setEmptyAuthProvider();
            return;
        }

        List<PluginEntry> allEntries = new ArrayList<>();
        for (File jarFile : jarFiles) {
            try (JarFile jar = new JarFile(jarFile)) {
                JarEntry jsonEntry = jar.getJarEntry(MANIFEST_FILE);
                if (jsonEntry == null) {
                    log.warn("Skipping {}: missing {}", jarFile.getName(), MANIFEST_FILE);
                    continue;
                }

                PluginInfo info;
                try (InputStream is = jar.getInputStream(jsonEntry)) {
                    info = mapper.readValue(is, PluginInfo.class);
                }

                if (info.getId() == null || info.getId().isEmpty()) {
                    log.error("Plugin {} has no id, skipping", jarFile.getName());
                    continue;
                }
                if (info.getEntrypoint() == null || info.getEntrypoint().isEmpty()) {
                    log.error("Plugin {} has no entrypoint, skipping", jarFile.getName());
                    continue;
                }

                allEntries.add(new PluginEntry(jarFile, info));
                log.info("Discovered plugin: {} ({})", info.getName(), info.getId());
            } catch (IOException e) {
                log.error("Failed to read plugin jar {}: {}", jarFile.getName(), e.getMessage());
            }
        }

        if (allEntries.isEmpty()) {
            log.info("No valid plugins to load");
            setEmptyAuthProvider();
            return;
        }

        List<URL> urlList = new ArrayList<>();
        for (PluginEntry entry : allEntries) {
            try {
                urlList.add(entry.jarFile.toURI().toURL());
            } catch (MalformedURLException e) {
                log.error("Invalid URL for {}: {}", entry.jarFile.getName(), e.getMessage());
            }
        }
        sharedLoader = new URLClassLoader(urlList.toArray(new URL[0]), PluginManager.class.getClassLoader());

        Map<String, PluginInfo> pending = new LinkedHashMap<>();
        for (PluginEntry entry : allEntries) {
            pending.put(entry.info.getId(), entry.info);
        }

        Map<String, PluginInfo> loaded = new HashMap<>();
        Map<String, IPlugin> instances = new HashMap<>();

        boolean changed;
        do {
            changed = false;
            Iterator<Map.Entry<String, PluginInfo>> it = pending.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, PluginInfo> entry = it.next();
                String id = entry.getKey();
                PluginInfo info = entry.getValue();

                boolean depsOk = true;
                String[] deps = info.getDependencies();
                if (deps != null) {
                    for (String dep : deps) {
                        if (!checkDependency(dep, loaded)) {
                            depsOk = false;
                            break;
                        }
                    }
                }

                if (depsOk) {
                    try {
                        Class<?> mainClass = sharedLoader.loadClass(info.getEntrypoint());
                        if (!IPlugin.class.isAssignableFrom(mainClass)) {
                            log.error("Plugin {} main class does not implement IPlugin", id);
                            it.remove();
                            continue;
                        }

                        IPlugin instance = (IPlugin) mainClass.getDeclaredConstructor().newInstance();
                        instance.onEnable();

                        // Handle auth provider if present
                        if (info.getAuthProvider() != null && !info.getAuthProvider().isEmpty()) {
                            try {
                                Class<?> authClass = sharedLoader.loadClass(info.getAuthProvider());
                                if (AuthProvider.class.isAssignableFrom(authClass)) {
                                    authProviderClasses.put(id, authClass);
                                    log.info("Plugin {} provides auth provider class {}", id, info.getAuthProvider());
                                } else {
                                    log.error("Plugin {} auth provider class {} does not implement AuthProvider", id, info.getAuthProvider());
                                }
                            } catch (ClassNotFoundException e) {
                                log.error("Plugin {} auth provider class {} not found", id, info.getAuthProvider());
                            }
                        }

                        loaded.put(id, info);
                        instances.put(id, instance);
                        log.info("Successfully loaded plugin {} version {} by {}",
                                info.getName(), info.getVersion(), info.getAuthors());
                        changed = true;
                        it.remove();
                    } catch (Exception e) {
                        log.error("Failed to enable plugin {}: {}", id, e.getMessage(), e);
                        it.remove();
                    }
                }
            }
        } while (changed && !pending.isEmpty());

        if (!pending.isEmpty()) {
            for (PluginInfo info : pending.values()) {
                log.error("Plugin {} skipped due to unsatisfied dependencies", info.getId());
            }
        }

        loadedPlugins.putAll(loaded);
        pluginInstances.putAll(instances);

        // Determine and initialize the active auth provider
        initializeAuthProvider();
    }

    private static void initializeAuthProvider() {
        if (ConfigManager.getConfig().isDisableAuth()) return;
        String configuredId = ConfigManager.getConfig().getAuthProviderPluginId();
        if (configuredId != null && !configuredId.isEmpty()) {
            Class<?> authClass = authProviderClasses.get(configuredId);
            if (authClass == null) {
                log.error("Configured auth provider plugin id {} does not provide any auth provider", configuredId);
                return;
            }
            try {
                authProviderInstance = (AuthProvider) authClass.getDeclaredConstructor().newInstance();
                authProviderInstance.init();
                log.info("Initialized auth provider from plugin {}", configuredId);
            } catch (Exception e) {
                log.error("Failed to instantiate auth provider from plugin {}: {}", configuredId, e.getMessage(), e);
            }
        } else {
            if (authProviderClasses.isEmpty()) {
                log.info("No auth provider found");
            } else if (authProviderClasses.size() == 1) {
                Map.Entry<String, Class<?>> entry = authProviderClasses.entrySet().iterator().next();
                try {
                    authProviderInstance = (AuthProvider) entry.getValue().getDeclaredConstructor().newInstance();
                    authProviderInstance.init();
                    log.info("Initialized auth provider from plugin {}", entry.getKey());
                } catch (Exception e) {
                    log.error("Failed to instantiate auth provider from plugin {}: {}", entry.getKey(), e.getMessage(), e);
                }
            } else {
                log.error("Multiple auth providers found ({}), but no authProviderPluginId configured. No auth provider will be used.",
                        authProviderClasses.keySet());
            }
        }
        if (authProviderInstance == null) {
            setEmptyAuthProvider();
        }
    }

    private static void setEmptyAuthProvider() {
        log.warn("No auth provider initialized; auth is off!");
        authProviderInstance = new EmptyAuthProvider();
    }

    private static boolean checkDependency(String dep, Map<String, PluginInfo> loaded) {
        dep = dep.trim();
        String[] ops = {">=", "<=", "~=", ">", "<", "~", "="};
        String matchedOp = null;
        int opIndex = -1;
        for (String op : ops) {
            int idx = dep.indexOf(op);
            if (idx != -1 && (opIndex == -1 || idx < opIndex)) {
                opIndex = idx;
                matchedOp = op;
            }
        }
        if (matchedOp == null) {
            log.error("Invalid dependency format (no operator): {}", dep);
            return false;
        }

        String id = dep.substring(0, opIndex).trim();
        String constraint = dep.substring(opIndex);
        if (id.isEmpty()) {
            log.error("Empty plugin id in dependency: {}", dep);
            return false;
        }

        if (id.equalsIgnoreCase(CORE_ID)) {
            if (ManifestManager.getManifest() == null) {
                log.error("Core manifest not available");
                return false;
            }
            String coreVersion = ManifestManager.getManifest().metaMystiaVersion();
            if (coreVersion == null) {
                log.error("Core version not found in manifest");
                return false;
            }
            boolean ok = VersionValidators.isVersionValid(coreVersion, constraint);
            if (!ok) {
                log.error("Core version {} does not satisfy constraint {}", coreVersion, constraint);
            }
            return ok;
        } else {
            PluginInfo depInfo = loaded.get(id);
            if (depInfo == null) {
                log.error("Dependency plugin {} not loaded yet", id);
                return false;
            }
            String version = depInfo.getVersion();
            if (version == null) {
                log.error("Dependency plugin {} has no version", id);
                return false;
            }
            boolean ok = VersionValidators.isVersionValid(version, constraint);
            if (!ok) {
                log.error("Plugin {} version {} does not satisfy constraint {}", id, version, constraint);
            }
            return ok;
        }
    }

    public static PluginInfo getPlugin(String id) {
        return loadedPlugins.get(id);
    }

    public static IPlugin getPluginInstance(String id) {
        return pluginInstances.get(id);
    }

    public static Map<String, PluginInfo> getLoadedPlugins() {
        return new HashMap<>(loadedPlugins);
    }

    public static Map<String, IPlugin> getPluginInstances() {
        return new HashMap<>(pluginInstances);
    }

    public static AuthProvider getAuthProvider() {
        return authProviderInstance;
    }
}