package org.protege.editor.core.update;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
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
    private final Logger logger = LoggerFactory.getLogger(PluginRegistryImpl.class);

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

    private URL root;
    private PluginRegistryType pluginType;

    private List<PluginInfo> plugins = null;


    public PluginRegistryImpl(URL root, PluginRegistryType pluginType) {
        this.root = root;
        this.pluginType = pluginType;
    }
    
    public void reload() {
        plugins = new ArrayList<>();
        new Calculator().run();
    }

    public List<PluginInfo> getAvailableDownloads() {
        if (plugins == null){
            reload();
        }
        return plugins;
    }


    public boolean isSelected(PluginInfo download) {
        return pluginType == PluginRegistryType.PLUGIN_UPDATE_REGISTRY;
    }
    
    private class Calculator implements Runnable {
        private BundleContext context = PluginUtilities.getInstance().getApplicationContext();
        private Map<String, Bundle> bundleByIds = new HashMap<String, Bundle>();
        private Set<String> selfUpdatingBundleIds = new HashSet<String>();
        private Set<URL> visitedURLs = new HashSet<URL>();

        public void run() {
            logger.info(LogBanner.start("Running Auto-update"));
            checkBundles();
            visit(root);
            sortPlugins();
            logger.info(LogBanner.end());
        }

        private void sortPlugins() {
            Collections.sort(plugins, new Comparator<PluginInfo>() {
                public int compare(PluginInfo o1, PluginInfo o2) {
                    final String l1 = o1.getLabel();
                    final String l2 = o2.getLabel();
                    return l1.compareToIgnoreCase(l2);
                }
            });
        }

        private void checkBundles() {
            if (context != null) {
                for (Bundle bundle : context.getBundles()) {
                    bundleByIds.put(bundle.getSymbolicName(), bundle);
                }
            }
            if (pluginType == PluginRegistryType.PLUGIN_UPDATE_REGISTRY) {
                for (Bundle bundle : context.getBundles()) {
                    try {
                        String updateLocation = (String) bundle.getHeaders().get(UPDATE_URL);
                        if (updateLocation != null) {
                            URL url = new URL(updateLocation);
                            UpdateChecker checker = new UpdateChecker(url, bundle);
                            PluginInfo info = checker.run();
                            if (info != null && info.getAvailableVersion().compareTo(bundle.getVersion()) > 0) {
                                info.setPluginDescriptor(bundle);
                                plugins.add(info);
                                selfUpdatingBundleIds.add(info.getId());
                                logger.debug(AUTO_UPDATE, "Found self updating bundle {}", info.getId());
                            }
                        }
                    }
                    catch (Throwable e) {
                        logger.warn(AUTO_UPDATE, "Self update of bundle {} failed.  Reason: {}", bundle.getHeaders().get("Bundle-Name"), e.getMessage(), e);
                    }
                }
            }
        }
        
        private void visit(URL node) {
            if (!visitedURLs.contains(node)){
                visitedURLs.add(node);
                
                logger.info(AUTO_UPDATE, "Checking {}", node);

                // see if this is a plugin file
                try{
                    UpdateChecker checker = new UpdateChecker(node, null);
                    PluginInfo info = checker.run();
                    if (info != null) {
                        logger.debug(AUTO_UPDATE, "URL {} has valid plugin info: {}", node, info.getId());

                        if (pluginType == PluginRegistryType.PLUGIN_DOWNLOAD_REGISTRY && !bundleByIds.containsKey(info.getId())) {
                            plugins.add(info);
                            logger.debug(AUTO_UPDATE, "URL {} is a download", node);

                        }
                        Bundle bundle = bundleByIds.get(info.getId());
                        if (pluginType == PluginRegistryType.PLUGIN_UPDATE_REGISTRY
                                      && bundle != null
                                      && bundle.getVersion().compareTo(info.getAvailableVersion()) < 0
                                      && !selfUpdatingBundleIds.contains(info.getId())) {
                            info.setPluginDescriptor(bundle);
                            plugins.add(info);
                            logger.debug(AUTO_UPDATE, "URL {} is an update", node);
                        }
                    }

                }
                catch(MalformedURLException e){
                    readRegistry(node);
                }
                catch (UpdateException e){
                    if (e.getPluginId().isPresent()) {
                        logger.warn(AUTO_UPDATE, "Couldn't read plugin updated file at {}.  Reason: {}", node, e.getMessage(), e);
                    }
                    readRegistry(node);
                }
                catch(IOException e){
                    logger.warn(AUTO_UPDATE, "Cannot open remote plugin file/registry at {}.  Reason: {}", node, e.getCause(), e);
                }
            }
        }

        private void readRegistry(URL node) {
            logger.debug(AUTO_UPDATE, "Attempting to process info at {} as a plugin registry", node);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(node.openStream())));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (logger.isDebugEnabled()) {
                        logger.debug(AUTO_UPDATE, "\treading line from node " + node + ":" + line);
                    }
                    if (line.length() > 0 && !line.startsWith("//")){
                        try{
                            URL url = new URL(line);
                            visit(url);
                        }
                        catch(MalformedURLException urlException){
                            logger.debug(AUTO_UPDATE, "Invalid URL in plugin registry: " + line);
                        }
                    }
                }
                reader.close();
            }
            catch (IOException ex) {
                logger.warn(AUTO_UPDATE, "Cannot open remote plugin registry at {}.  Reason: {}", ex.getMessage(), ex);
            }
        }

    }
}
