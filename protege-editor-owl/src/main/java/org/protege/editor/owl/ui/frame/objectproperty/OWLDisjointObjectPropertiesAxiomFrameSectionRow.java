package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyTabbedSetEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLDisjointObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLDisjointObjectPropertiesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLDisjointObjectPropertiesAxiom, Set<OWLObjectPropertyExpression>> {


    public OWLDisjointObjectPropertiesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, 
    													   OWLFrameSection<OWLObjectProperty, OWLDisjointObjectPropertiesAxiom, Set<OWLObjectPropertyExpression>> section,
                                                           OWLOntology ontology, OWLObjectProperty rootObject,
                                                           OWLDisjointObjectPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<Set<OWLObjectPropertyExpression>> getObjectEditor() {
        OWLObjectPropertyTabbedSetEditor editor = new OWLObjectPropertyTabbedSetEditor(getOWLEditorKit());
        final Set<OWLObjectPropertyExpression> disjoints = new HashSet<>(getAxiom().getProperties());
        disjoints.remove(getRootObject());
        editor.setEditedObject(disjoints);
        return editor;
    }


    protected OWLDisjointObjectPropertiesAxiom createAxiom(Set<OWLObjectPropertyExpression> editedObject) {
        Set<OWLObjectPropertyExpression> props = new HashSet<>();
        props.add(getRootObject());
        props.addAll(editedObject);
        return getOWLDataFactory().getOWLDisjointObjectPropertiesAxiom(props);
    }

    @Override
    public boolean checkEditorResults(OWLObjectEditor<Set<OWLObjectPropertyExpression>> editor) {
    	Set<OWLObjectPropertyExpression> equivalents = editor.getEditedObject();
    	return !equivalents.contains(getRootObject());
    }

    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLObjectPropertyExpression> getManipulatableObjects() {
        List<OWLObjectPropertyExpression> props = new ArrayList<>(getAxiom().getProperties());
        props.remove(getRoot());
        return props;
    }
}

