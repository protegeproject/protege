package org.protege.editor.owl.ui.metrics;

import org.semanticweb.owlapi.metrics.ObjectCountMetric;
import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.annotation.Nonnull;
import java.util.stream.Stream;

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
    protected Stream<OWLAnnotationProperty> getObjects(@Nonnull OWLOntology owlOntology) {
        return owlOntology.annotationPropertiesInSignature();
    }
}
