package org.protege.editor.owl.ui.frame;

import java.util.Set;

import org.semanticweb.owlapi.model.OWLAnnotation;

public class SWRLRuleUtils {

	public static OWLAnnotation findAnnotation(Set<OWLAnnotation> annotations) {
		return annotations.stream()
				.filter(a -> a.getProperty().getIRI().toString().equals("http://www.w3.org/2000/01/rdf-schema#comment"))
				.findAny().orElse(null);
	}
}
