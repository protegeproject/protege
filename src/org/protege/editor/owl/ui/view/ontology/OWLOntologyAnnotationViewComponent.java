package org.protege.editor.owl.ui.view.ontology;

import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.core.ui.util.AugmentedJTextField;
import org.protege.editor.core.ui.util.LinkLabel;
import org.protege.editor.owl.model.OntologyAnnotationContainer;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.ontology.annotation.OWLOntologyAnnotationList;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.OWLOntologyChangeVisitorAdapter;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLOntologyAnnotationViewComponent extends AbstractOWLViewComponent {


    private static final long serialVersionUID = 1252038674995535772L;

    public static final String ONTOLOGY_IRI_FIELD_LABEL = "Ontology IRI";

    public static final String ONTOLOGY_VERSION_IRI_FIELD_LABEL = "Ontology Version IRI";
    
    
    public static final URI ONTOLOGY_IRI_DOCUMENTATION = URI.create("http://www.w3.org/TR/2009/REC-owl2-syntax-20091027/#Ontology_IRI_and_Version_IRI");

    public static final URI VERSION_IRI_DOCUMENTATION = URI.create("http://www.w3.org/TR/2009/REC-owl2-syntax-20091027/#Versioning_of_OWL_2_Ontologies");



    private OWLModelManagerListener listener;

    private OWLOntologyAnnotationList list;

    private final AugmentedJTextField ontologyIRIField = new AugmentedJTextField("e.g http://www.example.com/ontologies/myontology");

    private final AugmentedJTextField ontologyVersionIRIField = new AugmentedJTextField("e.g. http://www.example.com/ontologies/myontology/1.0.0");
    
    
    
    private boolean updatingViewFromModel = false;
    
    private boolean updatingModelFromView = false;
    
    
    
    
    
    
    

    private final OWLOntologyChangeListener ontologyChangeListener = new OWLOntologyChangeListener() {
        public void ontologiesChanged(List<? extends OWLOntologyChange> owlOntologyChanges) throws OWLException {
            handleOntologyChanges(owlOntologyChanges);
        }
    };


    protected void initialiseOWLView() throws Exception {
        setLayout(new BorderLayout());

        setLayout(new BorderLayout());
        JPanel ontologyIRIPanel = new JPanel(new GridBagLayout());
        add(ontologyIRIPanel, BorderLayout.NORTH);
        Insets insets = new Insets(0, 4, 2, 0);
        ontologyIRIPanel.add(new LinkLabel(ONTOLOGY_IRI_FIELD_LABEL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showOntologyIRIDocumentation();
            }
        }), new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
        ontologyIRIPanel.add(ontologyIRIField, new GridBagConstraints(1, 0, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, insets, 0, 0));
        ontologyIRIField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateModelFromView();
            }

            public void removeUpdate(DocumentEvent e) {
                updateModelFromView();
            }

            public void changedUpdate(DocumentEvent e) {

            }
        });
        
        ontologyIRIPanel.add(new LinkLabel(ONTOLOGY_VERSION_IRI_FIELD_LABEL, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showVersionIRIDocumentation();
            }
        }), new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0, GridBagConstraints.BASELINE_TRAILING, GridBagConstraints.NONE, insets, 0, 0));
        
        ontologyIRIPanel.add(ontologyVersionIRIField, new GridBagConstraints(1, 1, 1, 1, 100.0, 0.0, GridBagConstraints.BASELINE_LEADING, GridBagConstraints.HORIZONTAL, insets, 0, 0));

        ontologyVersionIRIField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateModelFromView();
            }

            public void removeUpdate(DocumentEvent e) {
                updateModelFromView();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
        
        
        ontologyIRIPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));


        list = new OWLOntologyAnnotationList(getOWLEditorKit());
        add(new JScrollPane(list));
        list.setRootObject(new OntologyAnnotationContainer(activeOntology()));
        listener = new OWLModelManagerListener() {
            public void handleChange(OWLModelManagerChangeEvent event) {
                if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED) || event.isType(EventType.ONTOLOGY_RELOADED) || event.isType(EventType.ONTOLOGY_SAVED)) {
                    updateView();
                }
            }
        };
        getOWLModelManager().addListener(listener);

        getOWLModelManager().addOntologyChangeListener(ontologyChangeListener);
    }

    private void showVersionIRIDocumentation() {
        try {
            Desktop.getDesktop().browse(VERSION_IRI_DOCUMENTATION);
        }
        catch (IOException ex) {
            ErrorLogPanel.showErrorDialog(ex);
        }
    }

    private void showOntologyIRIDocumentation() {
        try {
            Desktop.getDesktop().browse(ONTOLOGY_IRI_DOCUMENTATION);
        }
        catch (IOException ex) {
            ErrorLogPanel.showErrorDialog(ex);
        }
    }

    /**
     * Updates the view from the model - unless the changes were triggered by changes in the view.
     */
    private void updateViewFromModel() {
//        if(updatingModelFromView) {
//            ontologyVersionIRIField.repaint();
//            ontologyIRIField.repaint();
//            return;
//        }
        updatingViewFromModel = true;
        try {
            OWLOntology activeOntology = getOWLEditorKit().getOWLModelManager().getActiveOntology();
            if (activeOntology.isAnonymous()) {
                if (!ontologyIRIField.getText().isEmpty()) {
                    ontologyIRIField.setText("");
                    if (ontologyVersionIRIField.getText().isEmpty()) {
                        ontologyVersionIRIField.setText("");
                    }
                }
            }
            else {
                OWLOntologyID id = activeOntology.getOntologyID();
    
                IRI ontologyIRI = id.getOntologyIRI();
                String ontologyIRIString = ontologyIRI.toString();
                if (ontologyIRI != null) {
                    if (!ontologyIRIField.getText().equals(ontologyIRIString)) {
                        ontologyIRIField.setText(ontologyIRIString);
                    }
                }
    
                IRI versionIRI = id.getVersionIRI();
                if (versionIRI != null) {
                    String versionIRIString = versionIRI.toString();
                    if (!ontologyVersionIRIField.getText().equals(versionIRIString)) {
                        ontologyVersionIRIField.setText(versionIRIString);
                    }
                }
                else {
                    ontologyVersionIRIField.setText("");
                    if(ontologyIRI != null) {
                        ontologyVersionIRIField.setGhostText("e.g. " + ontologyIRIString + (ontologyIRIString.endsWith("/") ? "1.0.0" : "/1.0.0"));
                    }
                }
            }
        }
        finally {
            updatingViewFromModel = false;
        }
    }

    /**
     * Updates the model from the view - unless the changes in the view were triggered by changes in the model.
     */
    private void updateModelFromView() {
        if(updatingViewFromModel) {
            return;
        }
        try {
            updatingModelFromView = true;
            OWLOntologyID id = createOWLOntologyIDFromView();
            if (id != null && !activeOntology().getOntologyID().equals(id)) {
                getOWLModelManager().applyChange(new SetOntologyID(activeOntology(), id));
            }
        }
        finally {
            updatingModelFromView = false;
        }

    }

    private OWLOntology activeOntology() {
        return getOWLModelManager().getActiveOntology();
    }


    private OWLOntologyID createOWLOntologyIDFromView() {
        try {
            ontologyIRIField.clearErrorMessage();
            ontologyIRIField.clearErrorLocation();
            String ontologyIRIString = ontologyIRIField.getText().trim();
            if (ontologyIRIString.isEmpty()) {
                return new OWLOntologyID();
            }
            URI ontURI = new URI(ontologyIRIString);
            IRI ontologyIRI = IRI.create(ontURI);
            String versionIRIString = ontologyVersionIRIField.getText().trim();
            if (versionIRIString.isEmpty()) {
                return new OWLOntologyID(ontologyIRI);
            }
            
            URI verURI = new URI(versionIRIString);
            IRI versionIRI = IRI.create(verURI);
            return new OWLOntologyID(ontologyIRI, versionIRI);
        }
        catch (URISyntaxException e) {
            ontologyIRIField.setErrorMessage(e.getReason());
            ontologyIRIField.setErrorLocation(e.getIndex());
            return null;
        }
    }


    private void handleOntologyChanges(List<? extends OWLOntologyChange> changes) {
        for (OWLOntologyChange change : changes) {
            change.accept(new OWLOntologyChangeVisitorAdapter() {
                @Override
                public void visit(SetOntologyID change) {
                    updateView();
                }
            });
        }
    }

    private void updateView() {
        list.setRootObject(new OntologyAnnotationContainer(activeOntology()));
        updateViewFromModel();
    }


    protected void disposeOWLView() {
        list.dispose();
        getOWLModelManager().removeListener(listener);
        getOWLModelManager().removeOntologyChangeListener(ontologyChangeListener);
    }

}
