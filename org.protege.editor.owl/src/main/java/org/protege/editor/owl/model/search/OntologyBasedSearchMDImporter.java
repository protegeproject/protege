package org.protege.editor.owl.model.search;

import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 03/10/2012
 */
public abstract class OntologyBasedSearchMDImporter extends SearchMDImporter {

    public abstract boolean isImporterFor(Set<SearchCategory> categories);

    public abstract void generateSearchMetadata(OWLOntology ontology, SearchMetadataImportContext context, SearchMetadataDB db);
}
