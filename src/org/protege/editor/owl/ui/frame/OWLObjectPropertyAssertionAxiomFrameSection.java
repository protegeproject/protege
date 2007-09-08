package org.protege.editor.owl.ui.frame;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLObjectProperty;
import org.semanticweb.owl.model.OWLObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLOntology;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jan-2007<br><br>
 */
public class OWLObjectPropertyAssertionAxiomFrameSection extends AbstractOWLFrameSection<OWLIndividual, OWLObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair> {

    public static final String LABEL = "Object property assertions";

    private Set<OWLObjectPropertyAssertionAxiom> added;


    public OWLObjectPropertyAssertionAxiomFrameSection(OWLEditorKit owlEditorKit, OWLFrame<OWLIndividual> frame) {
        super(owlEditorKit, LABEL, frame);
        added = new HashSet<OWLObjectPropertyAssertionAxiom>();
    }


    protected void clear() {

    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        added.clear();
        for (OWLObjectPropertyAssertionAxiom ax : ontology.getObjectPropertyAssertionAxioms(getRootObject())) {
            addRow(new OWLObjectPropertyAssertionAxiomFrameSectionRow(getOWLEditorKit(),
                                                                      this,
                                                                      ontology,
                                                                      getRootObject(),
                                                                      ax));
            added.add(ax);
        }
    }


    protected void refillInferred() throws OWLReasonerException {
        Map<OWLObjectProperty, Set<OWLIndividual>> rels = getReasoner().getObjectPropertyRelationships(getRootObject());
        for (OWLObjectProperty prop : rels.keySet()) {
            for (OWLIndividual ind : rels.get(prop)) {
                OWLObjectPropertyAssertionAxiom ax = getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(
                        getRootObject(),
                        prop,
                        ind);
                if (!added.contains(ax)) {
                    addRow(new OWLObjectPropertyAssertionAxiomFrameSectionRow(getOWLEditorKit(),
                                                                              this,
                                                                              null,
                                                                              getRootObject(),
                                                                              ax));
                }
            }
        }
    }


    protected OWLObjectPropertyAssertionAxiom createAxiom(OWLObjectPropertyIndividualPair object) {
        return getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(getRootObject(),
                                                                      object.getProperty(),
                                                                      object.getIndividual());
    }


    public OWLFrameSectionRowObjectEditor<OWLObjectPropertyIndividualPair> getObjectEditor() {
        return new OWLObjectPropertyIndividualPairEditor(getOWLEditorKit());
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLIndividual, OWLObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair>> getRowComparator() {
        return null;
    }


    public void visit(OWLObjectPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }
}
