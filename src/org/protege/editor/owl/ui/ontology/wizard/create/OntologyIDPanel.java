package org.protege.editor.owl.ui.ontology.wizard.create;

import org.protege.editor.core.ui.wizard.AbstractWizardPanel;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.ontology.OntologyPreferences;
import org.protege.editor.owl.ui.ontology.OntologyPreferencesPanel;
import org.semanticweb.owl.model.IRI;
import org.semanticweb.owl.model.OWLOntologyID;

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

    public static final String ID = "ONTOLOGY_ID_PANEL";

    private JTextField ontologyIRIField;

    private JTextField versionIRIField;


    public OntologyIDPanel(OWLEditorKit editorKit) {
        super(ID, "Ontology ID", null);
    }


    private void updateState() {
        getWizard().setNextFinishButtonEnabled(isValidData());
    }


    private boolean isValidData() {
        try {
            final String ontTxt = ontologyIRIField.getText();
            final String versionTxt = versionIRIField.getText();
            if (ontTxt == null){
                return versionTxt == null; // cannot have a version IRI without an ontology IRI
            }
            else{
                URI ontologyURI = new URI(ontTxt);
                if (versionTxt != null){
                    URI versionURI = new URI(versionTxt);
                    return ontologyURI.isAbsolute() && versionURI.isAbsolute();
                }
                return ontologyURI.isAbsolute();
            }
        }
        catch (URISyntaxException e) {
        }
        return false;
    }


    protected void createUI(JComponent parent) {
        setInstructions("Please specify the ontology IRI.  \n\nThe ontology IRI is used to identify" +
                        " the ontology in the context of the world wide web. Additionally, ontologies that" +
                        " import this ontology will use the IRI for the import.  It is recommended that you " +
                        " set the ontology IRI to be the URL where the ontology will be published.");

        ontologyIRIField = new JTextField(OntologyPreferences.getInstance().generateURI().toString());
        ontologyIRIField.setSelectionStart(ontologyIRIField.getText().lastIndexOf("/") + 1);
        ontologyIRIField.setSelectionEnd(ontologyIRIField.getText().lastIndexOf(".owl"));
        ontologyIRIField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
            }

            public void insertUpdate(DocumentEvent e) {
                versionIRIField.setText(ontologyIRIField.getText());
                updateState();
            }

            public void removeUpdate(DocumentEvent e) {
                versionIRIField.setText(ontologyIRIField.getText());
                updateState();
            }
        });

        versionIRIField = new JTextField(ontologyIRIField.getText());
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

        JButton but = new JButton("Default base...");
        but.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OntologyPreferencesPanel.showDialog(OntologyIDPanel.this);
                ontologyIRIField.setText(OntologyPreferences.getInstance().generateURI().toString());
            }
        });

        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(but, BorderLayout.EAST);

        Box holderPanel = new Box(BoxLayout.PAGE_AXIS);
        holderPanel.add(new JLabel("Ontology IRI"));
        holderPanel.add(ontologyIRIField);
        holderPanel.add(Box.createVerticalStrut(12));
        holderPanel.add(new JLabel("Version IRI"));
        holderPanel.add(versionIRIField);

        parent.setLayout(new BorderLayout());
        parent.add(holderPanel, BorderLayout.NORTH);
        parent.add(buttonPanel, BorderLayout.SOUTH);
    }


    public OWLOntologyID getOntologyID() {
        try {
            URI ontologyURI = new URI(ontologyIRIField.getText());
            IRI ontologyIRI = IRI.create(ontologyURI);

            URI versionURI = new URI(versionIRIField.getText());
            IRI versionIRI = IRI.create(versionURI);

            return new OWLOntologyID(ontologyIRI, versionIRI);
        }
        catch (URISyntaxException e) {
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
