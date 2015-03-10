package org.protege.editor.core;

import java.awt.*;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.List;

import javax.swing.*;
import javax.swing.border.MatteBorder;

import org.apache.log4j.Logger;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkEvent;
import org.osgi.framework.FrameworkListener;
import org.osgi.framework.Version;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;
import org.protege.editor.core.editorkit.EditorKitManager;
import org.protege.editor.core.editorkit.RecentEditorKitManager;
import org.protege.editor.core.platform.OSGi;
import org.protege.editor.core.platform.OSUtils;
import org.protege.editor.core.platform.PlatformArguments;
import org.protege.editor.core.platform.apple.ProtegeAppleApplication;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.error.ErrorLog;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.core.ui.progress.BackgroundTaskManager;
import org.protege.editor.core.ui.tabbedpane.CloseableTabbedPaneUI;
import org.protege.editor.core.ui.util.ProtegePlasticTheme;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.core.update.PluginManager;

import com.jgoodies.looks.plastic.PlasticLookAndFeel;

/*
 * Copyright (C) 2007, University of Manchester
 *
 *
 */

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 15, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * <p/>
 * The ProtegeApplication is the main entry point to Protege.  The application
 * is actually a plugin to the Java Plugin Framework
 */
public class ProtegeApplication implements BundleActivator {

    private static final Logger logger = Logger.getLogger(ProtegeApplication.class);
    
    public static final String BUNDLE_WITHOUT_PLUGIN_XML = "No-Plugin-XML";
    
    public static final String BUNDLE_DIR_PROP = "org.protege.plugin.dir";

    public static final String RUN_ONCE = "PROTEGE_OSGI_RUN_ONCE";

    public static final String ID = "org.protege.editor.core.application";

    public static final String LOOK_AND_FEEL_KEY = "LOOK_AND_FEEL_KEY";

    public static final String LOOK_AND_FEEL_CLASS_NAME = "LOOK_AND_FEEL_CLASS_NAME";

    private static BundleContext context;

    private List<URI> commandLineURIs;

    private static ErrorLog errorLog = new ErrorLog();

    private static BackgroundTaskManager backgroundTaskManager = new BackgroundTaskManager();

    private static boolean quitting = false;

    public void start(final BundleContext context) {
    	
    	context.addFrameworkListener(new FrameworkListener() {
    		@Override
    		public void frameworkEvent(FrameworkEvent event) {
    			if (event.getType() == FrameworkEvent.STARTED) {
    				reallyStart(context);
    			}

    		}
    	});

    }
    
    public void reallyStart(BundleContext context) {
    	try {
    		ProtegeApplication.context = context;
    		displayPlatform();
    		initApplication();


    		if (OSUtils.isOSX()) {
    			ProtegeAppleApplication.getInstance();
    		}

    		ProtegeManager.getInstance().initialise(this);
    		startApplication();


    	}
    	catch (Throwable t) {
    		logger.error("Exception caught starting Protege", t);
    	}
    }


    // Called when the application is finally completely shutting down
    public void stop(BundleContext context) throws Exception {
        BookMarkedURIManager.getInstance().dispose();
        RecentEditorKitManager.getInstance().save();
        RecentEditorKitManager.getInstance().dispose();
        PluginUtilities.getInstance().dispose();
        ProtegeManager.getInstance().dispose();
    }


    /////////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation of ApplicationPlugin
    //
    /////////////////////////////////////////////////////////////////////////////////


