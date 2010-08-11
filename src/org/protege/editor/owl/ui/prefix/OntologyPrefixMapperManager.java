package org.protege.editor.owl.ui.prefix;

import java.util.Map;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class OntologyPrefixMapperManager implements PrefixMapperManager {
	private OWLOntology ontology;
	private PrefixMapper mapper;
	
	public OntologyPrefixMapperManager(OWLOntology ontology) {
		this.ontology = ontology;
		mapper = new PrefixMapperImpl();
	}

	public void reload() {
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		OWLOntologyFormat format = manager.getOntologyFormat(ontology);
		if (format != null && format instanceof PrefixOWLOntologyFormat) {
			PrefixOWLOntologyFormat prefixes = (PrefixOWLOntologyFormat) format;
			for (String name : prefixes.getPrefixNames()) {
				mapper.addPrefixMapping(name, prefixes.getPrefix(name));
			}
		}
	}

	public void save() {
		OWLOntologyManager manager = ontology.getOWLOntologyManager();
		OWLOntologyFormat format = manager.getOntologyFormat(ontology);
		if (format != null && format instanceof PrefixOWLOntologyFormat) {
			// ToDo: need to clean up the existing prefixes - not supported by  the owl api yet
			// OWL API GForge 3042072.
			PrefixOWLOntologyFormat prefixes = (PrefixOWLOntologyFormat) format;
			for (String name : mapper.getPrefixes()) {
				prefixes.setPrefix(name, mapper.getPrefix(name));
			}
		}
	}

	public void setPrefixes(Map<String, String> prefix2ValueMap) {
		mapper = new PrefixMapperImpl(prefix2ValueMap);
	}
	
	public PrefixMapper getMapper() {
		return mapper;
	}

	public Map<String, String> getPrefixes() {
		return mapper.getPrefixMap();
	}



}
