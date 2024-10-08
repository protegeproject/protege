package org.protege.editor.owl.ui.frame.individual;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.OWLPropertyAssertionRowComparator;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyIndividualPairEditor2;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLObjectPropertyIndividualPair;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.NodeSet;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 30-Jan-2007<br><br>
 */
public class OWLObjectPropertyAssertionAxiomFrameSection extends AbstractOWLFrameSection<OWLIndividual, OWLObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair> {

    public static final String LABEL = "Object property assertions";

    private final Set<OWLObjectPropertyAssertionAxiom> added = new HashSet<>();

    private final OWLObjectPropertyIndividualPairEditor2 editor;


    public OWLObjectPropertyAssertionAxiomFrameSection(OWLEditorKit owlEditorKit, OWLFrame<? extends OWLIndividual> frame) {
        super(owlEditorKit, LABEL, "Object property assertion", frame);
        editor = new OWLObjectPropertyIndividualPairEditor2(getOWLEditorKit());
    }


    protected void clear() {
        added.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLObjectPropertyAssertionAxiom ax : ontology.getObjectPropertyAssertionAxioms(getRootObject())) {
            addRow(new OWLObjectPropertyAssertionAxiomFrameSectionRow(getOWLEditorKit(),
                                                                      this,
                                                                      ontology,
                                                                      getRootObject(),
                                                                      ax));
            added.add(ax);
        }
    }


    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_ASSERTIONS, () -> {
            if (!getOWLModelManager().getReasoner().isConsistent()) {
                return;
            }
            OWLDataFactory factory = getOWLDataFactory();
            if (!getRootObject().isAnonymous()){
                for (OWLObjectProperty prop : getReasoner().getRootOntology().getObjectPropertiesInSignature(true)) {
                    if (prop.equals(factory.getOWLTopObjectProperty())) {
                        continue;
                    }
                    NodeSet<OWLNamedIndividual> values = getReasoner().getObjectPropertyValues(getRootObject().asOWLNamedIndividual(), prop);
                    for (OWLNamedIndividual ind : values.getFlattened()) {
                        OWLObjectPropertyAssertionAxiom ax = getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(prop,
                                                                                                                    getRootObject(),
                                                                                                                    ind);
                        if (!added.contains(ax)) {
                            addRow(new OWLObjectPropertyAssertionAxiomFrameSectionRow(getOWLEditorKit(),
                                                                                      OWLObjectPropertyAssertionAxiomFrameSection.this,
                                                                                      null,
                                                                                      getRootObject(),
                                                                                      ax));
                        }
                    }
                }
            }
        });
    }


    protected OWLObjectPropertyAssertionAxiom createAxiom(OWLObjectPropertyIndividualPair object) {
        return getOWLDataFactory().getOWLObjectPropertyAssertionAxiom(object.getProperty(),
                                                                      getRootObject(),
                                                                      object.getIndividual());
    }


    public OWLObjectEditor<OWLObjectPropertyIndividualPair> getObjectEditor() {
        return editor;
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLIndividual, OWLObjectPropertyAssertionAxiom, OWLObjectPropertyIndividualPair>> getRowComparator() {
        return new OWLPropertyAssertionRowComparator<>(getOWLModelManager().getOWLObjectComparator());
    }
    
    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLObjectPropertyAssertionAxiom) {
    		return ((OWLObjectPropertyAssertionAxiom) axiom).getSubject().equals(getRootObject());
    	}
    	return false;
    }

}
