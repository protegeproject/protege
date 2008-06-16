package org.protege.editor.core.ui.view;

import org.apache.log4j.Logger;
import org.coode.mdock.*;
import org.protege.editor.core.FileManager;
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
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 15-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 * <p/>
 * A panel that contains views.  Each panel has an id.
 */
public class ViewsPane extends JPanel {

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
        File file = getCustomConfigFile();
        URL url = null;
        if (file == null || !file.exists() || memento.isForceReset()) {
            // No file, so default to default one :)
            url = memento.getInitialCongigFileURL();
        }
        else {
            try {
                url = file.toURI().toURL();
            }
            catch (MalformedURLException e) {
                logger.error(e);
            }
        }

        if (url != null) {
            try {
                // Got our config file.  Attempt to reannimate the views.
                NodeReanimator nodeReanimator = new NodeReanimator(new InputStreamReader(new BufferedInputStream(url.openStream())),
                                                                   new ViewComponentFactory(workspace));
                SplitterNode node = nodeReanimator.getRootNode();
                nodePanel = new NodePanel(node);
                add(nodePanel);
                dynamicConfigPanel = new DynamicConfigPanel(nodePanel);
            }
            catch (IOException e) {
                logger.error(e);
            }
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


    /**
     * Gets the file that is used to save view customisations
     * for this tab.
     */
    private File getCustomConfigFile() {
        URL url = memento.getInitialCongigFileURL();
        if (url != null) {
            return new File(FileManager.getViewConfigurationsFolder(), url.getFile());
        }
        else if (memento.getViewPaneId() != null){
            return new File(FileManager.getViewConfigurationsFolder(), "viewconfig-" + memento.getViewPaneId() + ".xml");
        }
        return null;
    }


    public void saveViews() {
        try {
            // Need to save a config file, and put a key in the prefs
            // that points to the config file
            File tabFile = getCustomConfigFile();
            if (tabFile == null) {
                return;
            }
            tabFile.getParentFile().mkdirs();
            Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(tabFile)));
            NodeSerialiser nodeSerialiser = new NodeSerialiser(nodePanel.getRootNode(),
                                                               new ViewComponentPropertiesFactory(),
                                                               writer);
            nodeSerialiser.serialise();
            writer.flush();
            writer.close();
        }
        catch (ParserConfigurationException e) {
            logger.error(e);
        }
        catch (IOException e) {
            logger.error(e);
        }
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
}
