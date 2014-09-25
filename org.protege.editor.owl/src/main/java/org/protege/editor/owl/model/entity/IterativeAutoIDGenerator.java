package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.OWLEntity;

import java.util.Stack;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 25, 2008<br><br>
 */
public class IterativeAutoIDGenerator extends AbstractIDGenerator implements Revertable {

	//TT: Not happy about this solution. The autoId generator is currently static,
	//and we need a way to say if the startId has changed in the preferences,
	//so that the generator will use the new start id.
	private long previousStartId;
    private long id;

    private Stack<Long> checkpoints = new Stack<Long>();


    public IterativeAutoIDGenerator() {
        id = EntityCreationPreferences.getAutoIDStart();
        previousStartId = id;      
    }


    protected long getRawID(Class<? extends OWLEntity> type) throws AutoIDException{
    	//check if start id prefs have changed meanwhile
    	if (previousStartId != EntityCreationPreferences.getAutoIDStart()) {
    		id = EntityCreationPreferences.getAutoIDStart();
    		previousStartId = id;
    		checkpoints.removeAllElements();
    		checkpoints.push(id);
    	}
    	
    	long end = EntityCreationPreferences.getAutoIDEnd();
        if (end != -1 && id > end){
            throw new AutoIDException("You have run out of IDs for creating new entities - max = " + end);
        }
        if (EntityCreationPreferences.getSaveAutoIDStart()) {
        	previousStartId = id + 1;
        	EntityCreationPreferences.setAutoIDStart((int) (previousStartId));
        }
        return id++;
    }


    public void checkpoint() {
        checkpoints.push(id);
    }


    public void revert() {
        id = checkpoints.pop();
    }
}
