package org.protege.editor.core;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.JFrame;
import javax.swing.LookAndFeel;
import javax.swing.PopupFactory;
import javax.swing.UIManager;

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
import org.protege.editor.core.platform.OSUtils;
import org.protege.editor.core.platform.PlatformArguments;
import org.protege.editor.core.platform.apple.ProtegeAppleApplication;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.error.ErrorLog;
import org.protege.editor.core.ui.progress.BackgroundTaskManager;
import org.protege.editor.core.ui.util.ProtegePlasticTheme;
import org.protege.editor.core.ui.workspace.Workspace;
import org.protege.editor.core.update.PluginManager;

import com.jgoodies.looks.FontPolicies;
import com.jgoodies.looks.FontPolicy;
import com.jgoodies.looks.FontSet;
import com.jgoodies.looks.FontSets;
import com.jgoodies.looks.plastic.PlasticLookAndFeel;

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

    public static final String RUN_ONCE = "PROTEGE_OSGI_RUN_ONCE";

    public static final String ID = "org.protege.editor.core.application";

    public static final String LOOK_AND_FEEL_KEY = "LOOK_AND_FEEL_KEY";

    public static final String LOOK_AND_FEEL_CLASS_NAME = "LOOK_AND_FEEL_CLASS_NAME";
    
    private static BundleContext context;
    
    private static BundleManager bundleManager;

    private List<URI> commandLineURIs;

    private static ErrorLog errorLog = new ErrorLog();

    private static BackgroundTaskManager backgroundTaskManager = new BackgroundTaskManager();

    private JFrame welcomeFrame;

    private static boolean quitting = false;

    public void start(BundleContext context) {
    	try {
    		ProtegeApplication.context = context;
    		displayPlatform();
    		initApplication();

    		if (OSUtils.isOSX()){
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
        Bundle b = context.getBundle();
        Version v = PluginUtilities.getBundleVersion(b);
        logger.info("Starting Protege 4 OWL Editor (Version "  
                    +  v.getMajor() + "." + v.getMinor() + "." + v.getMicro()
                    + ", Build = " + PluginUtilities.getBuildNumber(b) + ")");
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
        PluginUtilities.getInstance().initialise(context);
        loadDefaults();
        initializeLookAndFeel();
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
        ProtegeProperties.getInstance().put(ProtegeProperties.DATATYPE_COLOR_KEY, "29a779");//"719FA0");//"7DA230");//"98BDD8");
        ProtegeProperties.getInstance().put(ProtegeProperties.CLASS_VIEW_CATEGORY, "Class");
        ProtegeProperties.getInstance().put(ProtegeProperties.OBJECT_PROPERTY_VIEW_CATEGORY, "Object property");
        ProtegeProperties.getInstance().put(ProtegeProperties.DATA_PROPERTY_VIEW_CATEGORY, "Data property");
        ProtegeProperties.getInstance().put(ProtegeProperties.ANNOTATION_PROPERTY_VIEW_CATEGORY, "Annotation property");
        ProtegeProperties.getInstance().put(ProtegeProperties.INDIVIDUAL_VIEW_CATEGORY, "Individual");
        ProtegeProperties.getInstance().put(ProtegeProperties.DATATYPE_VIEW_CATEGORY, "Datatype");
        ProtegeProperties.getInstance().put(ProtegeProperties.ONTOLOGY_VIEW_CATEGORY, "Ontology");
    }


    private static void loadRecentEditorKits() {
        RecentEditorKitManager.getInstance().load();
    }


    private static void initializeLookAndFeel() {
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
    
    private void loadPlugins() {
        bundleManager = new BundleManager(context);
        bundleManager.loadPlugins();
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
        
        if (PluginManager.getInstance().isAutoUpdateEnabled()){
            PluginManager.getInstance().performAutoUpdate();

            context.addFrameworkListener(new FrameworkListener() {
               public void frameworkEvent(FrameworkEvent event) {
                   if (event.getType() == FrameworkEvent.STARTED) {
                       context.removeFrameworkListener(this);
                   }
                } 
            });

        }
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


    public static BackgroundTaskManager getBackgroundTaskManager() {
        return backgroundTaskManager;
    }


    public static BundleManager getBundleManager() {
        return bundleManager;
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
            context.getBundle(0).stop();
        }
        catch (Throwable t) {
            logger.fatal("Exception caught trying to shut down Protege.", t);
        }
        
        /*
         * OSGi is gone or going at this point. Felix is about to close the jvm but equinox does not.
         * But exiting is a bit tricky at this point.  If I don't call System.exit below then an equinox Protege 
         * will continue and throw lots of exceptions.  (TODO - this probably shouldn't work this way...)
         * Felix throws an exception without the sleep call below:
         * 
         * org.osgi.framework.BundleException: Bundle org.apache.felix.framework [0] cannot be stopped since it is already stopping.
         *
         * Indeed a printout of the state right after the stop call shows the system bundle in the STOPPING state
         * which seems to be counter to the specification.  Felix has a shutdown hook in that causes System.exit to 
         * stop the framework bundle again.
         * 
         * Knopflerish behavior not yet tested.
         */
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // we will exit anyway...
        }
        System.exit(0);
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
