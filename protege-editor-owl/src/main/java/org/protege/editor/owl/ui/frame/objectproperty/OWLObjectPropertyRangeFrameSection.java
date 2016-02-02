package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;

import java.util.*;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLObjectPropertyRangeFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLObjectPropertyRangeAxiom, OWLClassExpression> {

    public static final String LABEL = "Ranges (intersection)";

    Set<OWLClassExpression> addedRanges = new HashSet<OWLClassExpression>();


    public OWLObjectPropertyRangeFrameSection(OWLEditorKit owlEditorKit, OWLFrame<? extends OWLObjectProperty> frame) {
        super(owlEditorKit, LABEL, "Range", frame);
    }


    protected void clear() {
        addedRanges.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {

        for (OWLObjectPropertyRangeAxiom ax : ontology.getObjectPropertyRangeAxioms(getRootObject())) {
            addRow(new OWLObjectPropertyRangeFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
            addedRanges.add(ax.getRange());
        }
    }


    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_OBJECT_PROPERTY_RANGES,
                () -> {
                    if (!getOWLModelManager().getReasoner().isConsistent()) {
                        return;
                    }
                    for (OWLClassExpression inferredRange : getInferredRanges()) {
                        if (!addedRanges.contains(inferredRange)) {
                            OWLObjectPropertyRangeAxiom inferredAxiom = getOWLDataFactory().getOWLObjectPropertyRangeAxiom(getRootObject(),
                                                                                                                           inferredRange);
                            addInferredRowIfNontrivial(new OWLObjectPropertyRangeFrameSectionRow(getOWLEditorKit(),
                                                                             OWLObjectPropertyRangeFrameSection.this,
                                                                             null,
                                                                             getRootObject(),
                                                                             inferredAxiom));
                        }
                        addedRanges.add(inferredRange);
                    }
                });
    }


    private Set<OWLClassExpression> getInferredRanges() {
        return new HashSet<OWLClassExpression>(getOWLModelManager().getReasoner().getObjectPropertyRanges(getRootObject(), true).getFlattened());
    }


    protected OWLObjectPropertyRangeAxiom createAxiom(OWLClassExpression object) {
        return getOWLDataFactory().getOWLObjectPropertyRangeAxiom(getRootObject(), object);
    }


    public OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        return getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(null, AxiomType.OBJECT_PROPERTY_RANGE);
    }


    public boolean canAcceptDrop(List<OWLObject> objects) {
        for (OWLObject obj : objects) {
            if (!(obj instanceof OWLClassExpression)) {
                return false;
            }
        }
        return true;
    }


    public boolean dropObjects(List<OWLObject> objects) {
        List<OWLOntologyChange> changes = new ArrayList<OWLOntologyChange>();
        for (OWLObject obj : objects) {
            if (obj instanceof OWLClassExpression) {
                OWLClassExpression desc = (OWLClassExpression) obj;
                OWLAxiom ax = getOWLDataFactory().getOWLObjectPropertyRangeAxiom(getRootObject(), desc);
                changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
            }
            else {
                return false;
            }
        }
        getOWLModelManager().applyChanges(changes);
        return true;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLObjectPropertyRangeAxiom) {
    		return ((OWLObjectPropertyRangeAxiom) axiom).getProperty().equals(getRootObject());
    	}
    	return false;
    }

    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLObjectPropertyRangeAxiom, OWLClassExpression>> getRowComparator() {
        return null;
    }
}
