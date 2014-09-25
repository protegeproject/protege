package org.protege.editor.core.update;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.protege.editor.core.plugin.PluginUtilities;
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
    private static final Logger logger = Logger.getLogger(PluginRegistryImpl.class);

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
        plugins = new ArrayList<PluginInfo>();
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
            if (logger.isDebugEnabled()) {
                logger.debug("Starting calculation of " + pluginType);
            }
            checkBundles();
            visit(root);
            sortPlugins();
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
                                if (logger.isDebugEnabled()) {
                                    logger.debug("Found self updating bundle " + info.getId());
                                }
                            }
                        }
                    }
                    catch (Throwable e) {
                        if (logger.isDebugEnabled()) {
                            logger.debug(bundle.getHeaders().get("Bundle-Name") + " self update failed: " + e.getMessage());
                        }
                    }
                }
            }
        }
        
        private void visit(URL node) {
            if (!visitedURLs.contains(node)){
                visitedURLs.add(node);
                
                if (logger.isDebugEnabled()) {
                    logger.debug("Examining node " + node);
                }

                // see if this is a plugin file
                try{
                    UpdateChecker checker = new UpdateChecker(node, null);
                    PluginInfo info = checker.run();
                    if (info != null) {
                        if (logger.isDebugEnabled()) {
                            logger.debug("Node " + node + "has valid Plugin Info: " + info.getId());
                        }
                        if (pluginType == PluginRegistryType.PLUGIN_DOWNLOAD_REGISTRY && !bundleByIds.containsKey(info.getId())) {
                            plugins.add(info);
                            if (logger.isDebugEnabled()) {
                                logger.debug("Node " + node + " is a download");
                            }
                        }
                        Bundle bundle = bundleByIds.get(info.getId());
                        if (pluginType == PluginRegistryType.PLUGIN_UPDATE_REGISTRY
                                      && bundle != null
                                      && bundle.getVersion().compareTo(info.getAvailableVersion()) < 0
                                      && !selfUpdatingBundleIds.contains(info.getId())) {
                            info.setPluginDescriptor(bundle);
                            plugins.add(info);
                            if (logger.isDebugEnabled()) {
                                logger.debug("Node " + node + " is a update");
                            }
                        }
                    }

                }
                catch(MalformedURLException e){
                    readRegistry(node);
                }
                catch (UpdateException e){
                    readRegistry(node);
                }
                catch(IOException e){
                    logger.debug("Cannot open remote plugin file/registry", e);
                }
            }
        }

        private void readRegistry(URL node) {
            if (logger.isDebugEnabled()) {
                logger.debug("Trying node " + node + " as a registry");
            }
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(new BufferedInputStream(node.openStream())));
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim();
                    if (logger.isDebugEnabled()) {
                        logger.debug("\treading line from node " + node + ":" + line);
                    }
                    if (line.length() > 0 && !line.startsWith("//")){
                        try{
                            URL url = new URL(line);
                            visit(url);
                        }
                        catch(MalformedURLException urlException){
                            logger.debug("Invalid URL in plugin registry: " + line);
                        }
                    }
                }
                reader.close();
            }
            catch (IOException ex) {
                logger.debug("Cannot open remote plugin registry", ex);
            }
        }

    }
}
