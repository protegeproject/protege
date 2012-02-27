package org.protege.editor.owl.model.refactor.ontology;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.model.OWLOntologyID;

import java.util.List;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 26/02/2012
 */
public interface OntologyIDChangeStrategy {

    /**
     * Gets a list of ontology changes that should be applied to an ontology which has been renamed (had its ontology
     * id changed).
     * @param ontology The ontology that has had its id changed.
     * @param from The original ontology id.
     * @param to The new ontology id.
     * @return A list of ontology changes that should be applied.
     */
    List<OWLOntologyChange> getChangesForRename(OWLOntology ontology, OWLOntologyID from, OWLOntologyID to);
    
}
