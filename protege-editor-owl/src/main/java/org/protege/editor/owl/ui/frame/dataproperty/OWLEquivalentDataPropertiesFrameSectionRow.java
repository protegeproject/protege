package org.protege.editor.owl.ui.frame.dataproperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertyEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLEquivalentDataPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.CollectionFactory;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLEquivalentDataPropertiesFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLEquivalentDataPropertiesAxiom, OWLDataProperty> {

    public OWLEquivalentDataPropertiesFrameSectionRow(OWLEditorKit owlEditorKit, 
    												  OWLFrameSection<OWLDataProperty, OWLEquivalentDataPropertiesAxiom, OWLDataProperty> section,
                                                      OWLOntology ontology, OWLDataProperty rootObject,
                                                      OWLEquivalentDataPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLEquivalentDataPropertiesAxiom createAxiom(OWLDataProperty editedObject) {
        return getOWLDataFactory().getOWLEquivalentDataPropertiesAxiom(CollectionFactory.createSet(getRoot(),
                editedObject));
    }

    @Override
    public boolean isEditable() {
    	return getAxiom().getProperties().size() <= 2;
    }
    
    @Override
    public boolean isDeleteable() {
    	return true;
    }

    protected OWLObjectEditor<OWLDataProperty> getObjectEditor() {
        final OWLDataPropertyEditor editor = new OWLDataPropertyEditor(getOWLEditorKit());
        final Set<OWLDataPropertyExpression> equivs =
                new HashSet<>(getAxiom().getProperties());
        equivs.remove(getRootObject());
        if (equivs.size() == 1){
            final OWLDataPropertyExpression p = equivs.iterator().next();
            if (!p.isAnonymous()){
                editor.setEditedObject(p.asOWLDataProperty());
            }
        }
        return editor;
    }
    
    @Override
    public boolean checkEditorResults(OWLObjectEditor<OWLDataProperty> editor) {
    	Set<OWLDataProperty> equivalents = editor.getEditedObjects();
    	return equivalents.size() != 1 || !equivalents.contains(getRootObject());
    }
    
    @Override
    public void handleEditingFinished(Set<OWLDataProperty> editedObjects) {
    	editedObjects = new HashSet<>(editedObjects);
    	editedObjects.remove(getRootObject());
    	super.handleEditingFinished(editedObjects);
    }


    public List<OWLDataPropertyExpression> getManipulatableObjects() {
        List<OWLDataPropertyExpression> props = new ArrayList<>(getAxiom().getProperties());
        props.remove(getRoot());
        return props;
    }
}
