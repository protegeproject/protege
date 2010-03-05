package org.protege.editor.owl.ui.frame.objectproperty;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyRangeFrameSectionRow extends AbstractOWLFrameSectionRow<OWLObjectProperty, OWLObjectPropertyRangeAxiom, OWLClassExpression> {

    public OWLObjectPropertyRangeFrameSectionRow(OWLEditorKit editorKit, OWLFrameSection section, OWLOntology ontology,
                                                 OWLObjectProperty rootObject, OWLObjectPropertyRangeAxiom axiom) {
        super(editorKit, section, ontology, rootObject, axiom);
    }


    protected OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(getAxiom().getRange(), AxiomType.OBJECT_PROPERTY_RANGE);
    }


    protected OWLObjectPropertyRangeAxiom createAxiom(OWLClassExpression editedObject) {
        return getOWLDataFactory().getOWLObjectPropertyRangeAxiom(getRoot(), editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLClassExpression> getManipulatableObjects() {
        return Arrays.asList(getAxiom().getRange());
    }
}
