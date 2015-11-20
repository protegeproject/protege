package org.protege.editor.core.update;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Version;
import org.protege.editor.core.log.LogBanner;
import org.protege.editor.core.plugin.PluginUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Nov 5, 2008<br><br>
 */
public class PluginRegistryImpl implements PluginRegistry {

    public static final Marker AUTO_UPDATE = MarkerFactory.getMarker("Auto-Update");

    private static final Logger logger = LoggerFactory.getLogger(PluginRegistryImpl.class);

    public static final String UPDATE_URL = "Update-Url";

    public enum PluginRegistryType {
        PLUGIN_UPDATE_REGISTRY("Updates"), PLUGIN_DOWNLOAD_REGISTRY("Downloads");

        private String label;

        private PluginRegistryType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
    }

    private final URL root;

    private final List<PluginInfo> updates = new ArrayList<>();

    private final List<PluginInfo> installs = new ArrayList<>();

    public PluginRegistryImpl(URL root) {
        this.root = root;
    }

    public void reload() {
        updates.clear();
        installs.clear();
        Calculator calculator = new Calculator(root);
        calculator.run();
        updates.addAll(calculator.getUpdates());
        installs.addAll(calculator.getInstalls());
    }

    public List<PluginInfo> getAvailableUpdates() {
        return updates;
    }

    public List<PluginInfo> getAvailableInstalls() {
        return installs;
    }

    private static void sortPlugins(List<PluginInfo> plugins) {
        Collections.sort(plugins, new Comparator<PluginInfo>() {
            public int compare(PluginInfo o1, PluginInfo o2) {
                final String l1 = o1.getLabel();
                final String l2 = o2.getLabel();
                return l1.compareToIgnoreCase(l2);
            }
        });
    }

//    public boolean isSelected(PluginInfo download) {
//        return pluginType == PluginRegistryType.PLUGIN_UPDATE_REGISTRY;
//    }

    private static class Calculator {

        private URL root;

        private BundleContext context = PluginUtilities.getInstance().getApplicationContext();

        private Map<String, Bundle> bundleByIds = new HashMap<>();

        private Set<String> selfUpdatingBundleIds = new HashSet<>();

        private Set<URL> visitedURLs = new HashSet<>();

        private final List<PluginInfo> updates = new ArrayList<>();

        private final List<PluginInfo> installs = new ArrayList<>();

        public Calculator(URL root) {
            this.root = root;
        }


        public void run() {
            logger.info(LogBanner.start("Running Auto-update"));
            logger.info("");
            mapIdsToBundles();
            checkBundles();
            checkForDownloads(root);
            sortPlugins(updates);
            sortPlugins(installs);
            logger.info(LogBanner.end());

        }

        private void mapIdsToBundles() {
            if (context == null) {
                return;
            }
            for (Bundle bundle : context.getBundles()) {
                bundleByIds.put(bundle.getSymbolicName(), bundle);
            }
        }

        public List<PluginInfo> getUpdates() {
            return new ArrayList<>(updates);
        }

        public List<PluginInfo> getInstalls() {
            return new ArrayList<>(installs);
        }

        private void checkBundles() {
            if (context == null) {
                return;
            }
            logger.info("--- Checking for updates to installed plugins ---");
            for (Bundle bundle : context.getBundles()) {
                checkForUpdateToBundle(bundle, 0);
            }
            logger.info("");

        }

        private void checkForDownloads(URL root) {
            logger.info("--- Searching plugins to install ---");
            processUpdateOrRepositoryDocumentAt(root, 0);
        }

        private void checkForUpdateToBundle(Bundle bundle, int depth) {
            String updateLocation = (String) bundle.getHeaders().get(UPDATE_URL);
            if (updateLocation == null) {
                return;
            }
            try {
                URL url = new URL(updateLocation);
                Optional<Bundle> optionalBundle = Optional.ofNullable(bundle);
                UpdateChecker checker = new UpdateChecker(url, optionalBundle);
                Optional<PluginInfo> pluginInfo = checker.run();
                if (pluginInfo.isPresent()) {
                    PluginInfo info = pluginInfo.get();
                    Version installedVersion = bundle.getVersion();
                    Version availableVersion = info.getAvailableVersion();
                    if (availableVersion.compareTo(installedVersion) > 0) {
                        updates.add(info);
                        selfUpdatingBundleIds.add(info.getId());
                        logger.info(AUTO_UPDATE, "{}Found update for {}.  Installed version: {}  Available version: {}",
                                pad(depth + 1),
                                info.getId(),
                                installedVersion,
                                availableVersion);
                    }
                }
            } catch (PluginDocumentParseException e) {
                Object bundleName = bundle.getHeaders().get("Bundle-Name");
                logger.warn(AUTO_UPDATE, "The plugin update document for the {} plugin, " +
                        "which is located at {} could not be loaded/processed. Reason: {}",
                        bundleName,
                        updateLocation,
                        e.getMessage());
            } catch (MalformedURLException e) {
                logger.warn(AUTO_UPDATE, "The URL of the plugin document {} is malformed: {}",
                        updateLocation,
                        e.getMessage());
            }
        }

        private void processUpdateOrRepositoryDocumentAt(URL node, int depth) {
            if (visitedURLs.contains(node)) {
                return;
            }
            visitedURLs.add(node);
            logger.info(AUTO_UPDATE, "{}Checking {}", pad(depth), node);

            // see if this is a plugin file
            UpdateChecker checker = new UpdateChecker(node, Optional.empty());
            try {
                Optional<PluginInfo> parsedInfo = checker.run();
                if (parsedInfo.isPresent()) {
                    PluginInfo info = parsedInfo.get();
                    logger.debug(AUTO_UPDATE, "{}URL {} has valid plugin info: {}", pad(depth), node, info.getId());
                    if (!bundleByIds.containsKey(info.getId())) {
                        installs.add(info);
                        logger.debug(AUTO_UPDATE, "{}URL {} is a download", pad(depth), node);

                    }
                    Bundle bundle = bundleByIds.get(info.getId());
                    if (bundle != null && bundle.getVersion().compareTo(info.getAvailableVersion()) < 0 && !selfUpdatingBundleIds.contains(info.getId())) {
                        info.setPluginDescriptor(bundle);
                        updates.add(info);
                        logger.debug(AUTO_UPDATE, "{}URL {} is an update", pad(depth), node);
                    }
                }
            } catch (PluginDocumentParseException e) {
                readRegistry(node, depth + 1);
            }
        }

        private void readRegistry(URL node, int depth) {
            logger.info(AUTO_UPDATE, "{}Processing {} as a plugin registry", pad(depth), node);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(node.openStream())));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (logger.isDebugEnabled()) {
                        logger.debug(AUTO_UPDATE, "{}   reading line from node " + node + ":" + line, pad(depth));
                    }
                    if (line.length() > 0 && !line.startsWith("//")) {
                        try {
                            URL url = new URL(line);
                            processUpdateOrRepositoryDocumentAt(url, depth + 1);
                        } catch (MalformedURLException urlException) {
                            logger.debug(AUTO_UPDATE, "{}    Invalid URL in plugin registry: " + line, pad(depth));
                        }
                    }
                }
                reader.close();
            } catch (IOException ex) {
                logger.warn(AUTO_UPDATE, "{}    Cannot open remote plugin registry at {}.  Reason: {}", pad(depth), ex.getMessage(), ex);
            }
        }
    }


    private static String pad(int depth) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            sb.append("    ");
        }
        return sb.toString();
    }
}
