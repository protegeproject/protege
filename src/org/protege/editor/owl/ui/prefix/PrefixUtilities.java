package org.protege.editor.owl.ui.prefix;

import java.util.Collections;
import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class PrefixUtilities {

	public static final Set<String> STANDARD_PREFIXES = Collections.unmodifiableSet(new DefaultPrefixManager().getPrefixNames());

	public static PrefixOWLOntologyFormat getPrefixOWLOntologyFormat(OWLModelManager modelManager) {
		return PrefixUtilities.getPrefixOWLOntologyFormat(modelManager.getActiveOntology());
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
