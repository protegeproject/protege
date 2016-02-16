package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLIndividualSetEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
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
 * Date: 29-Jan-2007<br><br>
 */
public class OWLSameIndividualsAxiomFrameSection extends AbstractOWLFrameSection<OWLNamedIndividual, OWLSameIndividualAxiom, Set<OWLNamedIndividual>> {

    public static final String LABEL = "Same Individual As";


    public OWLSameIndividualsAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLNamedIndividual> frame) {
        super(editorKit, LABEL, LABEL, frame);
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLSameIndividualAxiom ax : ontology.getSameIndividualAxioms(getRootObject())) {
            addRow(new OWLSameIndividualsAxiomFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
        }
    }
    
    @Override
    protected void refillInferred() {
    	getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_SAMEAS_INDIVIDUAL_ASSERTIONS, () -> {
            if (!getOWLModelManager().getReasoner().isConsistent()) {
                return;
            }
            Set<OWLIndividual> existingSameIndividuals = getCurrentlyDisplayedSameIndividuals();
            Set<OWLNamedIndividual> newSameIndividuals = new HashSet<>();
            for (OWLNamedIndividual i : getCurrentReasoner().getSameIndividuals(getRootObject()).getEntities()) {
                if (!i.equals(getRootObject()) && !existingSameIndividuals.contains(i)) {
                    newSameIndividuals.add(i);
                }
            }
            if (!newSameIndividuals.isEmpty()) {
                newSameIndividuals.add(getRootObject());
                addRow(new OWLSameIndividualsAxiomFrameSectionRow(getOWLEditorKit(),
                        OWLSameIndividualsAxiomFrameSection.this,
                        null,
                        getRootObject(),
                        getOWLDataFactory().getOWLSameIndividualAxiom(newSameIndividuals)
                ));
            }
        });
    }
    
    public Set<OWLIndividual> getCurrentlyDisplayedSameIndividuals() {
		Set<OWLIndividual> existingSameIndividuals = new HashSet<>();
		for (OWLFrameSectionRow<OWLNamedIndividual, OWLSameIndividualAxiom, Set<OWLNamedIndividual>> existingRow : getRows()) {
			OWLSameIndividualAxiom existingAxiom = existingRow.getAxiom();
			for (OWLIndividual existingSameIndividual : existingAxiom.getIndividuals()) {
				existingSameIndividuals.add(existingSameIndividual);
			}
		}
		return existingSameIndividuals;
    }


    protected OWLSameIndividualAxiom createAxiom(Set<OWLNamedIndividual> object) {
        object.add(getRootObject());
        OWLSameIndividualAxiom ax = getOWLDataFactory().getOWLSameIndividualAxiom(object);
        return ax;
    }


    public OWLObjectEditor<Set<OWLNamedIndividual>> getObjectEditor() {
        return new OWLIndividualSetEditor(getOWLEditorKit());
    }
    
    @Override
	public boolean checkEditorResults(OWLObjectEditor<Set<OWLNamedIndividual>> editor) {
		Set<OWLNamedIndividual> equivalents = editor.getEditedObject();
		return !equivalents.contains(getRootObject());
	}
    
    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLSameIndividualAxiom) {
    		return ((OWLSameIndividualAxiom) axiom).getIndividuals().contains(getRootObject());
    	}
    	return false;
    }

    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLNamedIndividual, OWLSameIndividualAxiom, Set<OWLNamedIndividual>>> getRowComparator() {
        return null;
    }
}
