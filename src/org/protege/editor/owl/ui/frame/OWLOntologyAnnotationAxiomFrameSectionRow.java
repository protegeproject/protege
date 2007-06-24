package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyAnnotationAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLOntologyAnnotationAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLOntology, OWLOntologyAnnotationAxiom, OWLAnnotation> {

    public OWLOntologyAnnotationAxiomFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section,
                                                     OWLOntology ontology, OWLOntology rootObject,
                                                     OWLOntologyAnnotationAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected OWLFrameSectionRowObjectEditor<OWLAnnotation> getObjectEditor() {
        OWLAnnotationEditor editor = new OWLAnnotationEditor(getOWLEditorKit());
        editor.setAnnotation(getAxiom().getAnnotation());
        return editor;
    }


    protected OWLOntologyAnnotationAxiom createAxiom(OWLAnnotation editedObject) {
        return getOWLDataFactory().getOWLOntologyAnnotationAxiom(getRootObject(), editedObject);
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
