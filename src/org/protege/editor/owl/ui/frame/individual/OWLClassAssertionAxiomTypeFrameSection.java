package org.protege.editor.owl.ui.frame.individual;

import org.apache.log4j.Logger;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLClassAssertionAxiomTypeFrameSection extends AbstractOWLFrameSection<OWLIndividual, OWLClassAssertionAxiom, OWLClassExpression> {

    private static final Logger logger = Logger.getLogger(OWLClassAssertionAxiomTypeFrameSection.class);

    public static final String LABEL = "Types";

    private Set<OWLClassExpression> added = new HashSet<OWLClassExpression>();


    public OWLClassAssertionAxiomTypeFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLIndividual> frame) {
        super(editorKit, LABEL, "Type assertion", frame);
    }


    protected void clear() {
        added.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLClassAssertionAxiom ax : ontology.getClassAssertionAxioms(getRootObject())) {
            addRow(new OWLClassAssertionAxiomTypeFrameSectionRow(getOWLEditorKit(),
                                                                 this,
                                                                 ontology,
                                                                 getRootObject(),
                                                                 ax));
            added.add(ax.getClassExpression());
        }
    }


    protected void refillInferred() {
        if (!getRootObject().isAnonymous()){
            for (OWLClass inferredType : getReasoner().getTypes(getRootObject().asOWLNamedIndividual(), true).getFlattened()) {
                if (!added.contains(inferredType)) {
                    OWLClassAssertionAxiom ax = getOWLDataFactory().getOWLClassAssertionAxiom(inferredType, getRootObject());
                    addRow(new OWLClassAssertionAxiomTypeFrameSectionRow(getOWLEditorKit(),
                                                                         this,
                                                                         null,
                                                                         getRootObject(),
                                                                         ax));
                    added.add(inferredType);
                }
            }
        }
    }


    protected OWLClassAssertionAxiom createAxiom(OWLClassExpression classExpression) {
        return getOWLDataFactory().getOWLClassAssertionAxiom(classExpression, getRootObject());
    }


    public OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(null, AxiomType.CLASS_ASSERTION);
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
        for (OWLObject obj : objects) {
            if (obj instanceof OWLClassExpression) {
                OWLClassExpression classExpression = (OWLClassExpression) obj;
                OWLAxiom ax = getOWLDataFactory().getOWLClassAssertionAxiom(classExpression, getRootObject());
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLIndividual, OWLClassAssertionAxiom, OWLClassExpression>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<OWLIndividual, OWLClassAssertionAxiom, OWLClassExpression>>() {

            private Comparator<OWLObject> comparator = getOWLModelManager().getOWLObjectComparator();

            public int compare(OWLFrameSectionRow<OWLIndividual, OWLClassAssertionAxiom, OWLClassExpression> o1,
                               OWLFrameSectionRow<OWLIndividual, OWLClassAssertionAxiom, OWLClassExpression> o2) {
                if (o1.isInferred() && !o2.isInferred()) {
                    return 1;
                }
                else if (o2.isInferred() && !o1.isInferred()) {
                    return -1;
                }
                return comparator.compare(o1.getAxiom().getClassExpression(), o2.getAxiom().getClassExpression());
            }
        };
    }


    public void visit(OWLClassAssertionAxiom axiom) {
        if (axiom.getIndividual().equals(getRootObject())) {
            reset();
        }
    }
}
