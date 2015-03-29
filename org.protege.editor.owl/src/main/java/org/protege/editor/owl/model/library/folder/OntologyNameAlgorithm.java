package org.protege.editor.owl.model.library.folder;

import java.io.File;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.MissingImportHandlingStrategy;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyID;
import org.semanticweb.owlapi.model.OWLOntologyIRIMapper;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class OntologyNameAlgorithm implements Algorithm {

	@Override
	public Set<URI> getSuggestions(final File f) {
		try {
			final IRI iri = IRI.create(f);
			OWLOntologyManager manager = OWLManager.createOWLOntologyManager();
			manager.addIRIMapper(new OWLOntologyIRIMapper() {
				
				@Override
				public IRI getDocumentIRI(IRI ontologyIRI) {
					if (ontologyIRI.equals(iri)) {
						return IRI.create(f);
					}
					else {
						return IRI.create("http://hopefully.not.a.valid.host.name");
					}
				}
			});
			OWLOntologyLoaderConfiguration configuration = new OWLOntologyLoaderConfiguration();
			configuration = configuration.setLoadAnnotationAxioms(false);
			configuration = configuration.setMissingImportHandlingStrategy(MissingImportHandlingStrategy.SILENT);
			OWLOntology ontology = manager.loadOntology(iri);
			Set<URI> suggestions = new TreeSet<URI>();
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
