package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.*;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLDisjointClassesAxiomFrameSection extends AbstractOWLFrameSection<OWLClass, OWLDisjointClassesAxiom, Set<OWLDescription>> {

    public static final String LABEL = "Disjoint classes";


    public OWLDisjointClassesAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<OWLClass> frame) {
        super(editorKit, LABEL, LABEL, frame);
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should not be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLDisjointClassesAxiom ax : ontology.getDisjointClassesAxioms(getRootObject())) {
            addRow(new OWLDisjointClassesAxiomFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
        }
    }


    protected OWLDisjointClassesAxiom createAxiom(Set<OWLDescription> object) {
        object.add(getRootObject());
        return getOWLDataFactory().getOWLDisjointClassesAxiom(object);
    }


    public OWLFrameSectionRowObjectEditor<Set<OWLDescription>> getObjectEditor() {
        return new OWLClassDescriptionSetEditor(getOWLEditorKit());
    }


    public void visit(OWLDisjointClassesAxiom axiom) {
        if (axiom.getDescriptions().contains(getRootObject())) {
            reset();
        }
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLDescription)) {
                return false;
            }
        }
        return true;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        Set<OWLDescription> descriptions = new HashSet<OWLDescription>();
        descriptions.add(getRootObject());
        for (OWLObject obj : objects) {
            if (obj instanceof OWLDescription) {
                OWLDescription desc = (OWLDescription) obj;
                descriptions.add(desc);
            }
            else {
                return false;
            }
        }
        if (descriptions.size() > 1) {
            OWLAxiom ax = getOWLDataFactory().getOWLDisjointClassesAxiom(descriptions);
            changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            getOWLModelManager().applyChanges(changes);
        }
        return true;
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLClass, OWLDisjointClassesAxiom, Set<OWLDescription>>> getRowComparator() {
        return null;
    }
}
