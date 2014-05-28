package org.protege.editor.owl.model.axiom;

import org.semanticweb.owlapi.model.OWLOntology;

import java.util.List;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public class DefaultTopologicallySortedImportsClosureProvider implements TopologicallySortedImportsClosureProvider {
    @Override
    public List<OWLOntology> getTopologicallySortedImportsClosure(OWLOntology ontology) {
        return ontology.getOWLOntologyManager().getSortedImportsClosure(ontology);
    }
}
