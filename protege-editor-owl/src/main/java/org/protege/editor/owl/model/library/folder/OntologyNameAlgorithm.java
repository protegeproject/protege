package org.protege.editor.owl.model.library.folder;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.*;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

public class OntologyNameAlgorithm implements Algorithm {

	@Override
	public Set<URI> getSuggestions(final File f) {
		try {
			final IRI iri = IRI.create(f);
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			manager.addIRIMapper(ontologyIRI -> {
                if (ontologyIRI.equals(iri)) {
                    return IRI.create(f);
                }
                else {
                    return IRI.create("http://hopefully.not.a.valid.host.name");
                }
            });
			OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
			configuration = configuration.setLoadAnnotationAxioms(false);
			configuration = configuration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
			OWLOntology ontology = manager.loadOntology(iri);
			Set<URI> suggestions = new TreeSet<>();
			OWLOntologyID id = ontology.getOntologyID();
			if (id.getOntologyIRI().isPresent()) {
				suggestions.add(id.getOntologyIRI().get().toURI());
				if (id.getVersionIRI().isPresent()) {
					suggestions.add(id.getVersionIRI().get().toURI());
				}
			}
			return suggestions;
		}
		catch (Throwable t) {
			return Collections.emptySet();
		}
	}

}
