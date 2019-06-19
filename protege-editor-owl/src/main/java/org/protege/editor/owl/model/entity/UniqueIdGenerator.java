package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.OWLEntity;

import java.util.UUID;

/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>

 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 25, 2008<br><br>
 */
public class UniqueIdGenerator extends AbstractIDGenerator implements AutoIDGenerator, Revertable {

    private UUID nextId = UUID.randomUUID();


    public String getNextID(Class<? extends OWLEntity> type) throws AutoIDException {
        UUID id = nextId;
        nextId = UUID.randomUUID();
        return getPrefix(type) + id.toString() + getSuffix(type);
    }

    protected long getRawID(Class<? extends OWLEntity> type) throws AutoIDException {
    	throw new UnsupportedOperationException("Shouldn't get here");
    }


    public void checkpoint() {
    }


    public void revert() {
    }

}
