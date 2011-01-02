package org.protege.editor.owl.ui.renderer.prefix;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyFormat;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.model.PrefixManager;
import org.semanticweb.owlapi.util.DefaultPrefixManager;
import org.semanticweb.owlapi.vocab.PrefixOWLOntologyFormat;

public class ActiveOntologyComparator implements Comparator<OWLOntology> {

	public int compare(OWLOntology o1, OWLOntology o2) {
		boolean o1ImportsO2 = o1.getImportsClosure().contains(o2);
		boolean o2ImportsO1 = o2.getImportsClosure().contains(o1);
		if (o1ImportsO2 && !o2ImportsO1) {
			return 1;
		}
		else if (o2ImportsO1 && !o1ImportsO2) {
			return -1;
		}
		else {
			return o1.compareTo(o2);
		}
	}

	public static PrefixManager createPrefixMap(OWLModelManager manager) {
		OWLOntologyManager owlManager = manager.getOWLOntologyManager();
		DefaultPrefixManager prefixes = new DefaultPrefixManager();
		List<OWLOntology> ontologies = new ArrayList<OWLOntology>(manager.getActiveOntologies());
		Collections.sort(ontologies, new ActiveOntologyComparator());
		for (OWLOntology ontology : ontologies) {
			OWLOntologyFormat format = owlManager.getOntologyFormat(ontology);
			if (format instanceof PrefixOWLOntologyFormat) {
				PrefixOWLOntologyFormat newPrefixes = (PrefixOWLOntologyFormat) format;
				for (Entry<String, String> entry : newPrefixes.getPrefixName2PrefixMap().entrySet()) {
					String prefixName = entry.getKey();
					String prefix     = entry.getValue();
					prefixes.setPrefix(prefixName, prefix);
				}
			}
		}
		return prefixes;
	}
}
