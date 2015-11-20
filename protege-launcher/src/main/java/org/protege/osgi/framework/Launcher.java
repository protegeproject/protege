package org.protege.osgi.framework;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.framework.startlevel.BundleStartLevel;
import org.osgi.framework.wiring.BundleRevision;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;


public class Launcher {

    public static final String ARG_PROPERTY = "command.line.arg.";

    public static final String LAUNCH_LOCATION_PROPERTY = "org.protege.launch.config";

    public static final String PROTEGE_DIR_PROPERTY = "protege.dir";

    public static final String DEFAULT_CONFIG_XML_FILE_PATH_NAME = "conf/config.xml";

    public static String PROTEGE_DIR = System.getProperty(PROTEGE_DIR_PROPERTY);

    private final Logger logger = LoggerFactory.getLogger(Launcher.class.getCanonicalName());

    private final Map<String, String> frameworkProperties = new HashMap<>();

    private final List<BundleSearchPath> searchPaths = new ArrayList<>();

    private final File frameworkDir;

    private final String factoryClass;

    private Framework framework;


    public Launcher(File config) throws IOException, ParserConfigurationException, SAXException {
        parseConfig(config);
        factoryClass = locateOSGi();
        frameworkDir = new File(System.getProperty("java.io.tmpdir"), "ProtegeCache-" + UUID.randomUUID().toString());
        frameworkProperties.put(Constants.FRAMEWORK_STORAGE, frameworkDir.getCanonicalPath());
        frameworkProperties.put(Constants.FRAMEWORK_BEGINNING_STARTLEVEL, Integer.toString(searchPaths.size()));
    }

    public Framework getFramework() {
        return framework;
    }

    private void parseConfig(File config) throws ParserConfigurationException, SAXException, IOException {
        Parser p = new Parser();
        p.parse(config);
        setSystemProperties(p);
        setLogger(frameworkProperties);
        searchPaths.addAll(p.getSearchPaths());
        frameworkProperties.putAll(p.getFrameworkProperties());
    }

    private static String locateOSGi() throws IOException {
        InputStream frameworkFactory = Launcher.class.getClassLoader()
                .getResourceAsStream("META-INF/services/org.osgi.framework.launch.FrameworkFactory");
        try (BufferedReader factoryReader = new BufferedReader(new InputStreamReader(frameworkFactory))) {
            return factoryReader.readLine().trim();
        }
    }

    private void setSystemProperties(Parser p) {
        Map<String, String> systemProperties = p.getSystemProperties();
        System.setProperty("org.protege.osgi.launcherHandlesExit", "True");
        for (Entry<String, String> entry : systemProperties.entrySet()) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }

    /**
     * Sets the default felix logger so that logging output is redirected to SLF4J instead of stderr and stdout
     * @param configurationMap The configuration map.  Note that the framework factory newFramework method expects
     *                         a map that maps Strings to Strings.  However, the documentation for the Felix
     *                         configuration properties specifies that the config value of the felix.log.logger
     *                         property must be an instance of Logger.  This method therefore makes an unchecked
     *                         call to Map.put(), which works.
     */
    @SuppressWarnings("unchecked")
    private static void setLogger(Map configurationMap) {
        FrameworkSlf4jLogger logger = new FrameworkSlf4jLogger();
        configurationMap.put("felix.log.logger", logger);
    }

    public void start(final boolean exitOnOSGiShutDown) throws InstantiationException, IllegalAccessException, ClassNotFoundException, BundleException, IOException, InterruptedException {
        printBanner();
        logger.info("----------------- Initialising and Starting the OSGi Framework -----------------");
        logger.info("FrameworkFactory Class: {}", factoryClass);
        logger.info("");
        
        FrameworkFactory factory = (FrameworkFactory) Class.forName(factoryClass).newInstance();

        framework = factory.newFramework(frameworkProperties);
        framework.init();
        logger.info("The OSGi framework has been initialised");
        BundleContext context = framework.getBundleContext();
        List<Bundle> bundles = new ArrayList<>();
        int startLevel = 1;
        for (BundleSearchPath searchPath : searchPaths) {
            bundles.addAll(installBundles(context, searchPath, startLevel++));
        }
        startBundles(bundles);
        try {
            framework.start();
            logger.info("The OSGi framework has been started");
            logger.info("");
        } catch (BundleException e) {
            logger.error("An error occurred when starting the OSGi framework: {}", e.getMessage(), e);
        }
        addShutdownHook();
        addCleanupOnExit(exitOnOSGiShutDown);

    }



