package org.protege.editor.owl.ui.frame;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLAxiom;
import org.semanticweb.owl.model.OWLClass;
import org.semanticweb.owl.model.OWLClassAssertionAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObject;
import org.semanticweb.owl.model.OWLOntology;
import org.semanticweb.owl.model.OWLOntologyChange;
import org.semanticweb.owl.model.OWLRuntimeException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 27-Jan-2007<br><br>
 */
public class OWLClassAssertionAxiomIndividualSection extends AbstractOWLFrameSection<OWLClass, OWLClassAssertionAxiom, OWLIndividual> {

    public static final String LABEL = "Instances";

    private Set<OWLIndividual> added = new HashSet<OWLIndividual>();


    public OWLClassAssertionAxiomIndividualSection(OWLEditorKit editorKit, OWLFrame<? extends OWLClass> frame) {
        super(editorKit, LABEL, frame);
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
            addRow(new OWLClassAssertionAxiomIndividualSectionRow(getOWLEditorKit(),
                                                                  this,
                                                                  ontology,
                                                                  getRootObject(),
                                                                  ax));
            added.add(ax.getIndividual());
        }
    }


    protected void refillInferred() {
        try {
            for (OWLIndividual ind : getOWLModelManager().getReasoner().getIndividuals(getRootObject(), false)) {
                if (!added.contains(ind)) {
                    addRow(new OWLClassAssertionAxiomIndividualSectionRow(getOWLEditorKit(),
                                                                          this,
                                                                          null,
                                                                          getRootObject(),
                                                                          getOWLModelManager().getOWLDataFactory().getOWLClassAssertionAxiom(
                                                                                  ind,
                                                                                  getRootObject())));
                    added.add(ind);
                }
            }
        }
        catch (OWLReasonerException e) {
            throw new OWLRuntimeException(e);
        }
    }


    protected OWLClassAssertionAxiom createAxiom(OWLIndividual object) {
        return getOWLDataFactory().getOWLClassAssertionAxiom(object, getRootObject());
    }


    public OWLFrameSectionRowObjectEditor<OWLIndividual> getObjectEditor() {
        return new OWLIndividualEditor(getOWLEditorKit());
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLIndividual)) {
                return false;
            }
        }
        return true;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLIndividual) {
                OWLIndividual ind = (OWLIndividual) obj;
                OWLAxiom ax = getOWLDataFactory().getOWLClassAssertionAxiom(ind, getRootObject());
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
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
    public Comparator<OWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual>> getRowComparator() {
        return new Comparator<OWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual>>() {


            public int compare(OWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual> o1,
                               OWLFrameSectionRow<OWLClass, OWLClassAssertionAxiom, OWLIndividual> o2) {
                return getOWLModelManager().getRendering(o1.getAxiom().getIndividual()).compareToIgnoreCase(
                        getOWLModelManager().getRendering(o2.getAxiom().getIndividual()));
            }
        };
    }


    public void visit(OWLClassAssertionAxiom axiom) {
        if (axiom.getDescription().equals(getRootObject())) {
            reset();
        }
    }
}
