package org.protege.editor.owl.model.util;

import java.util.Collection;
import java.util.Set;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAnnotationAssertionAxiom;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLLiteral;
import org.semanticweb.owlapi.model.OWLObject;
import org.semanticweb.owlapi.model.OWLOntology;

public class OWLUtilities {

	private OWLUtilities() {
	}
	
    public static boolean isDeprecated(@Nonnull OWLModelManager manager,
									   @Nonnull OWLObject o) {
    	if (!(o instanceof OWLEntity)) {
    		return false;
    	}
		Set<OWLOntology> activeOntologies = manager.getActiveOntologies();
		return isDeprecated((OWLEntity) o, activeOntologies);
    }

	public static boolean isDeprecated(@Nonnull OWLEntity o,
									   @Nonnull Collection<OWLOntology> ontologies) {
		return isDeprecated(o.getIRI(), ontologies);
	}

	public static boolean isDeprecated(@Nonnull IRI iri,
									   @Nonnull Collection<OWLOntology> ontologies) {
		for (OWLOntology ontology : ontologies) {
			for (OWLAnnotationAssertionAxiom assertion : ontology.getAnnotationAssertionAxioms(iri)) {
				if (!assertion.getProperty().isDeprecated()) {
					continue;
				}
				if (!(assertion.getValue() instanceof OWLLiteral)) {
					continue;
				}
				OWLLiteral value = (OWLLiteral) assertion.getValue();
				if (!value.isBoolean()) {
					continue;
				}
				if (value.parseBoolean()) {
					return true;
				}
			}
		}
		return false;
	}
}
