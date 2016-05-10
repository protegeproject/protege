package org.protege.editor.owl.model.entity;

import org.semanticweb.owlapi.model.OWLEntity;
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
public interface AutoIDGenerator {
    
    String getNextID(Class<? extends OWLEntity> type) throws AutoIDException;
}
