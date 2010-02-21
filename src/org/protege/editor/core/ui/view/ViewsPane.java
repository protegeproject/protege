package org.protege.editor.core.ui.view;

import org.apache.log4j.Logger;
import org.coode.mdock.*;
import org.protege.editor.core.prefs.Preferences;
import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.workspace.Workspace;

import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br> The University Of Manchester<br> Medical Informatics Group<br> Date:
 * 15-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br> www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A panel that contains views.  Each panel has an id.
 */
public class ViewsPane extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 8919347949244067211L;

    public static final Logger logger = Logger.getLogger(ViewsPane.class);

    private ViewsPaneMemento memento;

    private DynamicConfigPanel dynamicConfigPanel;

    private NodePanel nodePanel;


    public ViewsPane(Workspace workspace, ViewsPaneMemento memento) {
        this.memento = memento;
        setLayout(new BorderLayout());

        // We need to read in a views configuration file.  Either we find
        // a customised one, or we read in the default one.

        // See if there is a customised file
        String serialisedViews = readViewLayout();
        Reader reader = null;
        if (serialisedViews.length() != 0 && !memento.isForceReset()) {
            // Got a previous config and not trying to reset
            reader = new StringReader(serialisedViews);
        }
        else {
            // Try and restore
            if(memento.getInitialCongigFileURL() != null) {
                // No file, so default to default one :)
                try {
                    reader = new InputStreamReader(new BufferedInputStream(memento.getInitialCongigFileURL().openStream()));
                }
                catch (IOException e) {
                    logger.error(e);
                }
            }
        }
        
        if (reader != null) {
            // Got our config file.  Attempt to reannimate the views.
            NodeReanimator nodeReanimator = new NodeReanimator(reader, new ViewComponentFactory(workspace));
            SplitterNode node = nodeReanimator.getRootNode();
            nodePanel = new NodePanel(node);
            add(nodePanel);
            dynamicConfigPanel = new DynamicConfigPanel(nodePanel);
        }
        else {
            // There isn't even a default xml config file.  We don't want the system
            // to keel over, so just create a blank panel (the user can drag views on
            // to it as they wish).
            VerticalSplitterNode node = new VerticalSplitterNode(Collections.EMPTY_LIST, Collections.EMPTY_LIST);
            nodePanel = new NodePanel(node);
            add(nodePanel);
            dynamicConfigPanel = new DynamicConfigPanel(nodePanel);
        }
    }


    public void dispose() {
        // Dispose of the views
        logger.debug("Disposing of views");
        for (View view : getViews()) {
            view.dispose();
        }
    }


    public Set<View> getViews() {
        Set<View> views = new HashSet<View>();
        getViews(this, views);
        return views;
    }


    private static void getViews(Component c, Set<View> result) {
        if (c instanceof View) {
            result.add((View) c);
        }
        if (c instanceof Container) {
            Component[] components = ((Container) c).getComponents();
            for (Component comp : components) {
                getViews(comp, result);
            }
        }
    }


    public void saveViews(Writer writer) {
        try {
            NodeSerialiser nodeSerialiser = new NodeSerialiser(nodePanel.getRootNode(),
                                                               new ViewComponentPropertiesFactory(),
                                                               writer);
            nodeSerialiser.serialise();
            writer.flush();
        }
        catch (ParserConfigurationException e) {
            logger.error(e);
        }
        catch (IOException e) {
            logger.error(e);
        }
    }


    public void saveViews() {
        StringWriter writer = new StringWriter();
        saveViews(writer);
        storeViewLayout(writer.getBuffer().toString());
    }


    public boolean containsView(String id) {
        for (View view : getViews()) {
            if (id.equals(view.getId())) {
                return true;
            }
        }
        return false;
    }


    public void addView(View view, String label) {
        view.createUI();
        dynamicConfigPanel.setCurrentComponent(view, label);
        dynamicConfigPanel.activate();
    }


    public void bringViewToFront(String id) {
        for (View view : getViews()) {
            if (view.getId() != null && view.getId().equals(id)) {
                Util.bringToFront(view);
                // Carry on until all views with the specified
                // view id are in front
            }
        }
    }

    //////////////////////////////////////////////////////////////////////////////////////////

    // We now save view layout information in the preferences.  This means that view layout
    // don't disappear between installations

    public static final String VIEW_LAYOUT_PREFERENCES_ID = "ViewLayoutPreferences";


    /**
     * Gets the layout preferences set.  This is common amongst all views
     * @return The preferences.
     */
    public static Preferences getViewLayoutPreferences() {
        PreferencesManager prefsMan = PreferencesManager.getInstance();
        return prefsMan.getApplicationPreferences(VIEW_LAYOUT_PREFERENCES_ID);
    }


    /**
     * Gets the layout preferences key for this view panel
     * @return The key
     */
    public String getLayoutPreferencesKey() {
        return memento.getViewPaneId();
    }


    /**
     * Stores a serialisation of a view layout in the preferences system
     * @param serialisation The serialisation to be stored.
     */
    public void storeViewLayout(String serialisation) {
        getViewLayoutPreferences().putString(getLayoutPreferencesKey(), serialisation);
    }


    /**
     * Reads a serialisation of a view layout from the preferences system
     * @return The serialisation, or the empty string if no serialisation
     * was previously stored.
     */
    public String readViewLayout() {
        return getViewLayoutPreferences().getString(getLayoutPreferencesKey(), "");
    }
}
