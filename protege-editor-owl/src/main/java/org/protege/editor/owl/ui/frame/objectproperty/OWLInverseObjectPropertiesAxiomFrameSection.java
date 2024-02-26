package org.protege.editor.owl.ui.frame.objectproperty;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLInverseObjectPropertiesAxiom;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLInverseObjectPropertiesAxiomFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLInverseObjectPropertiesAxiom, OWLObjectProperty> {

    public static final String LABEL = "Inverse Of";

    private Set<OWLObjectPropertyExpression> added = new HashSet<>();


    public OWLInverseObjectPropertiesAxiomFrameSection(OWLEditorKit editorKit,
                                                       OWLFrame<? extends OWLObjectProperty> frame) {
        super(editorKit, LABEL, "Inverse property", frame);
    }


    protected void clear() {
        added.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        for (OWLInverseObjectPropertiesAxiom ax : ontology.getInverseObjectPropertyAxioms(getRootObject())) {
            addRow(new OWLInverseObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                      this,
                                                                      ontology,
                                                                      getRootObject(),
                                                                      ax));
            added.addAll(ax.getProperties());
        }
    }


    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_INVERSE_PROPERTIES,
                () -> {
                    if (!getOWLModelManager().getReasoner().isConsistent()) {
                        return;
                    }
                    final Set<OWLObjectPropertyExpression> infInverses = new HashSet<>(getReasoner().getInverseObjectProperties(getRootObject()).getEntities());
                    infInverses.removeAll(added);
                    for (OWLObjectPropertyExpression invProp : infInverses) {
                        final OWLInverseObjectPropertiesAxiom ax = getOWLDataFactory().getOWLInverseObjectPropertiesAxiom(
                                getRootObject(),
                                invProp);
                            addInferredRowIfNontrivial(new OWLInverseObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                                      OWLInverseObjectPropertiesAxiomFrameSection.this,
                                                                                      null,
                                                                                      getRootObject(),
                                                                                      ax));
                    }
                });
    }


    protected OWLInverseObjectPropertiesAxiom createAxiom(OWLObjectProperty object) {
        return getOWLDataFactory().getOWLInverseObjectPropertiesAxiom(getRootObject(), object);
    }


    public OWLObjectEditor<OWLObjectProperty> getObjectEditor() {
        return new OWLObjectPropertyEditor(getOWLEditorKit());
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLInverseObjectPropertiesAxiom) {
    		return ((OWLInverseObjectPropertiesAxiom) axiom).getProperties().contains(getRootObject());
    	}
    	return false;
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLInverseObjectPropertiesAxiom, OWLObjectProperty>> getRowComparator() {
        return null;
    }
}
