package org.protege.editor.owl.ui.frame;

import java.util.Arrays;
import java.util.List;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLEntity;
import org.semanticweb.owl.model.OWLEntityAnnotationAxiom;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Jan-2007<br><br>
 */
public class OWLAnnotationsFrameSectionRow extends AbstractOWLFrameSectionRow<OWLEntity, OWLEntityAnnotationAxiom, OWLAnnotation> {

    public OWLAnnotationsFrameSectionRow(OWLEditorKit owlEditorKit, OWLFrameSection section, OWLOntology ontology,
                                         OWLEntity rootObject, OWLEntityAnnotationAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected List getObjects() {
        return Arrays.asList(getAxiom().getAnnotation());
    }


    protected OWLFrameSectionRowObjectEditor<OWLAnnotation> getObjectEditor() {
        OWLAnnotationEditor editor = new OWLAnnotationEditor(getOWLEditorKit());
        editor.setAnnotation(getAxiom().getAnnotation());
        return editor;
    }


    protected OWLEntityAnnotationAxiom createAxiom(OWLAnnotation editedObject) {
        return getOWLDataFactory().getOWLEntityAnnotationAxiom(getRootObject(), editedObject);
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List getManipulatableObjects() {
        return getObjects();
    }
}
