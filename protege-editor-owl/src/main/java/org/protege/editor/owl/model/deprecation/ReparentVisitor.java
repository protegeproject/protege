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
    private final DeprecatedEntityStrategy strategy;

    @Nonnull
    private final OWLDataFactory dataFactory;

    public ReparentVisitor(@Nonnull DeprecatedEntityStrategy strategy,
                           @Nonnull OWLDataFactory dataFactory) {
        this.strategy = checkNotNull(strategy);
        this.dataFactory = checkNotNull(dataFactory);
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLClass cls) {
        return strategy.getDeprecatedClassParent()
                       .map(parent -> dataFactory.getOWLSubClassOfAxiom(cls, parent));
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLObjectProperty property) {
        return strategy.getDeprecatedObjectPropertyParent()
                       .map(parent -> dataFactory.getOWLSubObjectPropertyOfAxiom(property, parent));
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLDataProperty property) {
        return strategy.getDeprecatedDataPropertyParent()
                       .map(parent -> dataFactory.getOWLSubDataPropertyOfAxiom(property, parent));
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLNamedIndividual individual) {
        return strategy.getDeprecatedIndividualParent()
                       .map(parent -> dataFactory.getOWLClassAssertionAxiom(parent, individual));
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLDatatype datatype) {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<OWLAxiom> visit(@Nonnull OWLAnnotationProperty property) {
        return strategy.getDeprecatedAnnotationPropertyParent()
                       .map(parent -> dataFactory.getOWLSubAnnotationPropertyOfAxiom(property, parent));
    }

}
