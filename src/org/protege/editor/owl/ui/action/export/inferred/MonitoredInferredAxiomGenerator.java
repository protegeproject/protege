package org.protege.editor.owl.ui.action.export.inferred;

import java.util.HashSet;
import java.util.Set;

import javax.swing.ProgressMonitor;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;

public class MonitoredInferredAxiomGenerator<A extends OWLAxiom> implements InferredAxiomGenerator<A> {
	private InferredAxiomGenerator<A> delegate;
	private ProgressMonitor progressMonitor;
	private int task;
	
	public MonitoredInferredAxiomGenerator(InferredAxiomGenerator<A> delegate) {
			this.delegate = delegate;
	}
	
	public void setProgressMonitor(ProgressMonitor progressMonitor, int task) {
		this.progressMonitor = progressMonitor;
		this.task = task;
	}

	public Set<A> createAxioms(OWLOntologyManager manager, OWLReasoner reasoner) {
		if (progressMonitor.isCanceled()) {
			throw new ExportCancelledException();
		}
		progressMonitor.setNote(delegate.getLabel());
		progressMonitor.setProgress(task);
		return delegate.createAxioms(manager, reasoner);
	}

	public String getLabel() {
		return delegate.getLabel();
	}
	
	public InferredAxiomGenerator<A> getDelegate() {
		return delegate;
	}
	
	
}
