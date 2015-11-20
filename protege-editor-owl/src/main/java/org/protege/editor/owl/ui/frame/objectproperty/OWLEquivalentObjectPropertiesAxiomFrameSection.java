package org.protege.editor.owl.ui.frame.objectproperty;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.inference.ReasonerPreferences.OptionalInferenceTask;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.editor.OWLObjectPropertyExpressionEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.reasoner.Node;
import org.semanticweb.owlapi.util.CollectionFactory;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 29-Jan-2007<br><br>
 */
public class OWLEquivalentObjectPropertiesAxiomFrameSection extends AbstractOWLFrameSection<OWLObjectProperty, OWLEquivalentObjectPropertiesAxiom, OWLObjectPropertyExpression> {

    public static final String LABEL = "Equivalent To";

    private Set<OWLEquivalentObjectPropertiesAxiom> added = new HashSet<OWLEquivalentObjectPropertiesAxiom>();


    public OWLEquivalentObjectPropertiesAxiomFrameSection(OWLEditorKit editorKit,
                                                          OWLFrame<? extends OWLObjectProperty> frame) {
        super(editorKit, LABEL, "Equivalent object property", frame);
    }


    protected void clear() {
    }


    /**
     * Refills the section with rows.  This method will be called
     * by the system and should be directly called.
     */
    protected void refill(OWLOntology ontology) {
        added.clear();
        for (OWLEquivalentObjectPropertiesAxiom ax : ontology.getEquivalentObjectPropertiesAxioms(getRootObject())) {
            addRow(new OWLEquivalentObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                         this,
                                                                         ontology,
                                                                         getRootObject(),
                                                                         ax));
            added.add(ax);
        }
    }

    protected void refillInferred() {
        getOWLModelManager().getReasonerPreferences().executeTask(OptionalInferenceTask.SHOW_INFERRED_EQUIVALENT_OBJECT_PROPERTIES, 
                                                                  new Runnable() {
            public void run() {
            	if (!getOWLModelManager().getReasoner().isConsistent()) {
            		return;
            	}
                Node<OWLObjectPropertyExpression> equivalentObjectProperties = getReasoner().getEquivalentObjectProperties(getRootObject());
                if (!equivalentObjectProperties.getEntitiesMinus(getRootObject()).isEmpty()) {
                    OWLEquivalentObjectPropertiesAxiom ax = getOWLDataFactory().getOWLEquivalentObjectPropertiesAxiom(equivalentObjectProperties.getEntities());
                    if (!added.contains(ax)) {
                        addInferredRowIfNontrivial(new OWLEquivalentObjectPropertiesAxiomFrameSectionRow(getOWLEditorKit(),
                                                                                     OWLEquivalentObjectPropertiesAxiomFrameSection.this,
                                                                                     null,
                                                                                     getRootObject(),
                                                                                     ax));
                    }
                }
            }
        });

    }


    protected OWLEquivalentObjectPropertiesAxiom createAxiom(OWLObjectPropertyExpression object) {
        return getOWLDataFactory().getOWLEquivalentObjectPropertiesAxiom(CollectionFactory.createSet(getRootObject(),
                                                                                                     object));
    }



    public OWLObjectEditor<OWLObjectPropertyExpression> getObjectEditor() {
        return new OWLObjectPropertyExpressionEditor(getOWLEditorKit());
    }
    
    @Override
    public boolean checkEditorResults(OWLObjectEditor<OWLObjectPropertyExpression> editor) {
    	Set<OWLObjectPropertyExpression> equivalents = editor.getEditedObjects();
    	return equivalents.size() != 1 || !equivalents.contains(getRootObject());
    }
    
    @Override
    public void handleEditingFinished(Set<OWLObjectPropertyExpression> editedObjects) {
    	editedObjects = new HashSet<OWLObjectPropertyExpression>(editedObjects);
    	editedObjects.remove(getRootObject());
    	super.handleEditingFinished(editedObjects);
    }

    
    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	return change.isAxiomChange() &&
    			change.getAxiom() instanceof OWLEquivalentObjectPropertiesAxiom &&
    			((OWLEquivalentObjectPropertiesAxiom) change.getAxiom()).getProperties().contains(getRootObject());
    }


    /**
     * Obtains a comparator which can be used to sort the rows
     * in this section.
     * @return A comparator if to sort the rows in this section,
     *         or <code>null</code> if the rows shouldn't be sorted.
     */
    public Comparator<OWLFrameSectionRow<OWLObjectProperty, OWLEquivalentObjectPropertiesAxiom, OWLObjectPropertyExpression>> getRowComparator() {
        return null;
    }
}
