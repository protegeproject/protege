package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

public class NoOpReasonerFactory implements OWLReasonerFactory {

	public String getReasonerName() {
		return "Null Reasoner";
	}

	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology) {
		return new NoOpReasoner(ontology);
	}

	public OWLReasoner createReasoner(OWLOntology ontology) {
		return new NoOpReasoner(ontology);

	}

	public OWLReasoner createNonBufferingReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		// TODO Auto-generated method stub
		return new NoOpReasoner(ontology);
	}

	public OWLReasoner createReasoner(OWLOntology ontology,
			OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		return new NoOpReasoner(ontology);
	}

}
