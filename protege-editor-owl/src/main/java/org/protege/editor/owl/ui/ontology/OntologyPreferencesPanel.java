package org.protege.editor.owl.ui.ontology;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.net.URI;
import java.net.URISyntaxException;


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

    private static final Logger logger = LoggerFactory.getLogger(OntologyPreferencesPanel.class);



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
        PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
        setLayout(new BorderLayout());
        add(panel, BorderLayout.NORTH);

        OntologyPreferences prefs = OntologyPreferences.getInstance();

        panel.addGroup("Default ontology IRI base");
        panel.addGroupComponent(textField = new JTextField(prefs.getBaseURI().toString(), 40));
        panel.addGroupComponent(yearCheckBox = new JCheckBox("Include year", prefs.isIncludeYear()));
        panel.addGroupComponent(monthCheckBox = new JCheckBox("Include month", prefs.isIncludeMonth()));
        panel.addGroupComponent(dayCheckBox = new JCheckBox("Include day", prefs.isIncludeDay()));
        yearCheckBox.addActionListener(e -> updateState());
        monthCheckBox.addActionListener(e -> updateState());
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
            logger.error("An error occurred whilst trying to display the Ontology Preferences Panel", e);
        }
    }
}
