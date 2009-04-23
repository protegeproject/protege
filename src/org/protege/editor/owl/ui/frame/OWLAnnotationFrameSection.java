package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.frame.editor.OWLAnnotationEditor;
import org.protege.editor.owl.ui.frame.editor.OWLFrameSectionRowObjectEditor;
import org.semanticweb.owl.model.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 26-Jan-2007<br><br>
 */
public class OWLAnnotationFrameSection extends AbstractOWLFrameSection<OWLEntity, OWLAnnotationAssertionAxiom, OWLAnnotation> {

    private static final String LABEL = "Annotations";

    private static OWLAnnotationSectionRowComparator comparator;


    public OWLAnnotationFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLEntity> frame) {
        super(editorKit, LABEL, "Entity annotation", frame);
        comparator = new OWLAnnotationSectionRowComparator(editorKit.getModelManager());
    }


    protected void clear() {
    }


    protected void refill(OWLOntology ontology) {
        boolean hidden = false;
        for (OWLAnnotationAssertionAxiom ax : ontology.getAnnotationAssertionAxioms(getRootObject())) {
            if (!getOWLEditorKit().getWorkspace().isHiddenAnnotationURI(ax.getAnnotation().getProperty().getURI())) {
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


    protected OWLAnnotationAssertionAxiom createAxiom(OWLAnnotation object) {
        return getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject(), object);
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
    public Comparator<OWLFrameSectionRow<OWLEntity, OWLAnnotationAssertionAxiom, OWLAnnotation>> getRowComparator() {

        return comparator;
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLAnnotation)) {
                return false;
            }
        }
        return true;
    }

    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLAnnotation) {
                OWLAnnotation annot = (OWLAnnotation)obj;
                OWLAxiom ax = getOWLDataFactory().getOWLAnnotationAssertionAxiom(getRootObject(), annot);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }

    public void visit(OWLAnnotationAssertionAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }


    private static class OWLAnnotationSectionRowComparator implements Comparator<OWLFrameSectionRow<OWLEntity, OWLAnnotationAssertionAxiom, OWLAnnotation>> {

        private Comparator<OWLAnnotationAxiom> owlObjectComparator;

        public OWLAnnotationSectionRowComparator(OWLModelManager owlModelManager) {
             owlObjectComparator = owlModelManager.getOWLObjectComparator();
        }

        public int compare(OWLFrameSectionRow<OWLEntity, OWLAnnotationAssertionAxiom, OWLAnnotation> o1,
                           OWLFrameSectionRow<OWLEntity, OWLAnnotationAssertionAxiom, OWLAnnotation> o2) {
                return owlObjectComparator.compare(o1.getAxiom(), o2.getAxiom());
        }
    }
}
