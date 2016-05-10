package org.protege.editor.owl.ui.action;

import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.model.selection.ontologies.OntologySelectionStrategy;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jun 6, 2008<br><br>
 */
public abstract class AbstractOntologySelectionStrategyAction extends ProtegeOWLAction {
	
	private JMenuItem menuItem;

    private OWLModelManagerListener l = event -> {
        if (event.getType() == EventType.ONTOLOGY_VISIBILITY_CHANGED){
            setSelected(isCurrent());
        }
    };

    public void setMenuItem(JMenuItem menuItem) {
    	this.menuItem = menuItem;
    	setSelected(isCurrent());
    }    
    
    protected boolean isCurrent() {
        return getOWLModelManager().getActiveOntologiesStrategy().getClass().equals(getStrategy().getClass());
    }


    public void actionPerformed(ActionEvent event) {
        getOWLModelManager().setActiveOntologiesStrategy(getStrategy());
    }


    public void initialise() throws Exception {
    	getOWLModelManager().registerOntologySelectionStrategy(getStrategy());
        setSelected(isCurrent());
        getOWLModelManager().addListener(l);
    }
       
    public void setSelected(boolean selected) {
    	if (menuItem != null) {
    		menuItem.setSelected(selected);
    	}
    }
        
    public void dispose() throws Exception {
        getOWLModelManager().removeListener(l);
    }


    protected abstract OntologySelectionStrategy getStrategy();
}
