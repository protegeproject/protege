package org.protege.editor.owl.ui.action.export.inferred;

import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;

import javax.swing.*;
import java.util.Set;

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

    public Set<A> createAxioms(OWLDataFactory dataFactory, OWLReasoner reasoner) {
        if (progressMonitor.isCanceled()) {
            throw new ExportCancelledException();
        }
        progressMonitor.setNote(delegate.getLabel());
        progressMonitor.setProgress(task);
        return delegate.createAxioms(dataFactory, reasoner);
    }

    public String getLabel() {
        return delegate.getLabel();
    }

    public InferredAxiomGenerator<A> getDelegate() {
        return delegate;
    }


}
