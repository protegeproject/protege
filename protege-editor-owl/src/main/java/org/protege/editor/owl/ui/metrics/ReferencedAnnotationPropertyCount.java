package org.protege.editor.owl.ui.metrics;

import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.metrics.ObjectCountMetric;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2 Feb 2017
 */
public class ReferencedAnnotationPropertyCount extends ObjectCountMetric<OWLAnnotationProperty> {

    public ReferencedAnnotationPropertyCount(@Nonnull OWLOntology o) {
        super(o);
    }

    @Nonnull
    @Override
    protected String getObjectTypeName() {
        return "Annotation Property";
    }

    @Nonnull
    @Override
    protected Set<? extends OWLAnnotationProperty> getObjects(@Nonnull OWLOntology owlOntology) {
        return owlOntology.getAnnotationPropertiesInSignature();
    }
}