    // If this isn't liked info can be replaced with debug.
    // It helps with diagnosing problems with the FaCT++ plugin.
    private void displayPlatform() {
        Bundle thisBundle = context.getBundle();
        Version v = PluginUtilities.getBundleVersion(thisBundle);
        logger.info("Starting Protege Desktop (Version " + v.getMajor() + "." + v.getMinor() + "." + v.getMicro() + ", Build = " + v.getQualifier() + ")");
        logger.info("Platform:");
        logger.info("    Java: JVM " + System.getProperty("java.runtime.version") +
                " Memory: " + (Runtime.getRuntime().maxMemory() / 1000000) + "M");
        logger.info("    Language: " + Locale.getDefault().getLanguage() +
                ", Country: " + Locale.getDefault().getCountry());
        logger.info("    Framework: " + context.getProperty(Constants.FRAMEWORK_VENDOR) + " (" + context.getProperty(Constants.FRAMEWORK_VERSION) + ")");
        logger.info("    OS: " + context.getProperty(Constants.FRAMEWORK_OS_NAME) + " (" + context.getProperty(Constants.FRAMEWORK_OS_VERSION) + ")");
        logger.info("    Processor: " + context.getProperty(Constants.FRAMEWORK_PROCESSOR));
        for (Bundle plugin : context.getBundles()) {
        	if (isPlugin(plugin)) {
        		logger.info("Plugin: " + getNiceBundleName(plugin) + " (" + plugin.getVersion() + ")");
        	}
        }
        for (Bundle plugin : context.getBundles()) {
        	if (isPlugin(plugin)) {
        		pluginSanityCheck(plugin);
        	}
        }
    }
    
    
    private boolean pluginSanityCheck(Bundle b) {
    	boolean passed = true;
        boolean hasPluginXml = (b.getResource("/plugin.xml") != null);
        if (b.getHeaders().get(BUNDLE_WITHOUT_PLUGIN_XML) == null && !hasPluginXml) {
            logger.info("\t" + getNiceBundleName(b) + " Plugin has no plugin.xml resource");
            passed = false;
        }
        if (hasPluginXml && !isSingleton(b)) {
            logger.warn("\t" + getNiceBundleName(b) + " plugin is not a singleton so its plugin.xml will not be seen by the registry." );
            passed = false;
        }
        return passed;
    }
    
    public static boolean isPlugin(Bundle b) {
    	String location = b.getLocation();
    	return location != null && location.contains("plugin");
    }
    
    public static boolean isSingleton(Bundle b) {
        StringBuffer singleton1 = new StringBuffer(Constants.SINGLETON_DIRECTIVE);
        singleton1.append(":=true");
        StringBuffer singleton2 = new StringBuffer(Constants.SINGLETON_DIRECTIVE);
        singleton2.append(":=\"true\"");
        return ((String) b.getHeaders().get(Constants.BUNDLE_SYMBOLICNAME)).contains(singleton1.toString()) ||
                ((String) b.getHeaders().get(Constants.BUNDLE_SYMBOLICNAME)).contains(singleton2.toString());
    }
    
    public static String getNiceBundleName(Bundle b) {
        String name = (String) b.getHeaders().get(Constants.BUNDLE_NAME);
        if (name == null) {
            name = b.getSymbolicName();
        }
        return name;
    }

    protected ProtegeApplication initApplication() throws Exception {
        PluginUtilities.getInstance().initialise(context);
        loadDefaults();
        initializeLookAndFeel();
        checkConfiguration();
        setupExceptionHandler();
        processCommandLineURIs();  // plugins may set arguments
        loadRecentEditorKits();
        return this;
    }


