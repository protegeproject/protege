package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.OWLObjectComparator;
import org.semanticweb.owl.model.*;

import java.util.Comparator;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Jan-2007<br><br>
 */
public class OWLAnnotationFrameSection extends AbstractOWLFrameSection<OWLEntity, OWLEntityAnnotationAxiom, OWLAnnotation> {

    private static final String LABEL = "Annotations";

    private static OWLAnnotationSectionRowComparator comparator;


    public OWLAnnotationFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLEntity> frame) {
        super(editorKit, LABEL, frame);
        comparator = new OWLAnnotationSectionRowComparator(editorKit.getOWLModelManager());
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        boolean hidden = false;
        for (OWLEntityAnnotationAxiom ax : ontology.getEntityAnnotationAxioms(getRootObject())) {
            if (!getOWLEditorKit().getOWLWorkspace().isHiddenAnnotationURI(ax.getAnnotation().getAnnotationURI())) {
                addRow(new OWLAnnotationsFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            }
            else {
                hidden = true;
            }
        }
        if (hidden) {
            setLabel(LABEL + " (some annotations are hidden)");
        }
        else {
            setLabel(LABEL);
        }
    }


    protected OWLEntityAnnotationAxiom createAxiom(OWLAnnotation object) {
        return getOWLDataFactory().getOWLEntityAnnotationAxiom(getRootObject(), object);
    }


    public OWLFrameSectionRowObjectEditor<OWLAnnotation> getObjectEditor() {
        return new OWLAnnotationEditor(getOWLEditorKit());
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLEntity, OWLEntityAnnotationAxiom, OWLAnnotation>> getRowComparator() {

        return comparator;
    }


    public void visit(OWLEntityAnnotationAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }


    private static class OWLAnnotationSectionRowComparator implements Comparator<OWLFrameSectionRow<OWLEntity, OWLEntityAnnotationAxiom, OWLAnnotation>> {

        private OWLObjectComparator<OWLAnnotationAxiom> owlObjectComparator;

        public OWLAnnotationSectionRowComparator(OWLModelManager owlModelManager) {
             owlObjectComparator = new OWLObjectComparator<OWLAnnotationAxiom>(owlModelManager);
        }

        public int compare(OWLFrameSectionRow<OWLEntity, OWLEntityAnnotationAxiom, OWLAnnotation> o1,
                           OWLFrameSectionRow<OWLEntity, OWLEntityAnnotationAxiom, OWLAnnotation> o2) {
                return owlObjectComparator.compare(o1.getAxiom(), o2.getAxiom());
        }
    }
}
