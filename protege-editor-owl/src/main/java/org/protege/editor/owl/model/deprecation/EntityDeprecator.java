package org.protege.editor.owl.model.deprecation;

import org.protege.editor.owl.model.entity.HomeOntologySupplier;
import org.protege.editor.owl.model.util.DefinitionRemover;
import org.protege.editor.owl.model.util.LiteralLexicalValueReplacer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AxiomSubjectProvider;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;
import org.semanticweb.owlapi.vocab.OWL2Datatype;

import javax.annotation.Nonnull;
import java.util.*;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singleton;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class EntityDeprecator<E extends OWLEntity> {

    @Nonnull
    private final OWLDataFactory dataFactory;

    @Nonnull
    private final Set<OWLOntology> ontologies = new HashSet<>();

    @Nonnull
    private final DeprecateEntityInfo<E> info;

    @Nonnull
    private final DeprecatedEntityStrategy strategy;

    @Nonnull
    private final HomeOntologySupplier homeOntologySupplier;

    public EntityDeprecator(@Nonnull DeprecateEntityInfo<E> info,
                            @Nonnull DeprecatedEntityStrategy strategy,
                            @Nonnull Set<OWLOntology> ontologies,
                            @Nonnull HomeOntologySupplier homeOntologySupplier,
                            @Nonnull OWLDataFactory dataFactory) {
        this.dataFactory = checkNotNull(dataFactory);
        this.strategy = checkNotNull(strategy);
        this.homeOntologySupplier = checkNotNull(homeOntologySupplier);
        this.ontologies.addAll(ontologies);
        this.info = checkNotNull(info);
    }

    public List<OWLOntologyChange> getChanges() {
        List<OWLOntologyChange> changes = new ArrayList<>();
        addDeprecatedAnnotationAssertion(changes);
        relabelDeprecatedEntity(changes);
        replacePreservedAnnotationAssertions(changes);
        addDeprecationReason(changes);
        addReplacedByAnnotation(changes);
        addAlternateEntityAnnotations(changes);
        removeLogicalDefinition(changes);
        removeAnnotationAssertions(changes);
        replaceReferencingLogicalAxioms(changes);
        cleanUpDanglingReferences(changes);
        reparentDeprecatedEntity(changes);
        return changes;
    }

    /**
     * Adds an annotation assertion that specifies that the entity is deprecated.
     * @param changes A list of changes that the enacting change will be added to.
     */
    private void addDeprecatedAnnotationAssertion(@Nonnull List<OWLOntologyChange> changes) {
        OWLAnnotationAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                dataFactory.getOWLDeprecated(),
                info.getEntityToDeprecate().getIRI(),
                dataFactory.getOWLLiteral(true)
        );
        changes.add(new AddAxiom(getHomeOntology(), ax));
    }


    /**
     * Removes axioms that logically define the deprecated entity.
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void removeLogicalDefinition(@Nonnull List<OWLOntologyChange> changes) {
        if (!strategy.shouldRemoveLogicalDefinition()) {
            return;
        }
        DefinitionRemover definitionRemover = new DefinitionRemover(info.getEntityToDeprecate(),
                                                                    ontologies,
                                                                    dataFactory);
        changes.addAll(definitionRemover.getChangesToRemoveDefinition());
    }

    /**
     * Removes annotation assertions that are along annotation properties that are not rdfs:label or any
     * preserved annotation properties.
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void removeAnnotationAssertions(@Nonnull List<OWLOntologyChange> changes) {
        if (!strategy.shouldRemoveAnnotationAssertions()) {
            return;
        }
        ontologies.forEach(o -> {
            o.getAnnotationAssertionAxioms(info.getEntityToDeprecate().getIRI()).stream()
             .filter(ax -> !ax.getProperty().isLabel())
             .filter(ax -> !strategy.getPreservedAnnotationProperties().contains(ax.getProperty()))
             .forEach(ax -> changes.add(new RemoveAxiom(o, ax)));
        });
    }

    /**
     * Relabels the deprecated entity by replacing existing labels with their original values prefixed by the value
     * returned by {@link DeprecatedEntityStrategy#getDeprecatedEntityLabelPrefix()}.
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void relabelDeprecatedEntity(@Nonnull List<OWLOntologyChange> changes) {
        String prefix = strategy.getDeprecatedEntityLabelPrefix();
        if (prefix.isEmpty()) {
            return;
        }
        String replacement = prefix.trim() + " $0";
        replaceAnnotationAssertions(changes,
                                    singleton(dataFactory.getRDFSLabel()),
                                    replacement);
    }

    /**
     * Replaces preserved annotations (as defined by {@link DeprecatedEntityStrategy#getPreservedAnnotationProperties()})
     * with annotations that are prefixed with the value returned by
     * {@link DeprecatedEntityStrategy#getDeprecatedEntityAnnotationValuePrefix()}
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void replacePreservedAnnotationAssertions(@Nonnull List<OWLOntologyChange> changes) {
        String prefix = strategy.getDeprecatedEntityAnnotationValuePrefix().trim();
        if (prefix.isEmpty()) {
            return;
        }
        String replacement = prefix.trim() + " $0";
        replaceAnnotationAssertions(changes,
                                    strategy.getPreservedAnnotationProperties(),
                                    replacement);
    }

    /**
     * Adds an annotation that specifies a human readable reason/explanation as to why the entity is deprecated.
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void addDeprecationReason(List<OWLOntologyChange> changes) {
        String reasonForDeprecation = info.getReasonForDeprecation().trim();
        if (reasonForDeprecation.isEmpty()) {
            return;
        }
        OWLAnnotationAssertionAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                strategy.getDeprecationReasonAnnotationProperty(),
                info.getEntityToDeprecate().getIRI(),
                dataFactory.getOWLLiteral(reasonForDeprecation, OWL2Datatype.XSD_STRING)
        );
        changes.add(new AddAxiom(getHomeOntology(), ax));
    }

    /**
     * Adds an annotation that specifies a replacement for the deprecated entity.
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void addReplacedByAnnotation(List<OWLOntologyChange> changes) {
        info.getReplacementEntity().ifPresent(replacementEntity -> {
            OWLAnnotationProperty prop = strategy.getReplacedByAnnotationProperty();
            OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                    prop,
                    info.getEntityToDeprecate().getIRI(),
                    replacementEntity.getIRI()
            );
            changes.add(new AddAxiom(getHomeOntology(), ax));
        });
    }

    /**
     * Adds a set of annotations that specify alternate entities that should be used for consumers of the ontology.
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void addAlternateEntityAnnotations(List<OWLOntologyChange> changes) {
        info.getAlternateEntities().forEach(alternate -> {
            OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                    strategy.getAlternateEntityAnnotationProperty(),
                    info.getEntityToDeprecate().getIRI(),
                    alternate.getIRI()
            );
            changes.add(new AddAxiom(getHomeOntology(), ax));
        });
    }

    /**
     * Reparents the deprecated entity.
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void reparentDeprecatedEntity(List<OWLOntologyChange> changes) {
        E deprecatedEntity = info.getEntityToDeprecate();
        Optional<OWLAxiom> ax = deprecatedEntity.accept(new ReparentVisitor(strategy, dataFactory));
        ax.map(axiom -> new AddAxiom(getHomeOntology(), axiom)).ifPresent(changes::add);
    }


    /**
     * Replaces logical axioms that mention the deprecated entity with axioms that mention the deprecated entity
     * replacement.
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void replaceReferencingLogicalAxioms(@Nonnull List<OWLOntologyChange> changes) {
        info.getReplacementEntity().ifPresent(replacementEntity -> {
            Map<OWLEntity, IRI> replacementMap = new HashMap<>();
            replacementMap.put(info.getEntityToDeprecate(), replacementEntity.getIRI());
            OWLObjectDuplicator duplicator = new OWLObjectDuplicator(replacementMap, dataFactory);
            ontologies.forEach(o -> {
                o.getReferencingAxioms(info.getEntityToDeprecate()).stream()
                 // Only replace the entity in logical axioms
                 .filter(OWLAxiom::isLogicalAxiom)
                 // Don't replace entity in axioms that define the entity
                 .filter(ax -> !new AxiomSubjectProvider().getSubject(ax).equals(info.getEntityToDeprecate()))
                 .forEach(ax -> {
                     OWLAxiom replacementAx = duplicator.duplicateObject(ax);
                     changes.add(new RemoveAxiom(o, ax));
                     changes.add(new AddAxiom(o, replacementAx));
                 });
            });
        });
    }

    /**
     * If a replacement entity is not specified then this method removes any axioms that mention the deprecated entity.
     * If a replacement entity is specified then these axioms will be removed anyway when the entity is replaced.
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void cleanUpDanglingReferences(@Nonnull List<OWLOntologyChange> changes) {
        if (!info.getReplacementEntity().isPresent() && strategy.shouldRemoveDanglingReferences()) {
            ontologies.forEach(o -> {
                o.getReferencingAxioms(info.getEntityToDeprecate()).stream()
                 .filter(OWLAxiom::isLogicalAxiom)
                 .map(ax -> new RemoveAxiom(o, ax))
                 .forEach(changes::add);
            });
        }
    }

    private void replaceAnnotationAssertions(@Nonnull List<OWLOntologyChange> changes,
                                             @Nonnull Set<OWLAnnotationProperty> properties,
                                             @Nonnull String replacement) {
        ontologies.forEach(o -> {
            IRI entityIri = info.getEntityToDeprecate().getIRI();
            o.getAnnotationAssertionAxioms(entityIri).stream()
             .filter(ax -> properties.contains(ax.getProperty()))
             .filter(ax -> ax.getValue().asLiteral().isPresent())
             .forEach(ax -> {
                 changes.add(new RemoveAxiom(o, ax));
                 OWLLiteral currentLiteral = ax.getValue().asLiteral().get();
                 OWLLiteral replacementLiteral = new LiteralLexicalValueReplacer(dataFactory).replaceLexicalValue(
                         currentLiteral,
                         replacement);
                 OWLAxiom replacementAx = dataFactory.getOWLAnnotationAssertionAxiom(
                         ax.getProperty(),
                         ax.getSubject(),
                         replacementLiteral,
                         ax.getAnnotations());
                 changes.add(new AddAxiom(o, replacementAx));
             });
        });
    }

    private OWLOntology getHomeOntology() {
        return homeOntologySupplier.getHomeOntology(info.getEntityToDeprecate(), ontologies);
    }


}
