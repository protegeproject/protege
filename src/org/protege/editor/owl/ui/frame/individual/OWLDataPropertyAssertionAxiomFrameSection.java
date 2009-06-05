package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLDataPropertyRelationshipEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owl.inference.OWLReasonerException;
import org.semanticweb.owl.model.*;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jan-2007<br><br>
 */
public class OWLDataPropertyAssertionAxiomFrameSection extends AbstractOWLFrameSection<OWLIndividual, OWLDataPropertyAssertionAxiom, OWLDataPropertyConstantPair> {

    public static final String LABEL = "Data property assertions";

    private OWLDataPropertyRelationshipEditor editor;

    private Set<OWLDataPropertyAssertionAxiom> added = new HashSet<OWLDataPropertyAssertionAxiom>();


    protected void clear() {
        if (editor != null) {
            editor.clear();
        }
    }


    public OWLDataPropertyAssertionAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLIndividual> frame) {
        super(editorKit, LABEL, "Data property assertion", frame);
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        added.clear();
        for (OWLDataPropertyAssertionAxiom ax : ontology.getDataPropertyAssertionAxioms(getRootObject())) {
            addRow(new OWLDataPropertyAssertionAxiomFrameSectionRow(getOWLEditorKit(),
                                                                    this,
                                                                    ontology,
                                                                    getRootObject(),
                                                                    ax));
            added.add(ax);
        }
    }


    protected void refillInferred() throws OWLReasonerException {
        Map<OWLDataProperty, Set<OWLLiteral>> rels = getReasoner().getDataPropertyRelationships(getRootObject());
        for (OWLDataProperty prop : rels.keySet()) {
            for (OWLLiteral constant : rels.get(prop)) {
                OWLDataPropertyAssertionAxiom ax = getOWLDataFactory().getOWLDataPropertyAssertionAxiom(
                        getRootObject(),
                        prop,
                        constant);
                if (!added.contains(ax)) {
                    addRow(new OWLDataPropertyAssertionAxiomFrameSectionRow(getOWLEditorKit(),
                                                                            this,
                                                                            null,
                                                                            getRootObject(),
                                                                            ax));
                }
            }
        }
    }


    protected OWLDataPropertyAssertionAxiom createAxiom(OWLDataPropertyConstantPair object) {
        return getOWLDataFactory().getOWLDataPropertyAssertionAxiom(getRootObject(),
                                                                    object.getProperty(),
                                                                    object.getConstant());
    }


    public OWLObjectEditor<OWLDataPropertyConstantPair> getObjectEditor() {
        if (editor == null) {
            editor = new OWLDataPropertyRelationshipEditor(getOWLEditorKit());
        }
        return editor;
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLIndividual, OWLDataPropertyAssertionAxiom, OWLDataPropertyConstantPair>> getRowComparator() {
        return null;
    }


    public void visit(OWLDataPropertyAssertionAxiom axiom) {
        if (axiom.getSubject().equals(getRootObject())) {
            reset();
        }
    }
}
