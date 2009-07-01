package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertySetEditor;
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
public class OWLDisjointObjectPropertiesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLDisjointObjectPropertiesAxiom, Set<OWLObjectProperty>> {


    public OWLDisjointObjectPropertiesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                           OWLOntology ontology, OWLObjectProperty rootObject,
                                                           OWLDisjointObjectPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<Set<OWLObjectProperty>> getObjectEditor() {
        OWLObjectPropertySetEditor editor = new OWLObjectPropertySetEditor(getOWLEditorKit());
        final Set<OWLObjectPropertyExpression> disjoints = getAxiom().getProperties();
        final Set<OWLObjectProperty> namedDisjoints = new HashSet<OWLObjectProperty>();
        for (OWLObjectPropertyExpression p : disjoints){
            if (!p.isAnonymous()){
                namedDisjoints.add(p.asOWLObjectProperty());
            }
        }
        namedDisjoints.remove(getRootObject());
        editor.setEditedObject(namedDisjoints);
        // @@TODO handle property expressions
        return editor;
    }


    protected OWLDisjointObjectPropertiesAxiom createAxiom(Set<OWLObjectProperty> editedObject) {
        Set<OWLObjectProperty> props = new HashSet<OWLObjectProperty>();
        props.add(getRootObject());
        props.addAll(editedObject);
        return getOWLDataFactory().getOWLDisjointObjectPropertiesAxiom(props);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        List<OWLObjectPropertyExpression> props = new ArrayList<OWLObjectPropertyExpression>(getAxiom().getProperties());
        props.remove(getRoot());
        return props;
    }
}

