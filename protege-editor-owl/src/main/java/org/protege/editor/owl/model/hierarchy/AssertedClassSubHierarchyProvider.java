package org.protege.editor.owl.model.hierarchy;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntologyManager;

public class AssertedClassSubHierarchyProvider extends AssertedClassHierarchyProvider {

	public AssertedClassSubHierarchyProvider(OWLOntologyManager owlOntologyManager) {
		super(owlOntologyManager);		
	}
	
	public void setRoot(OWLClass r) { 
		root = r;
	}
}
