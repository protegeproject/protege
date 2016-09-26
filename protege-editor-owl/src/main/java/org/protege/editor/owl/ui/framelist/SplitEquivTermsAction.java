package org.protege.editor.owl.ui.framelist;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.ui.frame.OWLFrameSection;
import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.protege.editor.owl.ui.frame.cls.OWLEquivalentClassesAxiomFrameSection;
import org.protege.editor.owl.ui.frame.cls.OWLSubClassAxiomFrameSection;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLObjectUnionOf;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Dec-2007<br><br>
 */
public class SplitEquivTermsAction<R> extends OWLFrameListPopupMenuAction<R> {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	protected String getName() {
        return "Split the selected equivalent class definition";
    }

    protected void initialise() throws Exception {

    }

    protected void dispose() throws Exception {
    }

    protected void updateState() {
    	if (getSelectedRows().size() == 1) {
    		Object selVal = getFrameList().getSelectedValue();
            if (selVal instanceof OWLFrameSectionRow) {
            	OWLFrameSection fs = ((OWLFrameSectionRow) selVal).getFrameSection();
            	if (fs instanceof OWLEquivalentClassesAxiomFrameSection) {
            		setEnabled(true);
            	} else {
            		setEnabled(false);
            	}
            } else {
            	setEnabled(false);
            }
    	} else {
    		setEnabled(false);
    	}
    }

    public void actionPerformed(ActionEvent e) {
    	
    	OWLClass cls = null;
    	HashSet<OWLClassExpression> diss = new HashSet<OWLClassExpression>();
    	List<OWLOntologyChange> changes = new ArrayList<>();
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
    	
    	OWLFrameSectionRow selVal = (OWLFrameSectionRow) getFrameList().getSelectedValue();
    	
        OWLEquivalentClassesAxiom ax = (OWLEquivalentClassesAxiom) selVal.getAxiom();
        
        changes.add(new RemoveAxiom(getOWLModelManager().getActiveOntology(), ax));
        
        
        Set<OWLClassExpression> exps = ax.getClassExpressions();
        
        for (OWLClassExpression exp : exps) {
        	System.out.println("The exp: " + exp);
        	if (exp instanceof OWLClass) {
        		cls = (OWLClass) exp;
        	} else if (exp instanceof OWLObjectIntersectionOf) {
        		Set<OWLClassExpression> conjs = exp.asConjunctSet();
        		for (OWLClassExpression c : conjs) {
        			changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), 
        	    			df.getOWLEquivalentClassesAxiom(cls, c)));        			
        		}	
        	}        	
        }
        getOWLModelManager().applyChanges(changes);
    }
}
