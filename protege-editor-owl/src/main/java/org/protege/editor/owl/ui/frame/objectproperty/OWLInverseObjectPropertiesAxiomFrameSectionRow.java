package org.protege.editor.owl.ui.frame.objectproperty;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLInverseObjectPropertiesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLInverseObjectPropertiesAxiom, OWLObjectProperty> {

    public OWLInverseObjectPropertiesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, 
    													  OWLFrameSection<OWLObjectProperty, OWLInverseObjectPropertiesAxiom, OWLObjectProperty> section,
                                                          OWLOntology ontology, OWLObjectProperty rootObject,
                                                          OWLInverseObjectPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLObjectProperty> getObjectEditor() {
        OWLObjectPropertyEditor editor = new OWLObjectPropertyEditor(getOWLEditorKit());
        OWLObjectPropertyExpression p = axiom.getFirstProperty();
        if (p.equals(getRootObject())){
            p = axiom.getSecondProperty();
        }
        
        if (!p.isAnonymous()){
            editor.setEditedObject(p.asOWLObjectProperty());
        }
        return editor;
    }


    protected OWLInverseObjectPropertiesAxiom createAxiom(OWLObjectProperty editedObject) {
        return getOWLDataFactory().getOWLInverseObjectPropertiesAxiom(getRoot(), editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLObjectPropertyExpression> getManipulatableObjects() {
        Set<OWLObjectPropertyExpression> props = new HashSet<>(getAxiom().getProperties());
        if(props.size() > 1) {
            props.remove(getRootObject());
        }
        return new ArrayList<>(props);
    }
}
