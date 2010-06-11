package org.protege.editor.core;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;

import java.io.File;
import java.util.*;

/*
 * I have left this a BundleActivator to emphasize that it could 
 * perfectly reasonably be the activator for a small management bundle of 
 * its own.
 */

public class BundleManager {
    private Logger logger = Logger.getLogger(BundleManager.class);

    public static final String BUNDLE_WITHOUT_PLUGIN_XML = "No-Plugin-XML";

    public static final String BUNDLE_DIR_PROP = "org.protege.plugin.dir";
    public static final String BUNDLE_EXTRA_PROP = "org.protege.plugin.extra";
    public final static char   BUNDLE_EXTRA_SEPARATOR = ':';
    public static final String OSGI_READS_DIRECTORIES = "org.protege.allow.directory.bundles";

    private BundleContext context;
    private List<Bundle> plugins = new ArrayList<Bundle>();
    
    public BundleManager(BundleContext context) {
        this.context = context;
    }

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
    public void loadPlugins() {
        installPlugins();
        startPlugins();
    }
    
    public void uninstallPlugins() {
        for (Bundle bundle : plugins) {
            try {
                bundle.uninstall();
            }
            catch (Throwable t) {
                if (logger.isDebugEnabled()) {
                    logger.debug("Exception caught uninstalling the bundle", t);
                }
            }
        }
    }

    private Map<String, Bundle> makeBundleMap() {
        Map<String, Bundle> bundleMap = new HashMap<String, Bundle>();
        for (Bundle b : context.getBundles()) {
            String location = b.getLocation();
            if (location != null) {
                bundleMap.put(location, b);
            }
        }
        return bundleMap;
    }
    
    private void installPlugins() {
        Map<String, Bundle> bundleMap = makeBundleMap();
        List<File> locations = new ArrayList<File>();
        locations.addAll(getPluginBundles());
        locations.addAll(getExtraBundles());
        locations.addAll(getNewExtraBundles());
        if (locations.isEmpty()) {
            logger.warn("No plugins found");
        }
        for (File plugin : locations) {
            Bundle b = null;
            try {
                String location = "file:" + plugin.getPath();
                Bundle alreadyInstalled = bundleMap.get(location);
                if (alreadyInstalled == null) {
                    b = context.installBundle(location);
                    logger.info("Installed plugin " + getNiceBundleName(b));
                }
                else {
                    b = alreadyInstalled;
                    logger.info("Loading plugin " + getNiceBundleName(b));
                }
                plugins.add(b);
            }
            catch (Throwable t) {
                if (isTrivialBundleLoadException(plugin, t)) {
                    logger.error("Could not install plugin in file/directory named " + plugin + ".  See the logs for more info.");
                    if (logger.isDebugEnabled()) {
                        logger.debug("Exception caught", t);
                    }
                }
                else {
                    logger.error("Could not install plugin in file/directory named " + plugin, t);
                }
            }
        }
    }
    
    private void startPlugins() {
        for (Bundle b : plugins) {
            if (b.getState() != Bundle.ACTIVE) {
                try {
                    b.start();
                    pluginSanityCheck(b);
                }
                catch (BundleException be) {
                    ProtegeApplication.getErrorLog().logError(be);
                    logger.warn("Problem starting plugin " + getNiceBundleName(b));
                }
            }
        }
    }

    private List<File> getPluginBundles() {
        ArrayList<File> pluginBundles = new ArrayList<File>();
        String dir_name = System.getProperty(BUNDLE_DIR_PROP);
        if  (dir_name != null) {
            File dir = new File(dir_name);
            if (dir.exists() && dir.isDirectory()) {
                for (File f : dir.listFiles()) pluginBundles.add(f);
            }
            else {
                logger.error("Plugin directory " + dir_name + " is invalid");
            }
        }
        return pluginBundles;
    }

    private List<File> getExtraBundles() {
        String remaining = System.getProperty(BUNDLE_EXTRA_PROP);
        List<File> extra_bundles = new ArrayList<File>();
        while (remaining != null && remaining.length() != 0) {
            int index = remaining.indexOf(File.pathSeparator);
            if (index < 0) {
                extra_bundles.add(new File(remaining));
                return extra_bundles;
            }
            else {
                extra_bundles.add(new File(remaining.substring(0, index)));
                remaining = remaining.substring(index+1);
            }
        }
        return extra_bundles;
    }
    
    private List<File> getNewExtraBundles() {
        List<File> pluginLocations = new ArrayList<File>();
        String location;
        for (int i = 0; (location = System.getProperty(BUNDLE_EXTRA_PROP + "." + i)) != null; i++) {
            pluginLocations.add(new File(location));
        }
        return pluginLocations;
    }
    
    private boolean isTrivialBundleLoadException(File plugin, Throwable t) {
        return plugin.getName().equals(".DS_Store");
    }
    
    private void pluginSanityCheck(Bundle b) {
        boolean hasPluginXml = (b.getResource("/plugin.xml") != null);
        if (b.getHeaders().get(BUNDLE_WITHOUT_PLUGIN_XML) == null && !hasPluginXml) {
            logger.info("\t" + getNiceBundleName(b) + " Plugin has no plugin.xml resource");
        }
        if (hasPluginXml && !isSingleton(b)) {
            logger.warn("\t" + getNiceBundleName(b) + " plugin is not a singleton so its plugin.xml will not be seen by the registry." );
        }

    }
    
    public static boolean isSingleton(Bundle b) {
        StringBuffer singleton = new StringBuffer(Constants.SINGLETON_DIRECTIVE);
        singleton.append(":=true");
        return ((String) b.getHeaders().get(Constants.BUNDLE_SYMBOLICNAME)).contains(singleton.toString());
    }
    
    public static String getNiceBundleName(Bundle b) {
        String name = (String) b.getHeaders().get(Constants.BUNDLE_NAME);
        if (name == null) {
            name = b.getSymbolicName();
        }
        return name;
    }

    public List<Bundle> getPlugins() {
        return Collections.unmodifiableList(plugins);
    }
}
