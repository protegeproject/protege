package org.protege.editor.owl.model.inference;

import java.awt.Component;

import javax.swing.JOptionPane;

import org.protege.editor.core.ProtegeApplication;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerConfiguration;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.reasoner.ReasonerProgressMonitor;
import org.slf4j.LoggerFactory;

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
	
	public static void warnUserIfReasonerIsNotConfigured(Component owner, OWLReasonerManager manager) {
		switch (manager.getReasonerStatus()) {
		case NO_REASONER_FACTORY_CHOSEN:
            JOptionPane.showMessageDialog(owner,
                    "No reasoner has been selected and initialized so inference cannot proceed. Select a reasoner from the Reasoner menu.",
                    "Reasoner not initialized.",
                    JOptionPane.WARNING_MESSAGE);
            break;
		case REASONER_NOT_INITIALIZED:
            JOptionPane.showMessageDialog(owner,
                    "No reasoner has been initialized so inference cannot proceed.  Go to the reasoner menu and select Start reasoner",
                    "Reasoner not initialized.",
                    JOptionPane.WARNING_MESSAGE);
            break;
		case OUT_OF_SYNC:
            JOptionPane.showMessageDialog(owner,
                    "The reasoner is not synchronized.  This may produce misleading results.  Consider Reasoner->Synchronize reasoner.",
                    "Reasoner out of sync",
                    JOptionPane.WARNING_MESSAGE);
            break;
		case INITIALIZATION_IN_PROGRESS:
            JOptionPane.showMessageDialog(owner,
                    "Reasoner still intializing.  Wait for initialization to complete.",
                    "Reasoner initializing.",
                    JOptionPane.WARNING_MESSAGE);
            break;
		case INITIALIZED:
			break; // nothing to do...
		}
	}
	
	public static void warnThatReasonerDied(Component owner, ReasonerDiedException died) {
		Throwable t = died.getCause();
		LoggerFactory.getLogger(ReasonerUtilities.class).error("Internal reasoner error: {}", t);
		JOptionPane.showMessageDialog(owner,
				                      "Internal reasoner error (see the logs for more info).",
				                      "Reasoner error",
				                      JOptionPane.WARNING_MESSAGE);
	}

}
