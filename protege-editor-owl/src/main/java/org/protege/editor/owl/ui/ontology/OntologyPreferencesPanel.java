package org.protege.editor.owl.ui.ontology;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;

import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;

import org.slf4j.Logger;
import org.protege.editor.core.ui.preferences.PreferencesPanelLayoutManager;
import org.protege.editor.owl.ui.UIHelper;
import org.protege.editor.owl.ui.preferences.OWLPreferencesPanel;
import org.slf4j.LoggerFactory;


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
            logger.error("An error occurred whilst trying to display the Ontology Preferences Panel", e);
        }
    }
}
