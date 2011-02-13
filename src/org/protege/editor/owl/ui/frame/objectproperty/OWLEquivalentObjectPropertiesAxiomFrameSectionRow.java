package org.protege.editor.owl.ui.frame.objectproperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyExpressionEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLEquivalentObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLEquivalentObjectPropertiesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLEquivalentObjectPropertiesAxiom, OWLObjectPropertyExpression> {

    public OWLEquivalentObjectPropertiesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                             OWLOntology ontology, OWLObjectProperty rootObject,
                                                             OWLEquivalentObjectPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }
    
    @Override
    public boolean isEditable() {
    	return getAxiom().getProperties().size() <= 2;
    }
    
    @Override
    public boolean isDeleteable() {
    	return true;
    }


    protected OWLObjectEditor<OWLObjectPropertyExpression> getObjectEditor() {
        final OWLObjectPropertyExpressionEditor editor = new OWLObjectPropertyExpressionEditor(getOWLEditorKit());
        final Set<OWLObjectPropertyExpression> equivs =
                new HashSet<OWLObjectPropertyExpression>(getAxiom().getProperties());
        equivs.remove(getRootObject());
        if (equivs.size() == 1){
            final OWLObjectPropertyExpression p = equivs.iterator().next();
            editor.setEditedObject(p);
        }
        return editor;    
    }
    
    @Override
    public boolean checkEditorResults(OWLObjectEditor<OWLObjectPropertyExpression> editor) {
    	Set<OWLObjectPropertyExpression> equivalents = editor.getEditedObjects();
    	return equivalents.size() != 1 || !equivalents.contains(getRootObject());
    }
    
    @Override
    public void handleEditingFinished(Set<OWLObjectPropertyExpression> editedObjects) {
    	editedObjects = new HashSet<OWLObjectPropertyExpression>(editedObjects);
    	editedObjects.remove(getRootObject());
    	super.handleEditingFinished(editedObjects);
    }


    protected OWLEquivalentObjectPropertiesAxiom createAxiom(OWLObjectPropertyExpression editedObject) {
        return getOWLDataFactory().getOWLEquivalentObjectPropertiesAxiom(CollectionFactory.createSet(getRoot(),
                                                                                                     editedObject));
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLObjectPropertyExpression> getManipulatableObjects() {
        List<OWLObjectPropertyExpression> props = new ArrayList<OWLObjectPropertyExpression>(getAxiom().getProperties());
        props.remove(getRoot());
        return props;
    }
}
