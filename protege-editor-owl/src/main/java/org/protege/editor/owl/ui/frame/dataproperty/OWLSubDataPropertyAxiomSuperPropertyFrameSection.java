package org.protege.editor.owl.ui.frame.dataproperty;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLDataPropertyEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDataPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubDataPropertyOfAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 16-Feb-2007<br><br>
 */
public class OWLSubDataPropertyAxiomSuperPropertyFrameSection extends AbstractOWLFrameSection<OWLDataProperty, OWLSubDataPropertyOfAxiom, OWLDataProperty> {

    public static final String LABEL = "SubProperty Of";

    private Set<OWLSubDataPropertyOfAxiom> added = new HashSet<>();


    public OWLSubDataPropertyAxiomSuperPropertyFrameSection(OWLEditorKit editorKit,
                                                            OWLFrame<? extends OWLDataProperty> frame) {
        super(editorKit, LABEL, "Super property", frame);
    }


    protected OWLSubDataPropertyOfAxiom createAxiom(OWLDataProperty object) {
        return getOWLDataFactory().getOWLSubDataPropertyOfAxiom(getRootObject(), object);
    }


    public OWLObjectEditor<OWLDataProperty> getObjectEditor() {
        return new OWLDataPropertyEditor(getOWLEditorKit());
    }


    protected void clear() {
    }


    protected void refill(OWLOntology ontology) {
        added.clear();
        for (OWLSubDataPropertyOfAxiom ax : ontology.getDataSubPropertyAxiomsForSubProperty(getRootObject())) {
            addRow(new OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow(getOWLEditorKit(),
                                                                           this,
                                                                           ontology,
                                                                           getRootObject(),
                                                                           ax));
            added.add(ax);
        }
    }


    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_SUPER_DATATYPE_PROPERTIES, () -> {
            if (!getOWLModelManager().getReasoner().isConsistent()) {
                return;
            }
            for (OWLDataPropertyExpression infSup : getOWLModelManager().getReasoner().getSuperDataProperties(getRootObject(), true).getFlattened()) {
                if (!added.contains(infSup)) {
                    final OWLSubDataPropertyOfAxiom ax = getOWLDataFactory().getOWLSubDataPropertyOfAxiom(
                                                                                                          getRootObject(),
                                                                                                          infSup);
                    addInferredRowIfNontrivial(new OWLSubDataPropertyAxiomSuperPropertyFrameSectionRow(getOWLEditorKit(),
                                                                                   OWLSubDataPropertyAxiomSuperPropertyFrameSection.this,
                                                                                   null,
                                                                                   getRootObject(),
                                                                                   ax));
                }
            }
        });
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLSubDataPropertyOfAxiom) {
    		return ((OWLSubDataPropertyOfAxiom) axiom).getSubProperty().equals(getRootObject());
    	}
    	return false;
    }


    public Comparator<OWLFrameSectionRow<OWLDataProperty, OWLSubDataPropertyOfAxiom, OWLDataProperty>> getRowComparator() {
        return null;
    }
}
