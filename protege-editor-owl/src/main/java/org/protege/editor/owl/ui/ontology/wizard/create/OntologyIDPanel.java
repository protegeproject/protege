package org.protege.editor.owl.ui.ontology.wizard.create;

import com.google.common.base.Optional;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.OntologyPreferences;
import org.protege.editor.owl.ui.ontology.OntologyPreferencesPanel;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLOntologyID;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;
import java.net.URISyntaxException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 12-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OntologyIDPanel extends AbstractWizardPanel {

    public static final String INSTRUCTIONS = "Please specify the ontology IRI.  \n\nThe ontology IRI is used to identify" +
            " the ontology in the context of the world wide web. It is recommended that you " +
            " set the ontology IRI to be the URL where the latest version of the ontology" +
            " will be published.  If you use a version IRI, then it is recommended that you" +
            " set the version IRI to be the URL where this version of the ontology" +
            " will be published.";

    public static final String ID = "ONTOLOGY_ID_PANEL";

    private AugmentedJTextField ontologyIRIField;

//    private JCheckBox enableVersionCheckBox;

    private AugmentedJTextField versionIRIField;


    public OntologyIDPanel(OWLEditorKit editorKit) {
        super(ID, "Ontology ID", null);
    }


    private void updateState() {
        getWizard().setNextFinishButtonEnabled(isValidData());
    }


    private boolean isValidData() {
        final String ontTxt = getOntologyIRIString();
        final String versionTxt = getOntologyVersionIRIString();
        if (ontTxt.isEmpty()) {
            return versionTxt.isEmpty(); // cannot have a version IRI without an ontology IRI
        }
        try {
            URI ontologyURI = new URI(ontTxt);
            ontologyIRIField.clearErrorMessage();
            ontologyIRIField.clearErrorLocation();
            try {
                if (!versionTxt.isEmpty()) {
                    URI versionURI = new URI(versionTxt);
                    versionIRIField.clearErrorMessage();
                    versionIRIField.clearErrorLocation();
                    return ontologyURI.isAbsolute() && versionURI.isAbsolute();
                }
            } catch (URISyntaxException e) {
                versionIRIField.setErrorMessage(e.getMessage());
                versionIRIField.setErrorLocation(e.getIndex());
                versionIRIField.setToolTipText(e.getMessage());
                return false;
            }
            return ontologyURI.isAbsolute();
        } catch (URISyntaxException e) {
            ontologyIRIField.setErrorLocation(e.getIndex());
            ontologyIRIField.setErrorMessage(e.getMessage());
            ontologyIRIField.setToolTipText(e.getMessage());
            return false;
        }
    }

    private String getOntologyVersionIRIString() {
        return versionIRIField.getText().trim();
    }

    private String getOntologyIRIString() {
        return ontologyIRIField.getText().trim();
    }


    protected void createUI(JComponent parent) {
        setInstructions(INSTRUCTIONS);

        ontologyIRIField = new AugmentedJTextField(OntologyPreferences.getInstance().generateURI().toString(), "Enter ontology IRI");
        ontologyIRIField.setSelectionStart(getOntologyIRIString().lastIndexOf("/") + 1);
        ontologyIRIField.setSelectionEnd(getOntologyIRIString().lastIndexOf(".owl"));
        ontologyIRIField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }

            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            public void removeUpdate(DocumentEvent e) {
                updateState();
            }
        });

        versionIRIField = new AugmentedJTextField("Enter version IRI e.g. " + getOntologyIRIString());
//        versionIRIField.setEnabled(false);
        versionIRIField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }

            public void insertUpdate(DocumentEvent e) {
                updateState();
            }

            public void removeUpdate(DocumentEvent e) {
                updateState();
            }
        });

        JButton but = new JButton("Default base...");
        but.addActionListener(e -> {
            OntologyPreferencesPanel.showDialog(OntologyIDPanel.this);
            ontologyIRIField.setText(OntologyPreferences.getInstance().generateURI().toString());
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(but, BorderLayout.EAST);

        JPanel holderPanel = new JPanel(new GridBagLayout());

        Insets insets = new Insets(0, 0, 0, 0);
        holderPanel.add(createLabel("Ontology IRI"),
                new GridBagConstraints(0, 0, 1, 1, 100, 100, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 0));

        holderPanel.add(ontologyIRIField,
                new GridBagConstraints(0, 1, 1, 1, 100, 100, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));


        holderPanel.add(Box.createVerticalStrut(12),
                new GridBagConstraints(0, 2, 1, 1, 100, 100, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, insets, 0, 0));


//        holderPanel.add(enableVersionCheckBox,
//                new GridBagConstraints(0, 3, 1, 1, 100, 100, GridBagConstraints.WEST, GridBagConstraints.NONE, insets, 0, 10));


        Insets versionIRIInsets = new Insets(0, 0, 0, 0);

        holderPanel.add(createLabel("Ontology Version IRI (Optional)"),
                new GridBagConstraints(0, 4, 1, 1, 100, 100, GridBagConstraints.WEST, GridBagConstraints.NONE, versionIRIInsets, 0, 5));


        holderPanel.add(versionIRIField,
                new GridBagConstraints(0, 5, 1, 1, 100, 100, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, versionIRIInsets, 0, 0));


        parent.setLayout(new BorderLayout());
        parent.add(holderPanel, BorderLayout.NORTH);
        parent.add(buttonPanel, BorderLayout.SOUTH);

    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD));
        return label;
    }


    public OWLOntologyID getOntologyID() {
        try {
            URI ontologyURI = new URI(getOntologyIRIString());
            IRI ontologyIRI = IRI.create(ontologyURI);

            String ontologyVersionIRIString = getOntologyVersionIRIString();
            if(ontologyVersionIRIString.isEmpty()) {
                return new OWLOntologyID(Optional.of(ontologyIRI), Optional.<IRI>absent());
            }
            else {
                URI versionURI = new URI(ontologyVersionIRIString);
                IRI versionIRI = IRI.create(versionURI);
                return new OWLOntologyID(Optional.of(ontologyIRI), Optional.of(versionIRI));
            }
        } catch (URISyntaxException e) {
            return null;
        }
    }

    public Object getNextPanelDescriptor() {
        return PhysicalLocationPanel.ID;
    }


    public void displayingPanel() {
        ontologyIRIField.requestFocus();
        updateState();
    }
}
