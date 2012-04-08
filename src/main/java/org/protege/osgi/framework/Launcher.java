package org.protege.osgi.framework;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.xml.sax.SAXException;


public class Launcher {    
	private Logger logger = Logger.getLogger(Launcher.class.getCanonicalName());
    public static final String ARG_PROPERTY = "command.line.arg.";
    public static final String LAUNCH_LOCATION_PROPERTY = "org.protege.launch.config";
    public static final String PROTEGE_DIR_PROPERTY = "protege.dir";
    public static String PROTEGE_DIR = System.getProperty(PROTEGE_DIR_PROPERTY);

    private Map<String, String> frameworkProperties;
    private List<BundleSearchPath> searchPaths;
    private String     factoryClass;
    private Framework  framework;
    private File frameworkDir;

    
    public Launcher(File config) throws IOException, ParserConfigurationException, SAXException {
        parseConfig(config);
        locateOSGi();
        frameworkDir = new File(System.getProperty("java.io.tmpdir"), "ProtegeCache-" + UUID.randomUUID().toString());
        frameworkProperties.put("org.osgi.framework.storage", frameworkDir.getCanonicalPath());
        frameworkProperties.put("org.osgi.framework.startlevel.beginning", "" + searchPaths.size());
    }
    
    public Framework getFramework() {
        return framework;
    }
    
    private void parseConfig(File config) throws ParserConfigurationException, SAXException, IOException {
        Parser p = new Parser();
        p.parse(config);
        setSystemProperties(p);
        searchPaths = p.getSearchPaths();
        frameworkProperties = p.getFrameworkProperties();
    }
    
    private void locateOSGi() throws IOException {
        BufferedReader factoryReader = new BufferedReader(new InputStreamReader(
             getClass().getClassLoader().getResourceAsStream("META-INF/services/org.osgi.framework.launch.FrameworkFactory")));
        factoryClass = factoryReader.readLine();
        factoryClass = factoryClass.trim();
        factoryReader.close();
    }
    
    private void setSystemProperties(Parser p) {
        Map<String, String> systemProperties = p.getSystemProperties();
        System.setProperty("org.protege.osgi.launcherHandlesExit", "True");
        for (Entry<String, String> entry : systemProperties.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }

    public void start(final boolean exitOnOSGiShutDown) throws InstantiationException, IllegalAccessException, ClassNotFoundException, BundleException, IOException, InterruptedException {
        FrameworkFactory factory = (FrameworkFactory) Class.forName(factoryClass).newInstance();
        framework = factory.newFramework(frameworkProperties);
        framework.init();
        BundleContext context = framework.getBundleContext();
        List<Bundle> bundles = new ArrayList<Bundle>();
        int startLevel = 1;
        for (BundleSearchPath searchPath : searchPaths) {
            bundles.addAll(installBundles(context, null, searchPath, startLevel++));
        }
        startBundles(context, bundles);
        framework.start();

        Thread shutdownThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    framework.waitForStop(0);
                    cleanup();
                    if (exitOnOSGiShutDown) {
                        System.exit(0);
                    }
                }
                catch (Throwable t) {
                    t.printStackTrace();
                }
            }
        }, "OSGi Shutdown Thread");
        shutdownThread.start();
    }

    private List<Bundle> installBundles(BundleContext context, String dir, BundleSearchPath searchPath, int startLevel) throws BundleException {
    	Collection<File> bundles = searchPath.search();
        List<Bundle> core = new ArrayList<Bundle>();
        for (File bundleFile :  bundles) {
            try {
            	Bundle newBundle = context.installBundle(bundleFile.getAbsoluteFile().toURI().toString());
            	// the cast to BundleStartLevel is not needed in Java 6 but it is in Java 7
            	((BundleStartLevel) newBundle.adapt(BundleStartLevel.class)).setStartLevel(startLevel);
                core.add(newBundle);
            }
            catch (Throwable t) {
            	logger.log(Level.WARNING, "Bundle " + bundleFile + " failed to install.", t);
            }
        }
        return core;
    }
    
    private void startBundles(BundleContext context, List<Bundle> bundles) throws BundleException {
        for (Bundle b : bundles) {
            try {
                b.start();
            }
            catch (Throwable t) {
            	logger.log(Level.WARNING, "Core Bundle " + b.getBundleId() + " failed to start.", t);
            }
        }
    }
    
    protected void cleanup() {
        delete(frameworkDir);
    }

    private void delete(File f) {
        if (f.isDirectory()) {
            for (File child : f.listFiles()) {
                delete(child);
            }
        }
        f.delete();
    }
    
    public static void setArguments(String... args) {
        if (args != null) {
            int counter = 0;
            for (String arg : args) {
                System.setProperty(ARG_PROPERTY + (counter++), arg);
            }
        }        
    }
    
    /**
    ddd * @param args
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws IOException 
     * @throws BundleException 
     * @throws ClassNotFoundException 
     * @throws IllegalAccessException 
     * @throws InstantiationException 
     */
    public static void main(String[] args) throws Exception {
        setArguments(args);
        String config = System.getProperty(LAUNCH_LOCATION_PROPERTY, "config.xml");
        File configFile;
        if (PROTEGE_DIR != null) {
            configFile = new File(PROTEGE_DIR, config);
        }
        else {
            configFile = new File(config);
        }
        Launcher launcher = new Launcher(configFile);
        launcher.start(true);
    }
    
}
