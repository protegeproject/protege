package org.protege.editor.owl.ui.frame.objectproperty;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyExpressionEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubObjectPropertyOfAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLSubObjectPropertyAxiomSuperPropertyFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLSubObjectPropertyOfAxiom, OWLObjectPropertyExpression> {

    public static final String LABEL = "SubProperty Of";

    Set<OWLObjectPropertyExpression> added = new HashSet<OWLObjectPropertyExpression>();


    public OWLSubObjectPropertyAxiomSuperPropertyFrameSection(OWLEditorKit editorKit,
                                                              OWLFrame<? extends OWLObjectProperty> frame) {
        super(editorKit, LABEL, "Super property", frame);
    }


    protected void clear() {
        added.clear();
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {

        for (OWLSubObjectPropertyOfAxiom ax : ontology.getObjectSubPropertyAxiomsForSubProperty(getRootObject())) {
            addRow(new OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow(getOWLEditorKit(),
                                                                             this,
                                                                             ontology,
                                                                             getRootObject(),
                                                                             ax));
            added.add(ax.getSuperProperty());
        }
    }


    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_SUPER_OBJECT_PROPERTIES,
                                                                  new Runnable() {
            public void run() {
            	if (!getOWLModelManager().getReasoner().isConsistent()) {
            		return;
            	}
            	OWLObjectProperty topProperty  = getOWLModelManager().getOWLDataFactory().getOWLTopObjectProperty();
                for (OWLObjectPropertyExpression infSup : getOWLModelManager().getReasoner().getSuperObjectProperties(getRootObject(),true).getFlattened()) {
                    if (!added.contains(infSup) && !topProperty.equals(infSup)) {
                        addRow(new OWLSubObjectPropertyAxiomSuperPropertyFrameSectionRow(getOWLEditorKit(),
                                                                                         OWLSubObjectPropertyAxiomSuperPropertyFrameSection.this,
                                                                                         null,
                                                                                         getRootObject(),
                                                                                         getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(getRootObject(),
                                                                                                                                            infSup)));
                    }
                }
            }
        });
    }


    protected OWLSubObjectPropertyOfAxiom createAxiom(OWLObjectPropertyExpression object) {
        return getOWLDataFactory().getOWLSubObjectPropertyOfAxiom(getRootObject(), object);
    }


    public OWLObjectEditor<OWLObjectPropertyExpression> getObjectEditor() {
        return new OWLObjectPropertyExpressionEditor(getOWLEditorKit());
    }


    public void visit(OWLSubObjectPropertyOfAxiom axiom) {
        if (axiom.getSubProperty().equals(getRootObject())) {
            reset();
        }
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLSubObjectPropertyOfAxiom, OWLObjectPropertyExpression>> getRowComparator() {
        return null;
    }
}
