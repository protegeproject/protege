package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLClassExpressionSetEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLDisjointClassesAxiomFrameSection extends AbstractOWLClassAxiomFrameSection<OWLDisjointClassesAxiom, Set<OWLClassExpression>> {

    public static final String LABEL = "Disjoint classes";


    public OWLDisjointClassesAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<OWLClass> frame) {
        super(editorKit, LABEL, LABEL, frame);
    }


    protected void clear() {
    }


    protected void addAxiom(OWLDisjointClassesAxiom ax, OWLOntology ont) {
        addRow(new OWLDisjointClassesAxiomFrameSectionRow(getOWLEditorKit(), this, ont, getRootObject(), ax));
    }


    protected Set<OWLDisjointClassesAxiom> getClassAxioms(OWLClassExpression descr, OWLOntology ont) {
        if (!descr.isAnonymous()){
            return ont.getDisjointClassesAxioms(descr.asOWLClass());
        }
        else{
            Set<OWLDisjointClassesAxiom> axioms = new HashSet<OWLDisjointClassesAxiom>();
            for (OWLDisjointClassesAxiom ax : ont.getAxioms(AxiomType.DISJOINT_CLASSES)){
                if (ax.getClassExpressions().contains(descr)){
                    axioms.add(ax);
                }
            }
            return axioms;
        }
    }


    protected OWLDisjointClassesAxiom createAxiom(Set<OWLClassExpression> object) {
        object.add(getRootObject());
        return getOWLDataFactory().getOWLDisjointClassesAxiom(object);
    }


    public OWLObjectEditor<Set<OWLClassExpression>> getObjectEditor() {
        return new OWLClassExpressionSetEditor(getOWLEditorKit());
    }

    public boolean checkEditorResults(OWLObjectEditor<Set<OWLClassExpression>> editor) {
    	Set<OWLClassExpression> disjoints = editor.getEditedObject();
    	return disjoints.size() != 1 || !disjoints.contains(getRootObject());
    }

    public void visit(OWLDisjointClassesAxiom axiom) {
        if (axiom.getClassExpressions().contains(getRootObject())) {
            reset();
        }
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLClassExpression)) {
                return false;
            }
        }
        return true;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        Set<OWLClassExpression> descriptions = new HashSet<OWLClassExpression>();
        descriptions.add(getRootObject());
        for (OWLObject obj : objects) {
            if (obj instanceof OWLClassExpression) {
                OWLClassExpression desc = (OWLClassExpression) obj;
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
    public Comparator<OWLFrameSectionRow<OWLClassExpression, OWLDisjointClassesAxiom, Set<OWLClassExpression>>> getRowComparator() {
        return null;
    }
}
