package org.protege.editor.owl.model.inference;

import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;

public class ReasonerUtilities {
	
	public static OWLReasoner createReasoner(OWLOntology ontology, ProtegeOWLReasonerInfo info, ReasonerProgressMonitor monitor) {
		OWLReasonerFactory factory = info.getReasonerFactory();
		OWLReasonerConfiguration configuration = info.getConfiguration(monitor);
		switch (info.getRecommendedBuffering()) {
		case BUFFERING:
			return factory.createReasoner(ontology, configuration);
		case NON_BUFFERING:
			return factory.createNonBufferingReasoner(ontology, configuration);
		default:
			throw new UnsupportedOperationException("Programmer error: missed a case " + info.getRecommendedBuffering());
		}
	}

}
