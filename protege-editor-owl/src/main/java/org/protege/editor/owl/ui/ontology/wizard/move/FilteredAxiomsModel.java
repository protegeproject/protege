package org.protege.editor.owl.ui.ontology.wizard.move;

import org.semanticweb.owlapi.model.OWLAxiom;
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
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 4, 2008<br><br>
 */
public interface FilteredAxiomsModel {

    void setFilteredAxioms(Set<OWLAxiom> axioms);

    Set<OWLAxiom> getUnfilteredAxioms(Set<OWLOntology> sourceOntologies);
}
