package org.protege.editor.core.update;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.protege.editor.core.plugin.PluginUtilities;
/*
* Copyright (C) 2007, University of Manchester
*
* Modifications to the initial code base are copyright of their
* respective authors, or their employers as appropriate.  Authorship
* of the modifications may be determined from the ChangeLog placed at
* the end of this file.
*
* This library is free software; you can redistribute it and/or
* modify it under the terms of the GNU Lesser General Public
* License as published by the Free Software Foundation; either
* version 2.1 of the License, or (at your option) any later version.

* This library is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
* Lesser General Public License for more details.

* You should have received a copy of the GNU Lesser General Public
* License along with this library; if not, write to the Free Software
* Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
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
        }
        
        private void checkBundles() {
            for (Bundle bundle : context.getBundles()) {
                bundleByIds.put(bundle.getSymbolicName(), bundle);
                if (pluginType == PluginRegistryType.PLUGIN_UPDATE_REGISTRY) {
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
