package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.OWLEntity;

import uk.org.russet.identitas.*;

public class RandomProlong extends AbstractIDGenerator implements AutoIDGenerator, Revertable {

	
	private String nextId;
    public RandomProlong () {
    	try {
			nextId = Util.randomProlong();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
	
    public String getNextID(Class<? extends OWLEntity> type) throws AutoIDException {
        String id = nextId;
        try {
			nextId = Util.randomProlong();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        return getPrefix(type) + id.toString().replace("-", "_") + getSuffix(type);
        
    }
    
	@Override
	protected long getRawID(Class<? extends OWLEntity> type) throws AutoIDException {
		// TODO Auto-generated method stub
		return 0;
	}
	
	
	@Override
	public void checkpoint() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void revert() {
		// TODO Auto-generated method stub
		
	}


}
