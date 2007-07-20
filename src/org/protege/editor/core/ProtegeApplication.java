package org.protege.editor.core;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.LookAndFeel;
import javax.swing.PopupFactory;
import javax.swing.UIManager;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.protege.editor.core.editorkit.EditorKitFactory;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;
import org.protege.editor.core.editorkit.EditorKitFactoryPluginLoader;
import org.protege.editor.core.editorkit.RecentEditorKitManager;
import org.protege.editor.core.plugin.PluginUtilities;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.error.ErrorLog;
import org.protege.editor.core.ui.util.ProtegePlasticTheme;
import org.protege.editor.core.update.UpdateManager;

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
 * The ProtegeApplication is the main entry point to Protge.  The application
 * is actually a plugin to the Java Plugin Framework
 */
public class ProtegeApplication implements BundleActivator {

    private static final Logger logger = Logger.getLogger(ProtegeApplication.class);

    public static final String BUNDLE_DIR_PROP = "org.protege.plugin.dir";

    public static final String ID = "org.protege.editor.core.application";

    public static final String LOOK_AND_FEEL_KEY = "LOOK_AND_FEEL_KEY";

    public static final String LOOK_AND_FEEL_CLASS_NAME = "LOOK_AND_FEEL_CLASS_NAME";
    
    private static BundleContext context;
    
    private boolean bundles_loaded = false;
    
    private List<URI> commandLineURIs;

    private static ErrorLog errorLog = new ErrorLog();
    
    

    public void start(BundleContext context) throws Exception {
        ProtegeApplication.context = context;
        ProtegeApplication application = new ProtegeApplication();
        application.initApplication(new String[0]);
        ProtegeManager.getInstance().initialise(this);
        application.startApplication();
    }


    /* TODO - this needs work */
    public void stop(BundleContext arg0) throws Exception {
        BookMarkedURIManager.getInstance().dispose();
        RecentEditorKitManager.getInstance().dispose();
        PluginUtilities.getInstance().dispose();
        ProtegeManager.getInstance().dispose();
    }
    
    public static BundleContext getContext() {
        return context;
    }

    /////////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation of ApplicationPlugin
    //
    /////////////////////////////////////////////////////////////////////////////////


