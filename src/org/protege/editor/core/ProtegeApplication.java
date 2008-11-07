package org.protege.editor.core;

import com.jgoodies.looks.FontPolicies;
import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import com.jgoodies.looks.FontSets;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;
import org.apache.log4j.Logger;
import org.osgi.framework.*;
import org.protege.editor.core.editorkit.EditorKit;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;
import org.protege.editor.core.editorkit.EditorKitManager;
import org.protege.editor.core.editorkit.RecentEditorKitManager;
import org.protege.editor.core.platform.PlatformArguments;
import org.protege.editor.core.platform.apple.ProtegeAppleApplication;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.error.ErrorLog;
import org.protege.editor.core.ui.util.OSUtils;
import org.protege.editor.core.ui.util.ProtegePlasticTheme;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.core.update.PluginManager;
import org.protege.editor.core.util.BundleBuilder;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Locale;

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
    public static final String BUNDLE_EXTRA_PROP = "org.protege.plugin.extra";
    public static final String OSGI_READS_DIRECTORIES = "org.protege.allow.directory.bundles";
    
    public static final String RUN_ONCE = "PROTEGE_OSGI_RUN_ONCE";

    public final static char BUNDLE_EXTRA_SEPARATOR = ':';

    public static final String ID = "org.protege.editor.core.application";

    public static final String LOOK_AND_FEEL_KEY = "LOOK_AND_FEEL_KEY";

    public static final String LOOK_AND_FEEL_CLASS_NAME = "LOOK_AND_FEEL_CLASS_NAME";
    

    private static BundleContext context;

    private boolean bundles_loaded = false;

    private List<URI> commandLineURIs;

    private static ErrorLog errorLog = new ErrorLog();

    private ProtegeWelcomeFrame welcomeFrame;

    private static boolean quitting = false;

    public void start(BundleContext context) throws Exception {
        ProtegeApplication.context = context;
        displayPlatform();
        initApplication();

        if (OSUtils.isOSX()){
            ProtegeAppleApplication.getInstance();
        }

        ProtegeManager.getInstance().initialise(this);
        startApplication();
    }


    // Called when the application is finally completely shutting down
    public void stop(BundleContext arg0) throws Exception {
        BookMarkedURIManager.getInstance().dispose();
        RecentEditorKitManager.getInstance().save();
        RecentEditorKitManager.getInstance().dispose();
        PluginUtilities.getInstance().dispose();
        ProtegeManager.getInstance().dispose();
        logger.info("Thank you for using Protege. Goodbye.");
    }


    /////////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation of ApplicationPlugin
    //
    /////////////////////////////////////////////////////////////////////////////////


    // If this isn't liked info can be replaced with debug.
    // It helps with diagnosing problems with the FaCT++ plugin.
    @SuppressWarnings("unchecked")
    private void displayPlatform() {
        Dictionary manifest = context.getBundle().getHeaders();
        logger.info("Starting Protege 4 OWL Editor (Version "  + manifest.get("Bundle-Version") + ")");
        logger.info("Platform:");
        logger.info("    Java: JVM " + System.getProperty("java.runtime.version") +
                    " Memory: " + (Runtime.getRuntime().maxMemory() / 1000000) + "M");
        logger.info("    Language: " + Locale.getDefault().getLanguage() +
                    ", Country: " + Locale.getDefault().getCountry());
        logger.info("    Framework: " + context.getProperty(Constants.FRAMEWORK_VENDOR)
                    + " (" + context.getProperty(Constants.FRAMEWORK_VERSION) + ")");
        logger.info("    OS: " + context.getProperty(Constants.FRAMEWORK_OS_NAME)
                    + " (" + context.getProperty(Constants.FRAMEWORK_OS_VERSION) + ")");
        logger.info("    Processor: " + context.getProperty(Constants.FRAMEWORK_PROCESSOR));
    }

    protected ProtegeApplication initApplication() throws Exception {
        PluginUtilities.getInstance().initialise(this, context);
        loadDefaults();
        loadPreferences();
        setupExceptionHandler();
        loadPlugins();
        processCommandLineURIs();  // plugins may set arguments
        loadRecentEditorKits();
        return this;
    }


    private static void loadDefaults() {
        ProtegeProperties.getInstance().put(ProtegeProperties.CLASS_COLOR_KEY, "CC9F2A");
        ProtegeProperties.getInstance().put(ProtegeProperties.PROPERTY_COLOR_KEY, "306FA2");
        ProtegeProperties.getInstance().put(ProtegeProperties.OBJECT_PROPERTY_COLOR_KEY, "306FA2");
        ProtegeProperties.getInstance().put(ProtegeProperties.DATA_PROPERTY_COLOR_KEY, "29A779");
        ProtegeProperties.getInstance().put(ProtegeProperties.INDIVIDUAL_COLOR_KEY, "531852");
        ProtegeProperties.getInstance().put(ProtegeProperties.ONTOLOGY_COLOR_KEY, "6B47A2");//"5D30A2"); //"E55D1A");
        ProtegeProperties.getInstance().put(ProtegeProperties.ANNOTATION_PROPERTY_COLOR_KEY, "C59969");//"719FA0");//"7DA230");//"98BDD8");
        ProtegeProperties.getInstance().put(ProtegeProperties.CLASS_VIEW_CATEGORY, "Class");
        ProtegeProperties.getInstance().put(ProtegeProperties.OBJECT_PROPERTY_VIEW_CATEGORY, "Object property");
        ProtegeProperties.getInstance().put(ProtegeProperties.DATA_PROPERTY_VIEW_CATEGORY, "Data property");
        ProtegeProperties.getInstance().put(ProtegeProperties.ANNOTATION_PROPERTY_VIEW_CATEGORY, "Annotation property");
        ProtegeProperties.getInstance().put(ProtegeProperties.INDIVIDUAL_VIEW_CATEGORY, "Individual");
        ProtegeProperties.getInstance().put(ProtegeProperties.ONTOLOGY_VIEW_CATEGORY, "Ontology");
    }


    private static void loadRecentEditorKits() {
        RecentEditorKitManager.getInstance().load();
    }


    private static void loadPreferences() {
        // Just the look and feel

        // command line look and feel overrides the protege-specific one
        if (System.getProperty("swing.defaultlaf") == null){
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
                if (lafClsName.equals(ProtegeProperties.PLASTIC_LAF_NAME)) {
                    setProtegeDefaultLookAndFeel(lafClsName);
                }
                else {
                    UIManager.setLookAndFeel(lafClsName);
                }
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
    }


    private static void setProtegeDefaultLookAndFeel(String lafName) {
        try {
            LookAndFeel lookAndFeel = (LookAndFeel) Class.forName(lafName).newInstance();

            PopupFactory.setSharedInstance(new PopupFactory());
            PlasticLookAndFeel.setCurrentTheme(new ProtegePlasticTheme());
            PlasticLookAndFeel.setTabStyle(PlasticLookAndFeel.TAB_STYLE_METAL_VALUE);

            FontSet fontSet = FontSets.createDefaultFontSet(ProtegePlasticTheme.DEFAULT_FONT);
            FontPolicy fixedPolicy = FontPolicies.createFixedPolicy(fontSet);
            PlasticLookAndFeel.setFontPolicy(fixedPolicy);

            UIManager.put("ClassLoader", lookAndFeel.getClass().getClassLoader());
            UIManager.setLookAndFeel(lookAndFeel);
        }
        catch (ClassNotFoundException e) {
            logger.warn("Look and feel not found: " + lafName);
        }
        catch (Exception e) {
            logger.warn(e.toString());
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


    private void loadPlugins() {
        if (bundles_loaded) return;
        List<File> locations = new ArrayList<File>();
        locations.addAll(getPluginBundles());
        locations.addAll(getExtraBundles());
        if (locations.isEmpty()) {
            logger.warn("No plugins found");
        }
        List<Bundle> plugins = new ArrayList<Bundle>();
        boolean warnAboutDirectories = false;
        for (File plugin : locations) {
            Bundle b = null;
            try {
                b = context.installBundle(getBundleLocation(plugin));
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
                if (plugin.isDirectory() && canReadDirectoryBundles()) warnAboutDirectories = true;
            }
        }
        if (warnAboutDirectories) {
            logger.warn("Consider using -D" + OSGI_READS_DIRECTORIES + "=false");
        }
        for (Bundle b  : plugins) {
            try {
                b.start();
                String name = (String) b.getHeaders().get(Constants.BUNDLE_NAME);
                if (name == null) {
                    name = b.getSymbolicName();
                }
                logger.info("Installed plugin " + name);
                if (b.getHeaders().get(BUNDLE_WITHOUT_PLUGIN_XML) == null && b.getResource("/plugin.xml") == null) {
                    logger.info("\t" + name + " Plugin has no plugin.xml resource");
                }

            }
            catch (Throwable t) {
                logger.error("Could not start bundle " + b.getSymbolicName(), t);
            }
        }
        bundles_loaded = true;
    }


    private String getBundleLocation(File source) throws IOException {
        boolean directoryBundlesWork = canReadDirectoryBundles();
        if (source.isFile() || directoryBundlesWork) { // the normal case
            return "file:" + source.getPath();
        }
        else { // this is a hack for IDE developers
            long start = System.currentTimeMillis();
            BundleBuilder builder = new BundleBuilder(source);
            File jar = File.createTempFile("ProtegeBundle", ".jar");
            builder.createJarFile(jar);
            logger.warn("Converted directory (" + source + ") to plugin (" + (System.currentTimeMillis() - start) + " ms)");
            return jar.toURI().toString();
        }
    }

    private boolean isTrivialBundleLoadException(File plugin, Throwable t) {
        return plugin.getName().equals(".DS_Store");
    }


    private boolean canReadDirectoryBundles() {
        return System.getProperty(OSGI_READS_DIRECTORIES, "true").toLowerCase().equals("true");
    }

    /////////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation of Application
    //
    /////////////////////////////////////////////////////////////////////////////////


    private void startApplication() throws Exception {
        showWelcomeFrame();
        try {
            if (commandLineURIs != null && !commandLineURIs.isEmpty()) {
                // Open any command line URIs
                for (URI uri : commandLineURIs) {
                    editURI(uri);
                }
                if (ProtegeManager.getInstance().getEditorKitManager().getEditorKitCount() != 0) {
                    welcomeFrame.setVisible(false);
                }
            }
        }
        catch (Exception e) {
            logger.error("Exception caught loading ontology", e);
        }
        PluginManager.getInstance().checkForUpdatesInBackground();
    }

    private void showWelcomeFrame(){
        if (welcomeFrame == null){
            welcomeFrame = new ProtegeWelcomeFrame();
            welcomeFrame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    handleQuit();
                }
            });
            welcomeFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        }
        welcomeFrame.setVisible(true);
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


    public static boolean handleQuit() {
        quitting = true;
        final EditorKitManager eKitMngr = ProtegeManager.getInstance().getEditorKitManager();
        for (EditorKit eKit : eKitMngr.getEditorKits()){
            Workspace wSpace = eKit.getWorkspace();
            if (!eKitMngr.getWorkspaceManager().doClose(wSpace)){
                quitting = false;
                return quitting;
            }
        }
        try {
            ProtegeApplication.context.getBundle().uninstall();
            System.exit(0);
        }
        catch (BundleException e) {
            logger.error("Failed to correctly shutdown Protege:", e);
        }
        return true;
    }


    public void handleClose() {
        if (!quitting){
            final EditorKitManager eKitMngr = ProtegeManager.getInstance().getEditorKitManager();
            if (eKitMngr.getEditorKitCount() == 0){
                showWelcomeFrame();
            }
        }
    }


    public void editURI(URI uri) throws Exception {
        ProtegeManager pm = ProtegeManager.getInstance();
        for (EditorKitFactoryPlugin plugin : pm.getEditorKitFactoryPlugins()) {
            if (plugin.newInstance().canLoad(uri)) {
                pm.loadAndSetupEditorKitFromURI(plugin, uri);
                welcomeFrame.setVisible(false);
                break;
            }
        }
    }


    public static void handleRestart() {
        
    }
}
