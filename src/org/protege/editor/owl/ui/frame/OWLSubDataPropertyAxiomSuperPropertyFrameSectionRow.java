package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLDataSubPropertyAxiom, OWLDataProperty> {

    private OWLFrameSection section;

    public OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                               OWLOntology ontology, OWLDataProperty rootObject,
                                                               OWLDataSubPropertyAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
        this.section = section;
    }


    protected OWLDataSubPropertyAxiom createAxiom(OWLDataProperty editedObject) {
        return getOWLDataFactory().getOWLSubDataPropertyAxiom(getRootObject(), editedObject);
    }


    protected OWLFrameSectionRowObjectEditor<OWLDataProperty> getObjectEditor() {
        OWLDataPropertyEditor editor = (OWLDataPropertyEditor) section.getEditor();
        OWLDataPropertyExpression p = getAxiom().getSuperProperty();
        if (!p.isAnonymous()){
            editor.setEditedObject(p.asOWLDataProperty());
        }
        return editor;
    }


    public List<? extends OWLObject> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getSuperProperty());
    }
}
