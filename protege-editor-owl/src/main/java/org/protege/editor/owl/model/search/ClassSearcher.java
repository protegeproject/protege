package org.protege.editor.owl.model.search;



import java.awt.Component;

import org.semanticweb.owlapi.model.OWLClass;


public interface ClassSearcher {
	OWLClass searchFor(Component parent);

}