    private static void loadDefaults() {
        ProtegeProperties.getInstance().put(ProtegeProperties.CLASS_COLOR_KEY, "CC9F2A");
        ProtegeProperties.getInstance().put(ProtegeProperties.PROPERTY_COLOR_KEY, "306FA2");
        ProtegeProperties.getInstance().put(ProtegeProperties.OBJECT_PROPERTY_COLOR_KEY, "306FA2");
        ProtegeProperties.getInstance().put(ProtegeProperties.DATA_PROPERTY_COLOR_KEY, "6B8E23");//"6B8E23");// "408000");//"29A779");
        ProtegeProperties.getInstance().put(ProtegeProperties.INDIVIDUAL_COLOR_KEY, "541852");//"531852");
        ProtegeProperties.getInstance().put(ProtegeProperties.ONTOLOGY_COLOR_KEY, "6B47A2");//"5D30A2"); //"E55D1A");
        ProtegeProperties.getInstance().put(ProtegeProperties.ANNOTATION_PROPERTY_COLOR_KEY, "719FA0");//"719FA0");//"7DA230");//"98BDD8");
        ProtegeProperties.getInstance().put(ProtegeProperties.DATATYPE_COLOR_KEY, "6B8E23  ");//"719FA0");//"7DA230");//"98BDD8");
        ProtegeProperties.getInstance().put(ProtegeProperties.CLASS_VIEW_CATEGORY, "Class");
        ProtegeProperties.getInstance().put(ProtegeProperties.OBJECT_PROPERTY_VIEW_CATEGORY, "Object property");
        ProtegeProperties.getInstance().put(ProtegeProperties.DATA_PROPERTY_VIEW_CATEGORY, "Data property");
        ProtegeProperties.getInstance().put(ProtegeProperties.ANNOTATION_PROPERTY_VIEW_CATEGORY, "Annotation property");
        ProtegeProperties.getInstance().put(ProtegeProperties.INDIVIDUAL_VIEW_CATEGORY, "Individual");
        ProtegeProperties.getInstance().put(ProtegeProperties.DATATYPE_VIEW_CATEGORY, "Datatype");
        ProtegeProperties.getInstance().put(ProtegeProperties.ONTOLOGY_VIEW_CATEGORY, "Ontology");
        ProtegeProperties.getInstance().put(ProtegeProperties.QUERY_VIEW_CATEGORY, "Query");
        ProtegeProperties.getInstance().put(ProtegeProperties.DIFF_VIEW_CATEGORY, "Ontology comparison");
    }


    private static void loadRecentEditorKits() {
        RecentEditorKitManager.getInstance().load();
    }


    private void initializeLookAndFeel() {
        // Just the look and feel

        // command line look and feel overrides the protege-specific one

        if (System.getProperty("swing.defaultlaf") == null) {
            // If the OS is a Mac then the Mac L&F is set by default.  I've had too many complaints
            // from Mac users that the first thing they do is switch the L&F over to OS X - the Protege
            // L&F might be nicer on other platforms with L&Fs like motif, but the OS X L&F looks much better
            // than the Protege L&F and, moreover, the keybindings for keys such as copy&paste in the
            // Protege L&F are hardcoded to be windows key bindings e.g. Copy is CTRL+C (where as on a
            // Mac it should be CMD+C)
            // I don't know if Windows users would prefer the Windows L&F to be the default one - although
            // the Windows L&F keybindings are the same as the Protege L&F keybindings.
            String defaultLAFClassName;
            if (OSUtils.isOSX()) {
                defaultLAFClassName = UIManager.getSystemLookAndFeelClassName();
            }
            else {
                defaultLAFClassName = ProtegeProperties.PLASTIC_LAF_NAME;
            }

            Preferences p = PreferencesManager.getInstance().getApplicationPreferences(LOOK_AND_FEEL_KEY);
            String lafClsName = p.getString(LOOK_AND_FEEL_CLASS_NAME, defaultLAFClassName);

            try {
                // This is a workaround for some OSGi "feature".  From here http://adamish.com/blog/archives/156.
                // Force the Look & Feel to be instantiated.
                UIDefaults defaults = UIManager.getDefaults();
                if (lafClsName.equals(PlasticLookAndFeel.class.getName())) {
                    // Truly strange.  If we don't do this then the LAF cannot be found.
                    PlasticLookAndFeel.setCurrentTheme(new ProtegePlasticTheme());
                    UIManager.put("ClassLoader", PlasticLookAndFeel.class.getClassLoader());
                    // For plastic this needs to be instantiated here - otherwise SwingUtilities uses the wrong class
                    // loaded.
                    LookAndFeel lookAndFeel = (LookAndFeel) Class.forName(lafClsName).newInstance();
                    UIManager.setLookAndFeel(lookAndFeel);
                }
                else {
                    // Now set the class loader for Component UI loading to this one.  Works for non Plastic LAFs.
                    UIManager.put("ClassLoader", this.getClass().getClassLoader());
                    UIManager.setLookAndFeel(lafClsName);
                }
                setupDefaults(defaults);

            }
            catch (Exception e) {
                logger.error(e, e);
            }
        }
    }

