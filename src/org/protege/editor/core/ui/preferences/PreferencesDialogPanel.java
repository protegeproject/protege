package org.protege.editor.core.ui.preferences;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.apache.log4j.Logger;
import org.protege.editor.core.editorkit.EditorKit;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 08-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class PreferencesDialogPanel extends JPanel {

    private static final Logger logger = Logger.getLogger(PreferencesDialogPanel.class);


    private EditorKit editorKit;

    private Map<String, PreferencesPanel> map;

    private JTabbedPane tabbedPane;


    public PreferencesDialogPanel(EditorKit editorKit) {
        this.editorKit = editorKit;
        map = new HashMap<String, PreferencesPanel>();
        setLayout(new BorderLayout());
        tabbedPane = new JTabbedPane();
        PreferencesPanelPluginLoader loader = new PreferencesPanelPluginLoader(editorKit);
        Set<PreferencesPanelPlugin> plugins = new TreeSet<PreferencesPanelPlugin>(new Comparator<PreferencesPanelPlugin>() {
            public int compare(PreferencesPanelPlugin o1, PreferencesPanelPlugin o2) {
                String s1 = o1.getLabel();
                String s2 = o2.getLabel();
                return s1.compareTo(s2);
            }
        });
        plugins.addAll(loader.getPlugins());
        for (PreferencesPanelPlugin plugin : plugins) {
            try {
                PreferencesPanel panel = plugin.newInstance();
                panel.initialise();
                map.put(plugin.getLabel(), panel);
                tabbedPane.addTab(plugin.getLabel(), panel);
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
        add(tabbedPane);
    }


    public void dispose() {
        for (PreferencesPanel panel : new ArrayList<PreferencesPanel>(map.values())) {
            try {
                panel.dispose();
            }
            catch (Exception e) {
                logger.warn("BAD PREFS PANEL: (" + panel.getClass().getSimpleName() + ")");
            }
        }
        map.clear();
    }


    public void applyPreferences() {
        for (PreferencesPanel panel : new ArrayList<PreferencesPanel>(map.values())) {
            try {
                panel.applyChanges();
            }
            catch (Exception e) {
                logger.error(e);
            }
        }
    }


    public Dimension getPreferredSize() {
        return new Dimension(600, 400);
    }


    public static void showPreferencesDialog(String selectedPanel, EditorKit editorKit) {
        PreferencesDialogPanel panel = new PreferencesDialogPanel(editorKit);
        Component c = panel.map.get(selectedPanel);
        if (c != null) {
            panel.tabbedPane.setSelectedComponent(c);
        }
        int ret = JOptionPane.showConfirmDialog(null,
                                                panel,
                                                "Preferences",
                                                JOptionPane.OK_CANCEL_OPTION,
                                                JOptionPane.PLAIN_MESSAGE);
        if (ret == JOptionPane.OK_OPTION) {
            panel.applyPreferences();
        }
        panel.dispose();
    }
}
