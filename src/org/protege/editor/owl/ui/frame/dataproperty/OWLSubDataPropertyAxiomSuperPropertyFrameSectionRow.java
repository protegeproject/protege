package org.protege.editor.owl.ui.frame.dataproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.editor.OWLDataPropertyEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.*;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow extends AbstractOWLFrameSectionRow<OWLDataProperty, OWLSubDataPropertyOfAxiom, OWLDataProperty> {

    private OWLFrameSection section;

    public OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                               OWLOntology ontology, OWLDataProperty rootObject,
                                                               OWLSubDataPropertyOfAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
        this.section = section;
    }


    protected OWLSubDataPropertyOfAxiom createAxiom(OWLDataProperty editedObject) {
        return getOWLDataFactory().getOWLSubDataPropertyOfAxiom(getRootObject(), editedObject);
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
