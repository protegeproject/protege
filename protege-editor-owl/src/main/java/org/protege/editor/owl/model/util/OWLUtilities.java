package org.protege.editor.owl.model.util;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.*;

import java.util.Collection;
import java.util.Set;

public class OWLUtilities {

	private OWLUtilities() {
		;
	}
	
    public static boolean isDeprecated(OWLModelManager p4Manager, OWLObject o) {
    	if (!(o instanceof OWLEntity)) {
    		return false;
    	}
		Set<OWLOntology> activeOntologies = p4Manager.getActiveOntologies();
		return isDeprecated((OWLEntity) o, activeOntologies);
    }

	public static boolean isDeprecated(OWLEntity o, Collection<OWLOntology> ontologies) {
		for (OWLOntology ontology : ontologies) {
    		for (OWLAnnotationAssertionAxiom assertion : ontology.getAnnotationAssertionAxioms(o.getIRI())) {

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