    private void setupDefaults(UIDefaults defaults) {
        // TODO: Move this to somewhere more sensible

        defaults.put("TabbedPaneUI", CloseableTabbedPaneUI.class.getName());

        Color borderColor = new Color(220, 220, 220);

        MatteBorder commonBorder = BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor);

        defaults.put("ScrollPane.border", BorderFactory.createCompoundBorder(commonBorder,
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        defaults.put("TextArea.border", commonBorder);
        defaults.put("Spinner.border", commonBorder);
        defaults.put("ComboBox.border", commonBorder);

        defaults.put("TextField.border", BorderFactory.createCompoundBorder(commonBorder,
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));

    }

    /*
     * At the moment we are only checking the Logger state but in theory this method could
     * test other things also.  Regular users should never see this message.  The performance
     * impact of not configuring the Logger correctly is enormous.
     */
    private static void checkConfiguration() {
        Logger rootLogger = Logger.getRootLogger();
        if (rootLogger.isDebugEnabled()) {
            JOptionPane.showMessageDialog(null, "Logger not initialized.\n" +
                    "This could have a major impact on performance.\n" +
                    "Use the -Dlog4j.configuration=\"file:/...\" jvm option.", "Performance Issue Detected", JOptionPane.WARNING_MESSAGE);
        }
    }

    private static void setupExceptionHandler() {
        errorLog = new ErrorLog();
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread t, Throwable e) {
                errorLog.uncaughtException(t, e);
                logger.warn("Uncaught Exception in thread " + t.getName(), e);
            }
        });
    }


    private void processCommandLineURIs() {
        try {
            commandLineURIs = new ArrayList<URI>();
            for (String arg : PlatformArguments.getArguments(context)) {
                File f = new File(arg);
                if (f.exists()) {
                    commandLineURIs.add(f.toURI());
                }
                else {
                    try {
                        URI uri = new URI(arg);
                        commandLineURIs.add(uri);
                    }
                    catch (URISyntaxException e) {
                        logger.error(e);
                    }
                }
            }
        }
        catch (Throwable t) { // it is not important enough to stop anything.
            logger.warn("Error processing command line arguments " + t);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation of Application
    //
    /////////////////////////////////////////////////////////////////////////////////


    private void startApplication() throws Exception {
        createAndSetupDefaultEditorKit();
        if (commandLineURIs != null && !commandLineURIs.isEmpty()) {
            // Open any command line URIs
            for (URI uri : commandLineURIs) {
                editURI(uri);
            }
        }
        checkForUpdates();
    }

    private void checkForUpdates() {
        if(!PluginManager.getInstance().isAutoUpdateEnabled()) {
            return;
        }
        if (hasAutoUpdateBeenRunToday()) {
            logger.info("Auto-update has been run today.  Not running it again.");
            return;
        }
        logger.info("Auto-update has not been performed today.  Running it.");
            PluginManager.getInstance().performAutoUpdate();
            context.addFrameworkListener(new FrameworkListener() {
                public void frameworkEvent(FrameworkEvent event) {
                    if (event.getType() == FrameworkEvent.STARTED) {
                        context.removeFrameworkListener(this);
                    }
                }
            });

    }

    private static boolean hasAutoUpdateBeenRunToday() {
        Date lastRun = PluginManager.getInstance().getLastAutoUpdateDate();
        logger.info("Auto-update last performed: " + lastRun);
        Date startOfToday = getStartOfToday();
        return lastRun.after(startOfToday);
    }

    private static Date getStartOfToday() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        return calendar.getTime();
    }

    /**
     * Gets the default (first) editor kit factory plugin and uses it to create and setup and empty editor kit.
     */
    private void createAndSetupDefaultEditorKit() {
        try {
            ProtegeManager pm = ProtegeManager.getInstance();
            List<EditorKitFactoryPlugin> editorKitFactoryPlugins = pm.getEditorKitFactoryPlugins();
            if (!editorKitFactoryPlugins.isEmpty()) {
                EditorKitFactoryPlugin defaultPlugin = editorKitFactoryPlugins.get(0);
                pm.createAndSetupNewEditorKit(defaultPlugin);
            }
            else {
                throw new RuntimeException("No editor kit factory plugins available");
            }
        }
        catch (Exception e) {
            ErrorLogPanel.showErrorDialog(e);
        }
    }

    /////////////////////////////////////////////////////////////////////////////////
    //
    //  Visible Application Interfaces
    //
    /////////////////////////////////////////////////////////////////////////////////

    public static BundleContext getContext() {
        return context;
    }


    public static ErrorLog getErrorLog() {
        return errorLog;
    }



    public static BackgroundTaskManager getBackgroundTaskManager() {
        return backgroundTaskManager;
    }


    public static boolean handleQuit() {
        quitting = true;
        final EditorKitManager eKitMngr = ProtegeManager.getInstance().getEditorKitManager();
        for (EditorKit eKit : eKitMngr.getEditorKits()) {
            Workspace wSpace = eKit.getWorkspace();
            if (!eKitMngr.getWorkspaceManager().doClose(wSpace)) {
                quitting = false;
                return quitting;
            }
        }
        try {
            boolean forceExit = !OSGi.systemExitHandledByLauncher(); // this call fails after context.getBundle(0).stop()
            context.getBundle(0).stop();
            // Danger, Will Robinson!  Weird territory here - the class loader is no longer working!
            //  java.lang.IllegalStateException: zip file closed
            //     at java.util.zip.ZipFile.ensureOpen(ZipFile.java:403)
            //     at java.util.zip.ZipFile.getEntry(ZipFile.java:148)
            //     at org.apache.felix.framework.util.ZipFileX.getEntry(ZipFileX.java:52)
            //     at org.apache.felix.framework.cache.JarContent.getEntryAsBytes(JarContent.java:122)
            //     at org.apache.felix.framework.ModuleImpl$ModuleClassLoader.findClass(ModuleImpl.java:1816)
            //     at org.apache.felix.framework.ModuleImpl.findClassOrResourceByDelegation(ModuleImpl.java:727)
            //     at org.apache.felix.framework.ModuleImpl.access$400(ModuleImpl.java:71)
            //     at org.apache.felix.framework.ModuleImpl$ModuleClassLoader.loadClass(ModuleImpl.java:1768)
            //     at java.lang.ClassLoader.loadClass(ClassLoader.java:248)
            //     at org.protege.editor.core.ProtegeApplication.handleQuit(ProtegeApplication.java:418)
            if (forceExit) {
                Thread.sleep(1000);
                System.exit(0);
            }
        }
        catch (Throwable t) {
            logger.fatal("Exception caught trying to shut down Protege.", t);
        }
        return true;
    }


    public void handleClose() {
        if (!quitting) {
            final EditorKitManager eKitMngr = ProtegeManager.getInstance().getEditorKitManager();
            if (eKitMngr.getEditorKitCount() == 0) {
                handleQuit();
            }
        }
    }


    public void editURI(URI uri) throws Exception {
        ProtegeManager pm = ProtegeManager.getInstance();
        for (EditorKitFactoryPlugin plugin : pm.getEditorKitFactoryPlugins()) {
            if (plugin.newInstance().canLoad(uri)) {
                pm.loadAndSetupEditorKitFromURI(plugin, uri);
//                welcomeFrame.setVisible(false);
                break;
            }
        }
    }


    public static void handleRestart() {

    }
}
