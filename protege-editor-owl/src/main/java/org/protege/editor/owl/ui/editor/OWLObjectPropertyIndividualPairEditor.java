package org.protege.editor.owl.ui.editor;

import java.awt.BorderLayout;
import java.util.HashSet;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.ListSelectionModel;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.protege.editor.owl.ui.selector.OWLIndividualSelectorPanel;
import org.protege.editor.owl.ui.selector.OWLObjectPropertySelectorPanel;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLPropertyAssertionAxiom;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br>
 * <br>
 */
public class OWLObjectPropertyIndividualPairEditor extends AbstractOWLObjectEditor<OWLObjectPropertyIndividualPair> {

    private JPanel editorPanel;

    private OWLObjectPropertySelectorPanel objectPropertyPanel;

    private OWLIndividualSelectorPanel individualSelectorPanel;


    public OWLObjectPropertyIndividualPairEditor(OWLEditorKit owlEditorKit) {

        objectPropertyPanel = new OWLObjectPropertySelectorPanel(owlEditorKit);
        individualSelectorPanel = new OWLIndividualSelectorPanel(owlEditorKit, ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.setLeftComponent(objectPropertyPanel);
        splitPane.setRightComponent(individualSelectorPanel);

        editorPanel = new JPanel(new BorderLayout());
        editorPanel.add(splitPane);
    }


    public void setObjectPropertyAxiom(OWLPropertyAssertionAxiom<OWLObjectPropertyExpression, OWLIndividual> ax) {

        OWLObjectPropertyExpression p = ax.getProperty();
        // @@TODO support for inverse properties
        if (p instanceof OWLObjectProperty) {
            objectPropertyPanel.setSelection((OWLObjectProperty) p);
        }
        // @@TODO support for anonymous individuals
        if (!ax.getObject().isAnonymous()){
            individualSelectorPanel.setSelection(ax.getObject().asOWLNamedIndividual());
        }
    }


    @Nullable
    public OWLObjectPropertyIndividualPair getEditedObject() {
        return new OWLObjectPropertyIndividualPair(objectPropertyPanel.getSelectedObject(),
                                                   individualSelectorPanel.getSelectedObject());
    }


    public Set<OWLObjectPropertyIndividualPair> getEditedObjects() {

        Set<OWLObjectPropertyIndividualPair> pairs = new HashSet<>();

        for (OWLObjectProperty prop : objectPropertyPanel.getSelectedObjects()) {
            for (OWLIndividual ind : individualSelectorPanel.getSelectedObjects()) {
                pairs.add(new OWLObjectPropertyIndividualPair(prop, ind));
            }
        }
        return pairs;
    }


    public boolean setEditedObject(OWLObjectPropertyIndividualPair editedObject) {
        if (editedObject == null){
            objectPropertyPanel.clearSelection();
            individualSelectorPanel.clearSelection();
        }
        else{
            if (!editedObject.getProperty().isAnonymous() &&
                !editedObject.getIndividual().isAnonymous()){
                objectPropertyPanel.setSelection(editedObject.getProperty().asOWLObjectProperty());
                individualSelectorPanel.setSelection(editedObject.getIndividual().asOWLNamedIndividual());
            }
            else{
                return false;
            }
        }
        return true;
    }


    @Nonnull
    public String getEditorTypeName() {
        return "Object property assertion";
    }


    public boolean canEdit(Object object) {
        return object instanceof OWLObjectPropertyIndividualPair;
    }


    @Nonnull
    public JComponent getEditorComponent() {
        return editorPanel;
    }

    
    public void dispose() {
        objectPropertyPanel.dispose();
        individualSelectorPanel.dispose();
    }
}
