package org.protege.editor.owl.ui.frame.ontology;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLOntologyAnnotationAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLOntology, OWLAnnotationAssertionAxiom, OWLAnnotation> {

    public OWLOntologyAnnotationAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                     OWLOntology ontology, OWLOntology rootObject,
                                                     OWLAnnotationAssertionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLAnnotation> getObjectEditor() {
        OWLAnnotationEditor editor = new OWLAnnotationEditor(getOWLEditorKit());
        editor.setAnnotation(getAxiom().getAnnotation());
        return editor;
    }


    protected OWLAnnotationAssertionAxiom createAxiom(OWLAnnotation editedObject) {
        return getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject().getIRI(), editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        return Arrays.asList(getAxiom().getAnnotation());
    }
}
