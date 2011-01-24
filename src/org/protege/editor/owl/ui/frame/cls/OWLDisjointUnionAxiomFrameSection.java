package org.protege.editor.owl.ui.frame.cls;

import java.util.Comparator;
import java.util.Set;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.ui.editor.OWLClassExpressionSetEditor;
import org.protege.editor.owl.ui.editor.OWLObjectEditor;
import org.protege.editor.owl.ui.frame.AbstractOWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrame;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDisjointUnionAxiom;
import org.semanticweb.owlapi.model.OWLOntology;

public class OWLDisjointUnionAxiomFrameSection extends AbstractOWLFrameSection<OWLClass, OWLDisjointUnionAxiom, Set<OWLClassExpression>> {
	public final static String LABEL = "Disjoint union of";
	
	public OWLDisjointUnionAxiomFrameSection(OWLEditorKit editorKit, OWLFrame<OWLClass> frame) {
		super(editorKit, LABEL, LABEL, frame);
	}

	@Override
	protected OWLDisjointUnionAxiom createAxiom(Set<OWLClassExpression> editedObject) {
		return getOWLDataFactory().getOWLDisjointUnionAxiom(getRootObject(), editedObject);
	}
	
	@Override
	protected void refill(OWLOntology ontology) {
		for (OWLDisjointUnionAxiom axiom : ontology.getDisjointUnionAxioms(getRootObject())) {
			addRow(new OWLDisjointUnionAxiomFrameSectionRow(getOWLEditorKit(), this, ontology, getRootObject(), axiom));
		}
	}

	@Override
	public OWLObjectEditor<Set<OWLClassExpression>> getObjectEditor() {
        return new OWLClassExpressionSetEditor(getOWLEditorKit());
	}
	
	@Override
    public boolean checkEditorResults(OWLObjectEditor<Set<OWLClassExpression>> editor) {
    	Set<OWLClassExpression> disjoints = editor.getEditedObject();
    	return disjoints.size() >= 2;
    }
    
    @Override
    public void visit(OWLDisjointUnionAxiom axiom) {
    	if (axiom.getOWLClass().equals(getRootObject())) {
    		reset();
    	}
    }

	public Comparator<OWLFrameSectionRow<OWLClass, OWLDisjointUnionAxiom, Set<OWLClassExpression>>> getRowComparator() {
		return null;
	}

	@Override
	protected void clear() {
		// TODO Auto-generated method stub
		
	}
}
