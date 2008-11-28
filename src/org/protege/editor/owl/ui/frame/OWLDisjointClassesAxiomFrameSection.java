package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.frame.cls.AbstractOWLClassAxiomFrameSection;
import org.semanticweb.owl.model.*;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLDisjointClassesAxiomFrameSection extends AbstractOWLClassAxiomFrameSection<OWLDisjointClassesAxiom, Set<OWLDescription>> {

    public static final String LABEL = "Disjoint classes";


    public OWLDisjointClassesAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<OWLClass> frame) {
        super(editorKit, LABEL, LABEL, frame);
    }


    protected void clear() {
    }


    protected void addAxiom(OWLDisjointClassesAxiom ax, OWLOntology ont) {
        addRow(new OWLDisjointClassesAxiomFrameSectionRow(getOWLEditorKit(), this, ont, getRootObject(), ax));
    }


    protected Set<OWLDisjointClassesAxiom> getClassAxioms(OWLDescription descr, OWLOntology ont) {
        if (!descr.isAnonymous()){
            return ont.getDisjointClassesAxioms(descr.asOWLClass());
        }
        else{
            Set<OWLDisjointClassesAxiom> axioms = new HashSet<OWLDisjointClassesAxiom>();
            for (OWLDisjointClassesAxiom ax : ont.getAxioms(AxiomType.DISJOINT_CLASSES)){
                if (ax.getDescriptions().contains(descr)){
                    axioms.add(ax);
                }
            }
            return axioms;
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
    public Comparator<OWLFrameSectionRow<OWLDescription, OWLDisjointClassesAxiom, Set<OWLDescription>>> getRowComparator() {
        return null;
    }
}
