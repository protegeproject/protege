package org.protege.editor.owl.ui.metrics;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.metrics.AbstractOWLMetric;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;
import org.semanticweb.owlapi.util.DLExpressivityChecker;
import org.semanticweb.owlapi.util.Languages;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2018-12-17
 */
public class DLExpressivityMetric extends AbstractOWLMetric<String> {

    public DLExpressivityMetric(@Nonnull OWLOntology o) {
        super(o);
    }

    /**
     * Recompute metric.
     *
     * @return the m
     */
    @Nonnull
    @Override
    protected String recomputeMetric() {
        DLExpressivityChecker checker = new DLExpressivityChecker(getOntologies());
        Collection<Languages> dlLangs = checker.expressibleInLanguages();
        return dlLangs.stream()
                .map(Enum::name)
                .collect(Collectors.joining(", "));
    }

    /**
     * Determines if the specified list of changes will cause the value of this
     * metric to be invalid.
     *
     * @param changes The list of changes which will be examined to determine if the
     *                metric is now invalid.
     * @return {@code true} if the metric value is invalidated by the specified
     * list of changes, or {@code false} if the list of changes do not
     * cause the value of this metric to be invalidated.
     */
    @Override
    protected boolean isMetricInvalidated(@Nonnull List<? extends OWLOntologyChange> changes) {
        return changes.stream()
                .filter(OWLOntologyChange::isAxiomChange)
                .map(OWLOntologyChange::getAxiom)
                .anyMatch(OWLAxiom::isLogicalAxiom);
    }

    /**
     * Dispose metric.
     */
    @Override
    protected void disposeMetric() {

    }

    /**
     * Gets the human readable name of this metric.
     *
     * @return A label which represents the human readable name of this metric.
     */
    @Nonnull
    @Override
    public String getName() {
        return "Expressivity";
    }
}
