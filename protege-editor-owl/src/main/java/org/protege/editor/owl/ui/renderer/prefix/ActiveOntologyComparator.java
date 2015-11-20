package org.protege.editor.owl.ui.renderer.prefix;

import org.semanticweb.owlapi.model.OWLOntology;

import java.util.Comparator;

public class ActiveOntologyComparator implements Comparator<OWLOntology> {

	public int compare(OWLOntology o1, OWLOntology o2) {
		boolean o1ImportsO2 = o1.getImportsClosure().contains(o2);
		boolean o2ImportsO1 = o2.getImportsClosure().contains(o1);
		if (o1ImportsO2 && !o2ImportsO1) {
			return -1;
		}
		else if (o2ImportsO1 && !o1ImportsO2) {
			return +1;
		}
		else {
			return o1.compareTo(o2);
		}
	}
}
