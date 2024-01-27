package org.protege.editor.owl.model.util;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Nonnull;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLDatatype;
import org.semanticweb.owlapi.model.OWLEntity;
import org.semanticweb.owlapi.model.OWLEntityVisitorEx;
import org.semanticweb.owlapi.model.OWLNamedIndividual;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyChange;

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
