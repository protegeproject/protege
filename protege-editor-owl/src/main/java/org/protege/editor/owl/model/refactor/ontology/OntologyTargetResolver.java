package org.protege.editor.owl.model.refactor.ontology;

import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;
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
 * Date: Aug 21, 2008<br><br>
 */
public interface OntologyTargetResolver {

    Set<OWLOntology> resolve(OWLEntity entity, Set<OWLOntology> ontologies);
}
