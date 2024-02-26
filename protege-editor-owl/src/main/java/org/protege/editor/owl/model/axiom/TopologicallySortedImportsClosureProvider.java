package org.protege.editor.owl.model.axiom;

import java.util.List;

import org.semanticweb.owlapi.model.OWLOntology;

/**
 * @author Matthew Horridge, Stanford University, Bio-Medical Informatics Research Group, Date: 27/05/2014
 */
public interface TopologicallySortedImportsClosureProvider {

    List<OWLOntology> getTopologicallySortedImportsClosure(OWLOntology ontology);
}
