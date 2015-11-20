package org.protege.editor.owl.ui.view.individual;

import org.protege.editor.owl.model.selection.OWLSelectionModelListener;
import org.protege.editor.owl.ui.framelist.OWLFrameList;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.reasoner.NodeSet;
import org.semanticweb.owlapi.reasoner.OWLReasoner;

import java.util.List;

public class OWLInferredMemberListViewComponent extends OWLIndividualListViewComponent {
    /**
     * 
     */
    private static final long serialVersionUID = 8626425915826524799L;
    private OWLSelectionModelListener refillOnClassSelectionListener = new OWLSelectionModelListener(){
        public void selectionChanged() throws Exception {
            if (getOWLWorkspace().getOWLSelectionModel().getSelectedObject() instanceof OWLClass){
                refill();
            }
        }
    };


    @Override
    public void initialiseIndividualsView() throws Exception {
        super.initialiseIndividualsView();
        getOWLWorkspace().getOWLSelectionModel().addListener(refillOnClassSelectionListener);
        setIndividualListColor(OWLFrameList.INFERRED_BG_COLOR);
    }
    
    @Override
    protected void setupActions() {
    	; // no actions
    }
    
    @Override
    protected void refill() {
        individualsInList.clear();
        OWLClass cls = getOWLWorkspace().getOWLSelectionModel().getLastSelectedClass();
        if (cls != null) {
        	OWLReasoner reasoner = getOWLModelManager().getReasoner();
        	NodeSet<OWLNamedIndividual> individuals = reasoner.getInstances(cls, true);
        	if (individuals != null) {
        		for (OWLIndividual ind : individuals.getFlattened()){
        			if (!ind.isAnonymous()){
        				individualsInList.add(ind.asOWLNamedIndividual());
        			}
        		}
        	}
        }
        reset();
    }
    
    @Override
    protected void processChanges(List<? extends OWLOntologyChange> changes) {
    	OWLReasoner reasoner = getOWLModelManager().getReasoner();
    	if (reasoner.getPendingChanges().isEmpty()) {
    		refill();
    	}
    }
    
    @Override
    public void disposeView() {
        getOWLWorkspace().getOWLSelectionModel().removeListener(refillOnClassSelectionListener);
        super.disposeView();
    }

    @Override
    protected void reset() {
    	super.reset();
        setIndividualListColor(OWLFrameList.INFERRED_BG_COLOR);
    }
}
