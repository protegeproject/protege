package org.protege.editor.owl.model.deprecation;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class ReparentVisitor implements OWLEntityVisitorEx<Optional<OWLAxiom>> {

    @Nonnull
    private final DeprecationProfile strategy;

    @Nonnull
    private final OWLDataFactory dataFactory;

    public ReparentVisitor(@Nonnull DeprecationProfile strategy,
                           @Nonnull OWLDataFactory dataFactory) {
        this.strategy = checkNotNull(strategy);
        this.dataFactory = checkNotNull(dataFactory);
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLClass cls) {
        return strategy.getDeprecatedClassParentIri()
                       .map(parent -> dataFactory.getOWLSubClassOfAxiom(cls, dataFactory.getOWLClass(parent)));
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLObjectProperty property) {
        return strategy.getDeprecatedObjectPropertyParentIri()
                       .map(parent -> dataFactory.getOWLSubObjectPropertyOfAxiom(property, dataFactory.getOWLObjectProperty(parent)));
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLDataProperty property) {
        return strategy.getDeprecatedDataPropertyParentIri()
                       .map(parent -> dataFactory.getOWLSubDataPropertyOfAxiom(property, dataFactory.getOWLDataProperty(parent)));
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLNamedIndividual individual) {
        return strategy.getDeprecatedIndividualParentClassIri()
                       .map(parent -> dataFactory.getOWLClassAssertionAxiom(dataFactory.getOWLClass(parent), individual));
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLDatatype datatype) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLAnnotationProperty property) {
        return strategy.getDeprecatedAnnotationPropertyParentIri()
                       .map(parent -> dataFactory.getOWLSubAnnotationPropertyOfAxiom(property, dataFactory.getOWLAnnotationProperty(parent)));
    }

}
