package org.protege.editor.owl.ui.prefix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.prefix.ActiveOntologyComparator;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class PrefixUtilities {
	public static final Logger LOGGER = Logger.getLogger(PrefixUtilities.class);

	public static final Set<String> STANDARD_PREFIXES = Collections.unmodifiableSet(new DefaultPrefixManager().getPrefixNames());

	public static PrefixManager getPrefixOWLOntologyFormat(OWLModelManager modelManager) {
		OWLOntologyManager owlManager = modelManager.getOWLOntologyManager();
		DefaultPrefixManager prefixes = new DefaultPrefixManager();
		List<OWLOntology> ontologies = new ArrayList<OWLOntology>(modelManager.getOntologies());
		Collections.sort(ontologies, new ActiveOntologyComparator());
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Sorted ontologies = " + ontologies);
		}
		Set<String> prefixNames
		Set<String> prefixValues = new HashSet<String>();
		for (OWLOntology ontology : ontologies) {
			OWLOntologyFormat format = owlManager.getOntologyFormat(ontology);
			if (format instanceof PrefixOWLOntologyFormat) {
				PrefixOWLOntologyFormat newPrefixes = (PrefixOWLOntologyFormat) format;
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

	public static PrefixOWLOntologyFormat getPrefixOWLOntologyFormat(OWLOntology ontology) {
		PrefixOWLOntologyFormat prefixManager = null;
		if (ontology != null) {
			OWLOntologyManager manager = ontology.getOWLOntologyManager();
			OWLOntologyFormat format = manager.getOntologyFormat(ontology);
			if (format.isPrefixOWLOntologyFormat()) {
				prefixManager = format.asPrefixOWLOntologyFormat();
			}
		}
		if (prefixManager == null) {
			prefixManager = new PrefixOWLOntologyFormat();
		}
		return prefixManager;
	}

	public static boolean isStandardPrefix(String prefix) {
		return STANDARD_PREFIXES.contains(prefix + ":");
	}
}
