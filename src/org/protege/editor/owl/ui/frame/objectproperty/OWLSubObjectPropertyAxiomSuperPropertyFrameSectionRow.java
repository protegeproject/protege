package org.protege.editor.owl.ui.frame.objectproperty;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyExpressionEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLSubObjectPropertyOfAxiom, OWLObjectPropertyExpression> {

    public OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                                 OWLOntology ontology, OWLObjectProperty rootObject,
                                                                 OWLSubObjectPropertyOfAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLObjectPropertyExpression> getObjectEditor() {
        OWLObjectPropertyExpressionEditor editor = new OWLObjectPropertyExpressionEditor(getOWLEditorKit());
        OWLObjectPropertyExpression p = getAxiom().getSuperProperty();
        editor.setEditedObject(p);
        return editor;
    }


    protected OWLSubObjectPropertyOfAxiom createAxiom(OWLObjectPropertyExpression editedObject) {
        return getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(getRoot(), editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLObjectPropertyExpression> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getSuperProperty());
    }
}
