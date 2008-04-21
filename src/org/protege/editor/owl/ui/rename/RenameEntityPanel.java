package org.protege.editor.owl.ui.rename;

import org.apache.log4j.Logger;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLEntity;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;


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

    private static final Logger logger = Logger.getLogger(RenameEntityPanel.class);


    private OWLEditorKit owlEditorKit;

    private OWLEntity owlEntity;

    private JTextField textField;

    private JCheckBox showFullURICheckBox;

    private static boolean showFullURI = false;


    public RenameEntityPanel(OWLEditorKit owlEditorKit, OWLEntity owlEntity) {
        this.owlEditorKit = owlEditorKit;
        this.owlEntity = owlEntity;
        createUI();
    }


    private void createUI() {
        setLayout(new BorderLayout(3, 3));
        textField = new JTextField(50);
        add(textField, BorderLayout.NORTH);
        JPanel checkBoxHolderPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        showFullURICheckBox = new JCheckBox("Show full URI", showFullURI);
        showFullURICheckBox.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                updateTextField();
            }
        });
        checkBoxHolderPanel.add(showFullURICheckBox);
        add(checkBoxHolderPanel, BorderLayout.SOUTH);
        updateTextField();
    }


    /**
     * Gets the URI Fragment for the selected entity.
     * @return The fragment, or the empty string if there
     *         is no fragment.
     */
    private String getFragment() {
        String fragment = owlEntity.getURI().getFragment();
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
            String uriString = owlEntity.getURI().toString();
            return uriString.substring(0, uriString.length() - fragment.length());
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


    public URI getURI() {
        try {
            String newName = textField.getText().trim().replaceAll(" ", "_");
            if (showFullURICheckBox.isSelected()) {
                return new URI(newName);
            }
            else {
                return new URI(getBase() + newName);
            }
        }
        catch (URISyntaxException e) {
            return null;
        }
    }


    public static URI showDialog(OWLEditorKit owlEditorKit, OWLEntity entity) {
        RenameEntityPanel panel = new RenameEntityPanel(owlEditorKit, entity);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JOptionPaneEx.showConfirmDialog(null,
                                        "Rename entity",
                                        panel,
                                        JOptionPane.PLAIN_MESSAGE,
                                        JOptionPane.OK_CANCEL_OPTION,
                                        panel.textField);
        return panel.getURI();
    }
}
