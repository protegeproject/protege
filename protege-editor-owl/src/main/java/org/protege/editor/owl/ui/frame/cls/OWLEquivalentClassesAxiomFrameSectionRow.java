package org.protege.editor.owl.ui.frame.cls;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.util.CollectionFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLEquivalentClassesAxiomFrameSectionRow extends AbstractOWLFrameSectionRow<OWLClassExpression, OWLEquivalentClassesAxiom, OWLClassExpression> {

    public OWLEquivalentClassesAxiomFrameSectionRow(OWLEditorKit owlEditorKit, 
    												OWLFrameSection<OWLClassExpression, OWLEquivalentClassesAxiom, OWLClassExpression> section,
                                                    OWLOntology ontology, OWLClassExpression rootObject,
                                                    OWLEquivalentClassesAxiom axiom) {
        super(owlEditorKit, section, ontology, rootObject, axiom);
    }


    protected List<OWLClassExpression> getObjects() {
        Set<OWLClassExpression> clses = new HashSet<>(getAxiom().getClassExpressions());
        clses.remove(getRoot());
        return new ArrayList<>(clses);
    }

    public boolean isEditable() {
        Set<OWLClassExpression> descs = new HashSet<>(getAxiom().getClassExpressions());
        descs.remove(getRoot());
        return descs.size() == 1;
    }
    
    @Override
    public boolean isDeleteable() {
    	return true;
    }
    
    protected OWLObjectEditor<OWLClassExpression> getObjectEditor() {
        Set<OWLClassExpression> descs = new HashSet<>(getAxiom().getClassExpressions());
        descs.remove(getRoot());
        return descs.size() == 1 ? getOWLEditorKit().getWorkspace().getOWLComponentFactory().getOWLClassDescriptionEditor(descs.iterator().next(), AxiomType.EQUIVALENT_CLASSES)
        		: null;
    }
    
    public boolean checkEditorResults(OWLObjectEditor<OWLClassExpression> editor) {
    	Set<OWLClassExpression> equivalents = editor.getEditedObjects();
    	return equivalents.size() != 1 || !equivalents.contains(getRootObject());
    }
    
    @Override
    public void handleEditingFinished(Set<OWLClassExpression> editedObjects) {
    	editedObjects = new HashSet<>(editedObjects);
    	editedObjects.remove(getRootObject());
    	super.handleEditingFinished(editedObjects);
    }


    protected OWLEquivalentClassesAxiom createAxiom(OWLClassExpression editedObject) {
        return getOWLDataFactory().getOWLEquivalentClassesAxiom(CollectionFactory.createSet(getRoot(), editedObject));
    }


    /**
     * Gets a list of objects contained in this row.  These objects
     * could be placed on the clip board during a copy operation,
     * or navigated to etc.
     */
    public List<OWLClassExpression> getManipulatableObjects() {
        return getObjects();
    }
}

