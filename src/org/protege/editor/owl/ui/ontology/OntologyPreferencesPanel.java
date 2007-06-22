package org.protege.editor.owl.ui.ontology;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;
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
 * Date: 28-Jun-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyPreferencesPanel extends OWLPreferencesPanel {

    private static final Logger logger = Logger.getLogger(OntologyPreferencesPanel.class);


    private JTextField textField;

    private JCheckBox yearCheckBox;

    private JCheckBox monthCheckBox;

    private JCheckBox dayCheckBox;

    private JTextComponent previewLabel;


    public void applyChanges() {
        OntologyPreferences prefs = OntologyPreferences.getInstance();
        try {
            prefs.setBaseURI(new URI(textField.getText()));
            prefs.setIncludeYear(yearCheckBox.isSelected());
            prefs.setIncludeMonth(monthCheckBox.isSelected());
            prefs.setIncludeDay(dayCheckBox.isSelected());
        }
        catch (URISyntaxException e) {
            UIHelper uiHelper = new UIHelper(getOWLEditorKit());
            uiHelper.showOptionPane("Error",
                                    "Couldn't set base URI: " + e.getMessage(),
                                    JOptionPane.OK_OPTION,
                                    JOptionPane.ERROR_MESSAGE);
        }
    }


    public void initialise() throws Exception {
        OntologyPreferences prefs = OntologyPreferences.getInstance();

        setLayout(new PreferencesPanelLayoutManager(this));
        add(textField = new JTextField(prefs.getBaseURI().toString(), 40), "Default base URI");
        add(yearCheckBox = new JCheckBox("Include year", prefs.isIncludeYear()));
        add(monthCheckBox = new JCheckBox("Include month", prefs.isIncludeMonth()));
        add(dayCheckBox = new JCheckBox("Include day", prefs.isIncludeDay()));
        yearCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateState();
            }
        });
        monthCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateState();
            }
        });
//        previewLabel = new JTextField(50);
//        previewLabel.setBackground(Color.WHITE);
//        previewLabel.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 1, Color.LIGHT_GRAY));

        updateState();
    }


    private void updateState() {
        if (yearCheckBox.isSelected()) {
            monthCheckBox.setEnabled(true);
        }
        else {
            monthCheckBox.setSelected(false);
            monthCheckBox.setEnabled(false);
        }
        if (monthCheckBox.isSelected()) {
            dayCheckBox.setEnabled(true);
        }
        else {
            dayCheckBox.setSelected(false);
            dayCheckBox.setEnabled(false);
        }
    }


    public void dispose() {
    }


    public static void showDialog(Component parent) {
        try {
            OntologyPreferencesPanel panel = new OntologyPreferencesPanel();
            panel.setup("Ontology URI Preferences", null);
            panel.initialise();
            panel.setPreferredSize(new Dimension(600, 300));
            int ret = JOptionPane.showConfirmDialog(parent,
                                                    panel,
                                                    "Default Ontology URI Base",
                                                    JOptionPane.OK_CANCEL_OPTION,
                                                    JOptionPane.PLAIN_MESSAGE);
            if (ret == JOptionPane.OK_OPTION) {
                panel.applyChanges();
            }
        }
        catch (Exception e) {
            logger.error(e);
        }
    }
}
