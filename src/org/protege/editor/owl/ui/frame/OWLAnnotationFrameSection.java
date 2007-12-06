package org.protege.editor.owl.ui.frame;

import java.util.Comparator;
import java.net.URI;

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
public class OWLAnnotationFrameSection extends AbstractOWLFrameSection<OWLEntity, OWLEntityAnnotationAxiom, OWLAnnotation> {

    private static final String LABEL = "Annotations";

    private static OWLAnnotationSectionRowComparator comparator = new OWLAnnotationSectionRowComparator();


    public OWLAnnotationFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLEntity> frame) {
        super(editorKit, LABEL, frame);
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


        public int compare(OWLFrameSectionRow<OWLEntity, OWLEntityAnnotationAxiom, OWLAnnotation> o1,
                           OWLFrameSectionRow<OWLEntity, OWLEntityAnnotationAxiom, OWLAnnotation> o2) {
            String fragment1 =  o1.getAxiom().getAnnotation().getAnnotationURI().getFragment();
            if(fragment1 == null) {
                fragment1 = o1.getAxiom().getAnnotation().getAnnotationURI().toString();
            }
            String fragment2 =  o2.getAxiom().getAnnotation().getAnnotationURI().getFragment();
            if(fragment2 == null) {
                fragment2 = o2.getAxiom().getAnnotation().getAnnotationURI().toString();
            }
            return fragment1.compareTo(fragment2);
        }
    }
}
