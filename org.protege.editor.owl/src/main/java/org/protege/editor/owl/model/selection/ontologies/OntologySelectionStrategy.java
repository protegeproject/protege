package org.protege.editor.owl.model.selection.ontologies;

import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;/*
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
 * Date: Jun 6, 2008<br><br>
 */
public interface OntologySelectionStrategy {

    Set<OWLOntology> getOntologies();

    String getName();
}
