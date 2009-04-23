package org.protege.editor.owl.ui.frame.ontology;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.OWLAnnotation;
import org.semanticweb.owl.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owl.model.OWLOntology;

import java.util.Comparator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 04-Feb-2007<br><br>
 */
public class OWLOntologyAnnotationAxiomFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLAnnotationAssertionAxiom, OWLAnnotation> {

    public static final String LABEL = "Annotations";


    public OWLOntologyAnnotationAxiomFrameSection(OWLEditorKit owlEditorKit, OWLFrame<? extends OWLOntology> frame) {
        super(owlEditorKit, LABEL, "Ontology annotation", frame);
    }


    protected OWLAnnotationAssertionAxiom createAxiom(OWLAnnotation object) {
        return getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject().getIRI(), object);
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
                for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(getRootObject().getIRI())) {
            if (!getOWLEditorKit().getWorkspace().isHiddenAnnotationURI(ax.getAnnotation().getProperty().getURI())) {
                addRow(new OWLOntologyAnnotationAxiomFrameSectionRow(getOWLEditorKit(),
                                                                     this,
                                                                     ontology,
                                                                     getRootObject(),
                                                                     ax));
            }
        }
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLOntology, OWLAnnotationAssertionAxiom, OWLAnnotation>> getRowComparator() {
        return null;
    }


    public void visit(OWLAnnotationAssertionAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }
}
