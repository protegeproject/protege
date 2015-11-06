package org.protege.editor.owl.model.util;

import org.protege.editor.owl.model.OWLModelManager;
import org.semanticweb.owlapi.model.*;

public class OWLUtilities {

	private OWLUtilities() {
		;
	}
	
    public static boolean isDeprecated(OWLModelManager p4Manager, OWLObject o) {
    	if (!(o instanceof OWLEntity)) {
    		return false;
    	}
    	for (OWLOntology ontology : p4Manager.getActiveOntologies()) {
    		for (OWLAnnotationAssertionAxiom assertion : ontology.getAnnotationAssertionAxioms(((OWLEntity) o).getIRI())) {
    			
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
