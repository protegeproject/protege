package org.protege.editor.core.ui.preferences;

import org.apache.log4j.Logger;
import org.protege.editor.core.editorkit.EditorKit;

import javax.swing.*;
import java.awt.*;
import java.util.*;
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
