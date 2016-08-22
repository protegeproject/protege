package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.IllegalConfigurationException;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;

import javax.annotation.Nonnull;

public class NoOpReasonerFactory implements OWLReasonerFactory {

	@Nonnull
	public String getReasonerName() {
		return "Null Reasoner";
	}

	@Nonnull
	public OWLReasoner createNonBufferingReasoner(@Nonnull OWLOntology ontology) {
		return new NoOpReasoner(ontology);
	}

	@Nonnull
	public OWLReasoner createReasoner(@Nonnull OWLOntology ontology) {
		return new NoOpReasoner(ontology);

	}

	@Nonnull
	public OWLReasoner createNonBufferingReasoner(@Nonnull OWLOntology ontology,
												  @Nonnull OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		return new NoOpReasoner(ontology);
	}

	@Nonnull
	public OWLReasoner createReasoner(@Nonnull OWLOntology ontology,
									  @Nonnull OWLReasonerConfiguration config)
			throws IllegalConfigurationException {
		return new NoOpReasoner(ontology);
	}

}
