package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.semanticweb.owlapi.model.OWLAnnotation;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLAnnotationSubject;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Arrays;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Jan-2007<br><br>
 */
public class OWLAnnotationsFrameSectionRow extends AbstractOWLFrameSectionRow<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> {

    public OWLAnnotationsFrameSectionRow(OWLEditorKit owlEditorKit, 
    									 OWLFrameSection<OWLAnnotationSubject, OWLAnnotationAssertionAxiom, OWLAnnotation> section, 
    									 OWLOntology ontology,
                                         OWLAnnotationSubject rootObject, OWLAnnotationAssertionAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected List<OWLAnnotation> getObjects() {
        return Arrays.asList(getAxiom().getAnnotation());
    }


    protected OWLObjectEditor<OWLAnnotation> getObjectEditor() {
        OWLAnnotationEditor editor = new OWLAnnotationEditor(getOWLEditorKit());
        editor.setEditedObject(getAxiom().getAnnotation());
        return editor;
    }


    protected OWLAnnotationAssertionAxiom createAxiom(OWLAnnotation editedObject) {
        return getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject(), editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLAnnotation> getManipulatableObjects() {
        return getObjects();
    }
}
