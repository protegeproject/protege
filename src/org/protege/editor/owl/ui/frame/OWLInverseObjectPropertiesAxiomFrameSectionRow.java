package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;

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
public class OWLInverseObjectPropertiesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLInverseObjectPropertiesAxiom, OWLObjectProperty> {

    private OWLFrameSection section;

    public OWLInverseObjectPropertiesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                          OWLOntology ontology, OWLObjectProperty rootObject,
                                                          OWLInverseObjectPropertiesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);

        this.section = section;
    }


    protected OWLFrameSectionRowObjectEditor<OWLObjectProperty> getObjectEditor() {
        OWLObjectPropertyEditor editor = (OWLObjectPropertyEditor)section.getEditor();
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
    public List<? extends OWLObject> getManipulatableObjects() {
        Set<OWLObjectPropertyExpression> props = new HashSet<OWLObjectPropertyExpression>(getAxiom().getProperties());
        if(props.size() > 1) {
            props.remove(getRootObject());
        }
        return new ArrayList(props);
    }
}