    protected ProtegeApplication initApplication(String args[]) throws Exception {
        PluginUtilities.getInstance().initialise(this, context);
        loadDefaults();
        loadRecentEditorKits();
        loadPreferences();
        // setupLogging();
        setupExceptionHandler();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                RecentEditorKitManager.getInstance().save();
            }
        });
        processCommandLineURIs(args);
        loadPlugins();
        return this;
    }


    private static void loadDefaults() {
        ProtegeProperties.getInstance().put(ProtegeProperties.CLASS_COLOR_KEY, "CC9F2A");
        ProtegeProperties.getInstance().put(ProtegeProperties.PROPERTY_COLOR_KEY, "306FA2");
        ProtegeProperties.getInstance().put(ProtegeProperties.OBJECT_PROPERTY_COLOR_KEY, "306FA2");
        ProtegeProperties.getInstance().put(ProtegeProperties.DATA_PROPERTY_COLOR_KEY, "29A779");
        ProtegeProperties.getInstance().put(ProtegeProperties.INDIVIDUAL_COLOR_KEY, "531852");
        ProtegeProperties.getInstance().put(ProtegeProperties.ONTOLOGY_COLOR_KEY, "6B47A2");//"5D30A2"); //"E55D1A");
        ProtegeProperties.getInstance().put(ProtegeProperties.ANNOTATION_PROPERTY_COLOR_KEY,
                                            "C59969");//"719FA0");//"7DA230");//"98BDD8");

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
        //String sysLookAndFeelClassName = UIManager.getSystemLookAndFeelClassName();
        Preferences p = PreferencesManager.getInstance().getApplicationPreferences(LOOK_AND_FEEL_KEY);
        String lafClsName = p.getString(LOOK_AND_FEEL_CLASS_NAME, ProtegeProperties.PLASTIC_LAF_NAME);
        try {
        	if (lafClsName.equals(ProtegeProperties.PLASTIC_LAF_NAME)) {
        		setProtegeDefaultLookAndFeel(lafClsName);
        	} else {
        		UIManager.setLookAndFeel(lafClsName);
        	}
        }
        catch (Exception e) {
            logger.error(e);
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
		} catch (ClassNotFoundException e) {
			logger.warn("Look and feel not found: " + lafName);
		} catch (Exception e) {
			logger.warn(e.toString());
		}
	}

    private static void setupLogging() {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ERROR);
        Logger.getRootLogger().addAppender(new ConsoleAppender() {
            public void append(LoggingEvent loggingEvent) {
                if (loggingEvent.getMessage() instanceof Throwable) {
                    ((Throwable) loggingEvent.getMessage()).printStackTrace();
                }
                else {
                    super.append(loggingEvent);
                }
            }
        });
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


    public static ErrorLog getErrorLog() {
        return errorLog;
    }


    private void processCommandLineURIs(String [] strings) {
        commandLineURIs = new ArrayList<URI>();
        for (String s : strings) {
            try {
                URI uri = new URI(s);
                commandLineURIs.add(uri);
            }
            catch (URISyntaxException e) {
                logger.error(e);
            }
        }
    }
    
    private void loadPlugins() {
        if (bundles_loaded) return;
        String dir_name = System.getProperty(BUNDLE_DIR_PROP);
        if  (dir_name == null) {
            logger.info("no plugins found");
            return;
        }
        File dir = new File(dir_name);
        if (!dir.exists() || !dir.isDirectory()) {
            logger.error("Plugin directory " + dir_name + " is invalid");
            return;
        }
        List<Bundle> plugins = new ArrayList<Bundle>();
        for (File plugin : dir.listFiles()) {
            Bundle b = null;
            try {
                b = context.installBundle(plugin.toURI().toString());
                plugins.add(b);
            }
            catch (Throwable t) {
                logger.error("Could not install plugin ", t);
            }
        }
        for (Bundle b  : plugins) {
            try {
                b.start();
                String name = (String) b.getHeaders().get(Constants.BUNDLE_NAME);
                if (name == null) {
                    name = b.getSymbolicName();
                }
                logger.info("Installed plugin " + name);
            }
            catch (Throwable t) {
                logger.error("Could not start bundle " + b.getSymbolicName(), t);
            }
        }
        bundles_loaded = true;
    }

    /////////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation of Application
    //
    /////////////////////////////////////////////////////////////////////////////////


    public void startApplication() throws Exception {
        ProtegeWelcomeFrame frame = new ProtegeWelcomeFrame();

//        frame.addWindowListener(new WindowAdapter() {
//            public void windowClosing(WindowEvent e) {
//                try {
//                    doStop();
//                    System.exit(0);
//                } catch (Exception e1) {
//                    e1.printStackTrace();
//                    System.exit(1);
//                }
//            }
//        });
        frame.setVisible(true);
        try {
            if (commandLineURIs != null && !commandLineURIs.isEmpty()) {
                // Open any command line URIs
                EditorKitFactoryPluginLoader loader = new EditorKitFactoryPluginLoader();
                List<EditorKitFactory> factories = new ArrayList<EditorKitFactory>();
                for (URI uri : commandLineURIs) {
                    for (EditorKitFactoryPlugin plugin : loader.getPlugins()) {
                        EditorKitFactory factory = plugin.newInstance();
                        if (factory.canLoad(uri)) {
                            ProtegeManager.getInstance().loadAndSetupEditorKitFromURI(plugin, uri);
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            logger.error(e);
        }
        UpdateManager.getInstance().checkForUpdates(false);
    }

    /////////////////////////////////////////////////////////////////////////////////
    //
    //  Implementation of Plugin
    //
    /////////////////////////////////////////////////////////////////////////////////



}
