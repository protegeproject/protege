package org.protege.editor.owl.ui.tree;

import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.core.ui.util.ComponentFactory;

import javax.swing.*;
import java.awt.*;
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
 * Bio-Health Informatics Group<br>
 * Date: 18-Oct-2007<br><br>
 */
public class OWLTreePreferencesPanel extends OWLPreferencesPanel {

    private JCheckBox autoExpandEnabledCheckBox;

    private JSpinner autoExpandMaxDepthSpinner;

    private JSpinner autoExpandMaxChildSizeSpinner;

    public void initialise() throws Exception {
        OWLTreePreferences prefs = OWLTreePreferences.getInstance();
        autoExpandEnabledCheckBox = new JCheckBox("Automatically expand hierarchies", prefs.isAutoExpandEnabled());
        autoExpandMaxDepthSpinner = new JSpinner(new SpinnerNumberModel(prefs.getAutoExpansionDepthLimit(), 1, Integer.MAX_VALUE, 1));
        autoExpandMaxChildSizeSpinner = new JSpinner(new SpinnerNumberModel(prefs.getAutoExpansionChildLimit(), 1, Integer.MAX_VALUE, 1));
        JPanel autoExpansionPanel = new JPanel();
        PreferencesPanelLayoutManager layout = new PreferencesPanelLayoutManager(autoExpansionPanel);
        autoExpansionPanel.setLayout(layout);
        autoExpansionPanel.add("", autoExpandEnabledCheckBox);
        autoExpansionPanel.add("Auto-expansion depth limit", autoExpandMaxDepthSpinner);
        autoExpansionPanel.add("Auto-expansion child count limit", autoExpandMaxChildSizeSpinner);
        autoExpansionPanel.setBorder(ComponentFactory.createTitledBorder("Automatic Hierarchy Expansion"));
        setLayout(new BorderLayout());
        add(autoExpansionPanel, BorderLayout.CENTER);
    }

    public void applyChanges() {
        OWLTreePreferences prefs = OWLTreePreferences.getInstance();
        prefs.setAutoExpansionEnabled(autoExpandEnabledCheckBox.isSelected());
        prefs.setAutoExpansionDepthLimit((Integer)autoExpandMaxDepthSpinner.getValue());
        prefs.setAutoExpansionChildLimit((Integer) autoExpandMaxChildSizeSpinner.getValue());
    }

    public void dispose() throws Exception {
    }

}
