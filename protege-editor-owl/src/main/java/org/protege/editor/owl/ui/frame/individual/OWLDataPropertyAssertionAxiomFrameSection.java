package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.OWLPropertyAssertionRowComparator;
import org.protege.editor.owl.ui.editor.OWLDataPropertyRelationshipEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.Comparator;
import java.util.HashSet;
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

    private Set<OWLDataPropertyAssertionAxiom> added = new HashSet<>();


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

    protected void refillInferred() {
    	getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_DATA_PROPERTY_ASSERTIONS, () -> {
            if (!getOWLModelManager().getReasoner().isConsistent()) {
                return;
            }
            if (!getRootObject().isAnonymous()){
                for (OWLDataProperty dp : getReasoner().getRootOntology().getDataPropertiesInSignature(true)) {
                    Set<OWLLiteral> values = getReasoner().getDataPropertyValues(getRootObject().asOWLNamedIndividual(), dp);
                    for (OWLLiteral constant : values) {
                        OWLDataPropertyAssertionAxiom ax = getOWLDataFactory().getOWLDataPropertyAssertionAxiom(dp,
                                getRootObject(),
                                constant);
                        if (!added.contains(ax)) {
                            addRow(new OWLDataPropertyAssertionAxiomFrameSectionRow(getOWLEditorKit(),
                                    OWLDataPropertyAssertionAxiomFrameSection.this,
                                    null,
                                    getRootObject(),
                                    ax));
                        }
                    }
                }
            }
        });
    }


    protected OWLDataPropertyAssertionAxiom createAxiom(OWLDataPropertyConstantPair object) {
        return getOWLDataFactory().getOWLDataPropertyAssertionAxiom(object.getProperty(),
                                                                    getRootObject(),
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
        return new OWLPropertyAssertionRowComparator<>(getOWLModelManager().getOWLObjectComparator());
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLDataPropertyAssertionAxiom &&
    			((OWLDataPropertyAssertionAxiom) change.getAxiom()).getSubject().equals(getRootObject());
    }

}
