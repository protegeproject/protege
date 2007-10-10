package org.protege.editor.owl.ui.frame;

import java.util.Comparator;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 01-Feb-2007<br><br>
 */
public class OWLNegativeObjectPropertyAssertionFrameSection extends AbstractOWLFrameSection<OWLIndividual, OWLNegativeObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair> {

    public static final String LABEL = "Negative object property assertions";


    protected void clear() {
    }


    public OWLNegativeObjectPropertyAssertionFrameSection(OWLEditorKit editorKit,
                                                          OWLFrame<? extends OWLIndividual> frame) {
        super(editorKit, LABEL, frame);
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLNegativeObjectPropertyAssertionAxiom ax : ontology.getNegativeObjectPropertyAssertionAxioms(
                getRootObject())) {
            addRow(new OWLNegativeObjectPropertyAssertionFrameSectionRow(getOWLEditorKit(),
                                                                         this,
                                                                         ontology,
                                                                         getRootObject(),
                                                                         ax));
        }
    }


    protected OWLNegativeObjectPropertyAssertionAxiom createAxiom(OWLObjectPropertyIndividualPair object) {
        return getOWLDataFactory().getOWLNegativeObjectPropertyAssertionAxiom(getRootObject(),
                                                                              object.getProperty(),
                                                                              object.getIndividual());
    }


    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
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
    public Comparator<OWLFrameSectionRow<OWLIndividual, OWLNegativeObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair>> getRowComparator() {
        return null;
    }


    public void visit(OWLNegativeDataPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }
}
