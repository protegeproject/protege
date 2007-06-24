package org.protege.editor.owl.ui.frame;

import java.util.Comparator;

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
public class OWLOntologyAnnotationAxiomFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLOntologyAnnotationAxiom, OWLAnnotation> {

    public static final String LABEL = "Annotations";


    public OWLOntologyAnnotationAxiomFrameSection(OWLEditorKit owlEditorKit, OWLFrame<? extends OWLOntology> frame) {
        super(owlEditorKit, LABEL, frame);
    }


    protected OWLOntologyAnnotationAxiom createAxiom(OWLAnnotation object) {
        return getOWLDataFactory().getOWLOntologyAnnotationAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLAnnotation> getObjectEditor() {
        return new OWLAnnotationEditor(getOWLEditorKit());
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLOntologyAnnotationAxiom ax : getRootObject().getAnnotations(ontology)) {
            addRow(new OWLOntologyAnnotationAxiomFrameSectionRow(getOWLEditorKit(),
                                                                 this,
                                                                 ontology,
                                                                 getRootObject(),
                                                                 ax));
        }
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLOntology, OWLOntologyAnnotationAxiom, OWLAnnotation>> getRowComparator() {
        return null;
    }


    public void visit(OWLOntologyAnnotationAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }
}
