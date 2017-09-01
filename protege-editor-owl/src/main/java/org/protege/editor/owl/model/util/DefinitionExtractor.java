package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class DefinitionExtractor {


    @Nonnull
    private OWLEntity entity;

    @Nonnull
    private OWLOntology ontology;

    @Nonnull
    private OWLDataFactory dataFactory;

    public DefinitionExtractor(@Nonnull OWLEntity entity,
                               @Nonnull OWLOntology ontology,
                               @Nonnull OWLDataFactory dataFactory) {
        this.entity = checkNotNull(entity);
        this.ontology = checkNotNull(ontology);
        this.dataFactory = checkNotNull(dataFactory);
    }

    public Set<OWLAxiom> getDefiningAxioms() {
        return entity.accept(new OWLEntityVisitorEx<Set<OWLAxiom>>() {
            @Nonnull
            @Override
            public Set<OWLAxiom> visit(@Nonnull OWLClass cls) {
                return new ClassDefinitionExtractor(cls, ontology, dataFactory).getDefiningAxioms();
            }

            @Nonnull
            @Override
            public Set<OWLAxiom> visit(@Nonnull OWLObjectProperty property) {
                return new ObjectPropertyDefinitionExtractor(property, ontology, dataFactory).getDefiningAxioms();
            }

            @Nonnull
            @Override
            public Set<OWLAxiom> visit(@Nonnull OWLDataProperty property) {
                return Collections.emptySet();
            }

            @Nonnull
            @Override
            public Set<OWLAxiom> visit(@Nonnull OWLNamedIndividual individual) {
                return Collections.emptySet();
            }

            @Nonnull
            @Override
            public Set<OWLAxiom> visit(@Nonnull OWLDatatype datatype) {
                return Collections.emptySet();
            }

            @Nonnull
            @Override
            public Set<OWLAxiom> visit(@Nonnull OWLAnnotationProperty property) {
                return Collections.emptySet();
            }
        });
    }

    public List<OWLOntologyChange> getChangesToRemoveDefinition() {
        return entity.accept(new OWLEntityVisitorEx<List<OWLOntologyChange>>() {
            @Nonnull
            @Override
            public List<OWLOntologyChange> visit(@Nonnull OWLClass cls) {
                return new ClassDefinitionExtractor(cls, ontology, dataFactory)
                        .getChangesToRemoveDefinition();
            }

            @Nonnull
            @Override
            public List<OWLOntologyChange> visit(@Nonnull OWLObjectProperty property) {
                return new ObjectPropertyDefinitionExtractor(property, ontology, dataFactory)
                        .getChangesToRemoveDefinition();
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
