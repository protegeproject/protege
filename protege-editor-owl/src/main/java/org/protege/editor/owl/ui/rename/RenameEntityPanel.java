package org.protege.editor.owl.ui.rename;

import org.protege.editor.core.prefs.PreferencesManager;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 14-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RenameEntityPanel extends JPanel {

    private static final String AUTO_RENAME_PUNS = "AUTO_RENAME_PUNS";

    private OWLEditorKit owlEditorKit;

    private OWLEntity owlEntity;

    private JTextField textField;

    private JCheckBox showFullURICheckBox;

    private JCheckBox renamePunsCheckBox;

    private static boolean showFullURI = false;

    private boolean renamePuns;


    public RenameEntityPanel(OWLEditorKit owlEditorKit, OWLEntity owlEntity) {
        this.owlEditorKit = owlEditorKit;
        this.owlEntity = owlEntity;
        renamePuns = RenameEntityPanel.isAutoRenamePuns();
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout(3, 3));

        textField = new JTextField(50);

        renamePunsCheckBox = new JCheckBox("Change all entities with this URI", renamePuns);
        renamePunsCheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                renamePuns = renamePunsCheckBox.isSelected();
            }
        });

        showFullURICheckBox = new JCheckBox("Show full IRI", showFullURI);
        showFullURICheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTextField();
            }
        });

        JPanel checkBoxHolderPanel = new JPanel(new BorderLayout());
        checkBoxHolderPanel.add(renamePunsCheckBox, BorderLayout.WEST);
        checkBoxHolderPanel.add(showFullURICheckBox, BorderLayout.EAST);

        add(textField, BorderLayout.NORTH);
        add(checkBoxHolderPanel, BorderLayout.SOUTH);

        updateTextField();
    }


    /**
     * Gets the URI Fragment for the selected entity.
     * @return The fragment, or the empty string if there
     *         is no fragment.
     */
    private String getFragment() {
        String fragment = owlEntity.getIRI().getFragment();
        if (fragment != null) {
            return fragment;
        }
        else {
            return "";
        }
    }


    private String getBase() {
        String fragment = getFragment();
        if (fragment != null) {
            try {
                String uriString = URLDecoder.decode(owlEntity.getIRI().toString(), "utf-8");
                return uriString.substring(0, uriString.length() - fragment.length());
            }
            catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
        return "";
    }


    private void updateTextField() {
        showFullURI = showFullURICheckBox.isSelected();
        if (showFullURI) {
            String enteredFragment = textField.getText();
            if (enteredFragment.length() > 0) {
                textField.setText(getBase() + enteredFragment);
            }
            else {
                textField.setText(getBase() + getFragment());
            }
        }
        else {
            // Set fragment
            try {
                URI uri = new URI(textField.getText());
                String fragment = uri.getFragment();
                if (fragment != null) {
                    textField.setText(fragment);
                }
                else {
                    textField.setText(getFragment());
                }
            }
            catch (URISyntaxException e) {
                textField.setText(getFragment());
            }
        }
    }


    public IRI getIRI() {
        try {
            String newName = textField.getText().trim().replaceAll(" ", "_");
            if (showFullURICheckBox.isSelected()) {
                return IRI.create(new URI(newName));
            }
            else {
                return IRI.create(new URI(getBase() + newName));
            }
        }
        catch (URISyntaxException e) {
            return null;
        }
    }


    public boolean getRenamePuns(){
        return renamePuns;
    }


    public static boolean isAutoRenamePuns() {
        return PreferencesManager.getInstance().getApplicationPreferences(RenameEntityPanel.class).getBoolean(AUTO_RENAME_PUNS, false);
    }

    
    public static IRI showDialog(OWLEditorKit owlEditorKit, OWLEntity entity) {
        RenameEntityPanel panel = new RenameEntityPanel(owlEditorKit, entity);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        if (JOptionPaneEx.showConfirmDialog(null,
                                            "Change entity URI",
                                            panel,
                                            JOptionPane.PLAIN_MESSAGE,
                                            JOptionPane.OK_CANCEL_OPTION,
                                            panel.textField) == JOptionPane.OK_OPTION){

            PreferencesManager.getInstance().getApplicationPreferences(RenameEntityPanel.class).putBoolean(AUTO_RENAME_PUNS, panel.getRenamePuns());
            return panel.getIRI();
        }
        return null;
    }
}