    private void addShutdownHook() {
        Thread hook = new Thread(() -> {
            try {
                if (framework.getState() == Bundle.ACTIVE) {
                    framework.stop();
                    framework.waitForStop(0);
                }
            } catch (Throwable t) {
                logger.error("Error shuting down OSGi session: {}", t);
            }
        }, "Close OSGi Session");
        Runtime.getRuntime().addShutdownHook(hook);
    }

    private void addCleanupOnExit(final boolean exitOnOSGiShutDown) {
        Thread shutdownThread = new Thread(() -> {
            try {
                framework.waitForStop(0);
                cleanup();
                if (exitOnOSGiShutDown) {
                    System.exit(0);
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }, "OSGi Shutdown Thread");
        shutdownThread.start();
    }

    private List<Bundle> installBundles(BundleContext context, BundleSearchPath searchPath, int startLevel) throws BundleException {
        Collection<File> bundles = searchPath.search();
        List<Bundle> core = new ArrayList<>();
        for (File bundleFile : bundles) {
            try {
                String bundleURI = bundleFile.getAbsoluteFile().toURI().toString();
                logger.debug("Installing bundle.  StartLevel: {}; Bundle: {}", startLevel, bundleFile.getAbsolutePath());
                Bundle newBundle = context.installBundle(bundleURI);
                // the cast to BundleStartLevel is not needed in Java 6 but it is in Java 7
                ((BundleStartLevel) newBundle.adapt(BundleStartLevel.class)).setStartLevel(startLevel);
                core.add(newBundle);
            } catch (Throwable t) {
                logger.warn("Bundle {} failed to install: {}", bundleFile, t);
            }
        }
        return core;
    }

    private void startBundles(List<Bundle> bundles) throws BundleException {
        logger.info("------------------------------- Starting Bundles -------------------------------");
        for (Bundle b : bundles) {
            try {
                if (!isFragmentBundle(b)) {
                    b.start();
                    logger.info("Starting bundle {}", b.getSymbolicName());
                }
                else {
                    logger.info("Not starting bundle {} explicitly because it is a fragment bundle.", b.getSymbolicName());
                }
            } catch (Throwable t) {
                logger.error("Core Bundle {} failed to start: {}", b.getBundleId(), t);
            }
        }
        logger.debug("-------------------------------------------------------------------------------");
    }

    private static boolean isFragmentBundle(Bundle b) {
        return (b.adapt(BundleRevision.class).getTypes() & BundleRevision.TYPE_FRAGMENT) != 0;
    }

    protected void cleanup() {
        delete(frameworkDir);
    }

    private void delete(File f) {
        if (f.isDirectory()) {
            File[] files = f.listFiles();
            if (files != null) {
                for (File child : files) {
                    delete(child);
                }
            }
        }
        if (!f.delete()) {
            logger.warn("File could not be deleted ({})", f.getAbsolutePath());
        }
    }

    public static void setArguments(String... args) {
        if (args != null) {
            int counter = 0;
            for (String arg : args) {
                System.setProperty(ARG_PROPERTY + (counter++), arg);
            }
        }
    }

    private void printBanner() {
        logger.info("********************************************************************************");
        logger.info("**                                  Protégé                                   **");
        logger.info("********************************************************************************");
        logger.info("");
    }

    public static void main(String[] args) throws Exception {
        setArguments(args);
        String config = System.getProperty(LAUNCH_LOCATION_PROPERTY, DEFAULT_CONFIG_XML_FILE_PATH_NAME);
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
