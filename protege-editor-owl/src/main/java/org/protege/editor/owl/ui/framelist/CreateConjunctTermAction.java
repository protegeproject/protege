package org.protege.editor.owl.ui.framelist;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.protege.editor.owl.ui.frame.OWLFrameSectionRow;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectIntersectionOf;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;
import org.semanticweb.owlapi.model.RemoveAxiom;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 18-Dec-2007<br><br>
 */
public class CreateConjunctTermAction<R> extends OWLFrameListPopupMenuAction<R> {

    /**
	 * 
	 */
	private static final long serialVersionUID = -8169858029492752493L;


	protected String getName() {
        return "Create the conjunction of the selected terms";
    }


    protected void initialise() throws Exception {

    }


    protected void dispose() throws Exception {
    }


    protected void updateState() {
        
        setEnabled(getSelectedRows().size() > 1);
    }


    public void actionPerformed(ActionEvent e) {
    	OWLClass cls = null;
    	HashSet<OWLClassExpression> cons = new HashSet<OWLClassExpression>();
    	List<OWLOntologyChange> changes = new ArrayList<>();
    	
    	
    	for (OWLFrameSectionRow<?, ?, ?> row : getSelectedRows()) {
    		OWLAxiom ax = row.getAxiom();
    		if (ax instanceof OWLSubClassOfAxiom) {
    			OWLSubClassOfAxiom sub_ax = (OWLSubClassOfAxiom) ax;
    			if (cls == null) {
    				cls = sub_ax.getSubClass().asOWLClass();
    			}
    			cons.add(sub_ax.getSuperClass());
    			changes.add(new RemoveAxiom(getOWLModelManager().getActiveOntology(), ax));
    		}


    		System.out.println("Add this row to the conjunction " + ax.toString());

    	}
    	OWLDataFactory df = getOWLModelManager().getOWLDataFactory();
    	OWLObjectIntersectionOf oio = df.getOWLObjectIntersectionOf(cons);
    	changes.add(new AddAxiom(getOWLModelManager().getActiveOntology(), 
    			df.getOWLSubClassOfAxiom(cls, oio)));
    	getOWLModelManager().applyChanges(changes);
    	
        
    }
}
