package org.protege.editor.owl.ui.frame;

import java.util.Comparator;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owl.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owl.model.OWLIndividual;
import org.semanticweb.owl.model.OWLOntology;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jan-2007<br><br>
 */
public class OWLDataPropertyAssertionAxiomFrameSection extends AbstractOWLFrameSection<OWLIndividual, OWLDataPropertyAssertionAxiom, OWLDataPropertyConstantPair> {

    public static final String LABEL = "Data property assertions";

    private OWLDataPropertyRelationshipEditor editor;


    protected void clear() {
        if (editor != null) {
            editor.clear();
        }
    }


    public OWLDataPropertyAssertionAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLIndividual> frame) {
        super(editorKit, LABEL, frame);
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLDataPropertyAssertionAxiom ax : ontology.getDataPropertyAssertionAxioms(getRootObject())) {
            addRow(new OWLDataPropertyAssertionAxiomFrameSectionRow(getOWLEditorKit(),
                                                                    this,
                                                                    ontology,
                                                                    getRootObject(),
                                                                    ax));
        }
    }


    protected OWLDataPropertyAssertionAxiom createAxiom(OWLDataPropertyConstantPair object) {
        return getOWLDataFactory().getOWLDataPropertyAssertionAxiom(getRootObject(),
                                                                    object.getProperty(),
                                                                    object.getConstant());
    }


    public OWLFrameSectionRowObjectEditor<OWLDataPropertyConstantPair> getObjectEditor() {
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
