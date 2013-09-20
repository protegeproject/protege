package org.protege.editor.owl.ui.frame;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLGeneralAxiomEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.semanticweb.owlapi.model.*;

import java.util.Comparator;
import java.util.Set;

import javax.swing.JOptionPane;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 23-Apr-2007<br><br>
 */
public class OWLGeneralClassAxiomsFrameSection extends AbstractOWLFrameSection<OWLOntology, OWLClassAxiom, OWLClassAxiom> {

    public OWLGeneralClassAxiomsFrameSection(OWLEditorKit editorKit, OWLFrame<? extends OWLOntology> frame) {
        super(editorKit, "General class axioms", "General class axiom", frame);
    }


    protected OWLClassAxiom createAxiom(OWLClassAxiom object) {
        return object;
    }


    public OWLObjectEditor<OWLClassAxiom> getObjectEditor() {
        return new OWLGeneralAxiomEditor(getOWLEditorKit());
    }
    
    @Override
    public void handleEditingFinished(Set<OWLClassAxiom> editedObjects) {
    	super.handleEditingFinished(editedObjects);
    	checkEditedAxiom(getOWLEditorKit(), editedObjects);
    }
    
    /* package */ static void checkEditedAxiom(OWLEditorKit editorKit, Set<OWLClassAxiom> editedObjects) {
    	OWLClassAxiom axiom = editedObjects.iterator().next();
    	OWLOntology ontology = editorKit.getOWLModelManager().getActiveOntology();
    	if (ontology.containsAxiom(axiom) && !ontology.getGeneralClassAxioms().contains(axiom)) {
    		JOptionPane.showMessageDialog(editorKit.getOWLWorkspace(), 
    				"Edited axiom is not a general class axiom.  It has been added to\n"+
    				"the ontology but will not show in the General Class Axiom Window.");
    	}
    }


    protected void refill(OWLOntology ontology) {
        for (OWLClassAxiom ax : ontology.getGeneralClassAxioms()) {
            addRow(new OWLGeneralClassAxiomFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), ax));
        }
    }


    protected void clear() {
    }


    public Comparator<OWLFrameSectionRow<OWLOntology, OWLClassAxiom, OWLClassAxiom>> getRowComparator() {
        return null;
    }

    @Override
    protected boolean isResettingChange(OWLOntologyChange change) {
    	if (!change.isAxiomChange()) {
    		return false;
    	}
    	OWLAxiom axiom = change.getAxiom();
    	if (axiom instanceof OWLSubClassOfAxiom) {
    		return ((OWLSubClassOfAxiom) axiom).getSubClass().isAnonymous();
    	}
    	else if (axiom instanceof OWLDisjointClassesAxiom) {
    		for (OWLClassExpression desc : ((OWLDisjointClassesAxiom) axiom).getClassExpressions()) {
    			if (!desc.isAnonymous()) {
    				return false;
    			}
    		}
    		return true;
    	}
    	else if (axiom instanceof OWLEquivalentClassesAxiom) {
            for (OWLClassExpression desc : ((OWLEquivalentClassesAxiom) axiom).getClassExpressions()) {
                if (!desc.isAnonymous()) {
                    return false;
                }
            }
            return true;
    	}
    	return false;
    }

}
