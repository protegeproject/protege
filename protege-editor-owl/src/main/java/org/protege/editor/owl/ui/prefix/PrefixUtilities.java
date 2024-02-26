package org.protege.editor.owl.ui.prefix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.prefix.ActiveOntologyComparator;
import org.semanticweb.owlapi.formats.PrefixDocumentFormat;
import org.semanticweb.owlapi.formats.PrefixDocumentFormatImpl;
import org.semanticweb.owlapi.model.OWLDocumentFormat;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PrefixUtilities {

	private static final Logger LOGGER = LoggerFactory.getLogger(PrefixUtilities.class);

	public static final Set<String> STANDARD_PREFIXES = Collections.unmodifiableSet(new DefaultPrefixManager().getPrefixNames());

	public static PrefixManager getPrefixOWLOntologyFormat(OWLModelManager modelManager) {
		OWLOntologyManager owlManager = modelManager.getOWLOntologyManager();
		DefaultPrefixManager prefixes = new DefaultPrefixManager();
		List<OWLOntology> ontologies = new ArrayList<>(modelManager.getOntologies());
		ontologies.sort(new ActiveOntologyComparator());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Sorted ontologies = " + ontologies);
		}
		Set<String> prefixValues = new HashSet<>();
		for (OWLOntology ontology : ontologies) {
			OWLDocumentFormat format = owlManager.getOntologyFormat(ontology);
			if (format instanceof PrefixDocumentFormat) {
				PrefixDocumentFormat newPrefixes = (PrefixDocumentFormat) format;
				for (Entry<String, String> entry : newPrefixes.getPrefixName2PrefixMap().entrySet()) {
					String prefixName = entry.getKey();
					String prefix     = entry.getValue();
					if (!prefixes.containsPrefixMapping(prefixName) && !prefixValues.contains(prefix)) {
						prefixes.setPrefix(prefixName, prefix);
						prefixValues.add(prefix);
					}
				}
			}
		}
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Merged prefix to prefix value map = " + prefixes.getPrefixName2PrefixMap());
		}
		return prefixes;
	}

	public static PrefixDocumentFormat getPrefixOWLOntologyFormat(OWLOntology ontology) {
		PrefixDocumentFormat prefixManager = null;
		if (ontology != null) {
			OWLOntologyManager manager = ontology.getOWLOntologyManager();
			OWLDocumentFormat format = manager.getOntologyFormat(ontology);
			if (format != null && format.isPrefixOWLOntologyFormat()) {
				prefixManager = format.asPrefixOWLOntologyFormat();
			}
		}
		if (prefixManager == null) {
			prefixManager = new PrefixDocumentFormatImpl();
		}
		return prefixManager;
	}

	public static boolean isStandardPrefix(String prefix) {
		return STANDARD_PREFIXES.contains(prefix + ":");
	}
}
