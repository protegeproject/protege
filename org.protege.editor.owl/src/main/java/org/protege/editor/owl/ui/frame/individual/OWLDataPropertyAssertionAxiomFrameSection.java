package org.protege.editor.owl.ui.frame.individual;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLDataPropertyRelationshipEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLDataPropertyConstantPair;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyAssertionAxiom;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.parameters.Imports;


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


    @Override
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
    @Override
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

    @Override
    protected void refillInferred() {
    	getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_DATA_PROPERTY_ASSERTIONS, new Runnable() {
    		@Override
            public void run() {
            	if (!getOWLModelManager().getReasoner().isConsistent()) {
            		return;
            	}
    			if (!getRootObject().isAnonymous()){
                            for (OWLDataProperty dp : getReasoner()
                                    .getRootOntology()
                                    .getDataPropertiesInSignature(
                                            Imports.INCLUDED)) {
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
    		}
    	});
    }


    @Override
    protected OWLDataPropertyAssertionAxiom createAxiom(OWLDataPropertyConstantPair object) {
        return getOWLDataFactory().getOWLDataPropertyAssertionAxiom(object.getProperty(),
                                                                    getRootObject(),
                                                                    object.getConstant());
    }


    @Override
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
    @Override
    public Comparator<OWLFrameSectionRow<OWLIndividual, OWLDataPropertyAssertionAxiom, OWLDataPropertyConstantPair>> getRowComparator() {
        return null;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLDataPropertyAssertionAxiom &&
    			((OWLDataPropertyAssertionAxiom) change.getAxiom()).getSubject().equals(getRootObject());
    }

}
