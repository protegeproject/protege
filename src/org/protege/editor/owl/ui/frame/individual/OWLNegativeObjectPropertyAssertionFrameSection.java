package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyIndividualPairEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNegativeDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLNegativeObjectPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Comparator;


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
        super(editorKit, LABEL, "Negative object property assertion", frame);
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
        return getOWLDataFactory().getOWLNegativeObjectPropertyAssertionAxiom(object.getProperty(),
                                                                              getRootObject(),
                                                                              object.getIndividual());
    }


    public void visit(OWLNegativeObjectPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }


    public OWLObjectEditor<OWLObjectPropertyIndividualPair> getObjectEditor() {
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
