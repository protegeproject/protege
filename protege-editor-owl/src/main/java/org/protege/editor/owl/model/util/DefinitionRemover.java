package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.search.EntitySearcher;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class DefinitionRemover {


    @Nonnull
    private OWLEntity entity;

    @Nonnull
    private Set<OWLOntology> ontologies;

    @Nonnull
    private OWLDataFactory dataFactory;

    public DefinitionRemover(@Nonnull OWLEntity entity,
                             @Nonnull Set<OWLOntology> ontologies,
                             @Nonnull OWLDataFactory dataFactory) {
        this.entity = checkNotNull(entity);
        this.ontologies = checkNotNull(ontologies);
        this.dataFactory = checkNotNull(dataFactory);
    }

    public List<OWLOntologyChange> getChangesToRemoveDefinition() {
        return entity.accept(new OWLEntityVisitorEx<List<OWLOntologyChange>>() {
            @Nonnull
            @Override
            public List<OWLOntologyChange> visit(@Nonnull OWLClass cls) {
                return new ClassDefinitionRemover(cls, ontologies, dataFactory).getChangesToRemoveDefinition();
            }

            @Nonnull
            @Override
            public List<OWLOntologyChange> visit(@Nonnull OWLObjectProperty property) {
                return Collections.emptyList();
            }

            @Nonnull
            @Override
            public List<OWLOntologyChange> visit(@Nonnull OWLDataProperty property) {
                return Collections.emptyList();
            }

            @Nonnull
            @Override
            public List<OWLOntologyChange> visit(@Nonnull OWLNamedIndividual individual) {
                return Collections.emptyList();
            }

            @Nonnull
            @Override
            public List<OWLOntologyChange> visit(@Nonnull OWLDatatype datatype) {
                return Collections.emptyList();
            }

            @Nonnull
            @Override
            public List<OWLOntologyChange> visit(@Nonnull OWLAnnotationProperty property) {
                return Collections.emptyList();
            }
        });
    }
}
