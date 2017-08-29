package org.protege.editor.owl.model.deprecation;

import org.protege.editor.core.log.LogBanner;
import org.protege.editor.owl.model.entity.HomeOntologySupplier;
import org.protege.editor.owl.model.util.DefinitionExtractor;
import org.protege.editor.owl.model.util.LiteralLexicalValueReplacer;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.util.AxiomSubjectProvider;
import org.semanticweb.owlapi.util.OWLObjectDuplicator;
import org.semanticweb.owlapi.vocab.OWL2Datatype;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    private static Logger logger = LoggerFactory.getLogger(EntityDeprecator.class);

    @Nonnull
    private final OWLDataFactory dataFactory;

    @Nonnull
    private final Set<OWLOntology> ontologies = new HashSet<>();

    @Nonnull
    private final DeprecateEntityInfo<E> info;

    @Nonnull
    private final DeprecationProfile profile;

    @Nonnull
    private final HomeOntologySupplier homeOntologySupplier;

    public EntityDeprecator(@Nonnull DeprecateEntityInfo<E> info,
                            @Nonnull DeprecationProfile profile,
                            @Nonnull Set<OWLOntology> ontologies,
                            @Nonnull HomeOntologySupplier homeOntologySupplier,
                            @Nonnull OWLDataFactory dataFactory) {
        this.dataFactory = checkNotNull(dataFactory);
        this.profile = checkNotNull(profile);
        this.homeOntologySupplier = checkNotNull(homeOntologySupplier);
        this.ontologies.addAll(ontologies);
        this.info = checkNotNull(info);
    }

    public List<OWLOntologyChange> getChanges() {
        logger.info(LogBanner.start("Deprecating entity"));
        logger.info("[Deprecate Entity] Deprecating " + info.getEntityToDeprecate());
        List<OWLOntologyChange> changes = new ArrayList<>();

        switchUsageOfDeprecatedEntityWithReplacement(changes);
        updateDeprecatedEntityLogicalDefinition(changes);
        updateDeprecatedEntityAnnotations(changes);

        addDeprecatedAnnotationAssertion(changes);
        addDeprecationReason(changes);
        relabelDeprecatedEntity(changes);
        prefixDeprecatedAnnotationValues(changes);

        addReplacedByAnnotation(changes);
        addAlternateEntityAnnotations(changes);

        // Re-parenting is optional and specified by the profile (the GO workflow does not do this)
        reparentDeprecatedEntity(changes);

        logger.info(LogBanner.end());
        return changes;
    }

    /**
     * Adds an annotation assertion that specifies that the entity is deprecated.
     *
     * @param changes A list of changes that the enacting change will be added to.
     */
    private void addDeprecatedAnnotationAssertion(@Nonnull List<OWLOntologyChange> changes) {
        OWLAnnotationAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                dataFactory.getOWLDeprecated(),
                info.getEntityToDeprecate().getIRI(),
                dataFactory.getOWLLiteral(true)
        );
        logger.info("[Deprecate Entity] Added owl:deprecated annotation");
        changes.add(new AddAxiom(getHomeOntology(), ax));
    }


    /**
     * Removes axioms that logically define the deprecated entity.
     *
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void updateDeprecatedEntityLogicalDefinition(@Nonnull List<OWLOntologyChange> changes) {
        if (!profile.shouldRemoveLogicalDefinition()) {
            logger.info("[Deprecate Entity] Logical definition left intact");
            return;
        }
        ontologies.forEach(o -> {
            DefinitionExtractor definitionExtractor = new DefinitionExtractor(info.getEntityToDeprecate(),
                                                                              o,
                                                                              dataFactory);
            changes.addAll(definitionExtractor.getChangesToRemoveDefinition());
        });
        logger.info("[Deprecate Entity] Removed deprecated entity logical definition");
    }

    private void updateDeprecatedEntityAnnotations(@Nonnull List<OWLOntologyChange> changes) {
        if (!profile.shouldRemoveAnnotationAssertions()) {
            logger.info("[Deprecate Entity] Annotation assertions left intact");
            return;
        }
        ontologies.forEach(o -> {
            o.getAnnotationAssertionAxioms(info.getEntityToDeprecate().getIRI()).stream()
             .filter(ax -> {
                 Set<IRI> preservedAnnotationValuePropertiesIris = profile.getPreservedAnnotationValuePropertiesIris();
                 return !preservedAnnotationValuePropertiesIris.contains(ax.getProperty().getIRI());
             })
             .forEach(ax -> changes.add(new RemoveAxiom(o, ax)));
        });
    }

    /**
     * Relabels the deprecated entity by replacing existing labels with their original values prefixed by the value
     * returned by {@link DeprecationProfile#getDeprecatedEntityLabelPrefix()}.
     *
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void relabelDeprecatedEntity(@Nonnull List<OWLOntologyChange> changes) {
        String prefix = profile.getDeprecatedEntityLabelPrefix();
        if (prefix.isEmpty()) {
            return;
        }
        String replacement = prefix.trim() + " $0";
        logger.info("[Deprecate Entity] Relabelled deprecated entity");
        replaceAnnotationAssertions(changes,
                                    singleton(dataFactory.getRDFSLabel().getIRI()),
                                    replacement);
    }

    /**
     * Replaces annotation values that annotate the deprecated entity along properties given by
     * {@link DeprecationProfile#getPreservedAnnotationValuePropertiesIris()}
     * with values that are prefixed with the String returned by
     * {@link DeprecationProfile#getPreservedAnnotationValuePrefix()}
     *
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void prefixDeprecatedAnnotationValues(@Nonnull List<OWLOntologyChange> changes) {
        String prefix = profile.getPreservedAnnotationValuePrefix().trim();
        if (prefix.isEmpty()) {
            return;
        }
        String replacement = prefix.trim() + " $0";
        logger.info("[Deprecate Entity] Prefixed deprecated entity annotations with \"{}\"", prefix);
        replaceAnnotationAssertions(changes,
                                    profile.getPreservedAnnotationValuePropertiesIris(),
                                    replacement);
    }

    /**
     * Adds an annotation that specifies a human readable reason/explanation as to why the entity is deprecated.
     *
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void addDeprecationReason(List<OWLOntologyChange> changes) {
        String reasonForDeprecation = info.getReasonForDeprecation().trim();
        if (reasonForDeprecation.isEmpty()) {
            return;
        }
        profile.getDeprecationReasonAnnotationPropertyIri().ifPresent(propIri -> {
            OWLAnnotationAssertionAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                    dataFactory.getOWLAnnotationProperty(propIri),
                    info.getEntityToDeprecate().getIRI(),
                    dataFactory.getOWLLiteral(reasonForDeprecation, OWL2Datatype.XSD_STRING)
            );
            logger.info("[Deprecate Entity] Added reason for deprecation as an annotation on the deprecated entity");
            changes.add(new AddAxiom(getHomeOntology(), ax));
        });
    }

    /**
     * Adds an annotation that specifies a replacement for the deprecated entity.
     *
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void addReplacedByAnnotation(List<OWLOntologyChange> changes) {
        info.getReplacementEntity().ifPresent(replacementEntity -> {
            profile.getReplacedByAnnotationPropertyIri().ifPresent(propIri -> {
                OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                        dataFactory.getOWLAnnotationProperty(propIri),
                        info.getEntityToDeprecate().getIRI(),
                        replacementEntity.getIRI()
                );
                logger.info("[Deprecate Entity] Added annotation to point to deprecated entity replacement");
                changes.add(new AddAxiom(getHomeOntology(), ax));
            });
        });
    }

    /**
     * Adds a set of annotations that specify alternate entities that should be used for consumers of the ontology.
     *
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void addAlternateEntityAnnotations(List<OWLOntologyChange> changes) {
        profile.getAlternateEntityAnnotationPropertyIri().ifPresent(property -> {
            info.getAlternateEntities().forEach(alternate -> {
                OWLAxiom ax = dataFactory.getOWLAnnotationAssertionAxiom(
                        dataFactory.getOWLAnnotationProperty(property),
                        info.getEntityToDeprecate().getIRI(),
                        alternate.getIRI()
                );
                logger.info("[Deprecate Entity] Added annotations to point to alternate entities");
                changes.add(new AddAxiom(getHomeOntology(), ax));
            });
        });
    }

    /**
     * Reparents the deprecated entity.
     *
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void reparentDeprecatedEntity(List<OWLOntologyChange> changes) {
        E deprecatedEntity = info.getEntityToDeprecate();
        Optional<OWLAxiom> ax = deprecatedEntity.accept(new ReparentVisitor(profile, dataFactory));
        ax.map(axiom -> new AddAxiom(getHomeOntology(), axiom)).ifPresent(changes::add);
    }


    /**
     * Replaces logical axioms that mention the deprecated entity with axioms that mention the deprecated entity
     * replacement.
     *
     * @param changes A list of changes that the enacting changes will be added to.
     */
    private void switchUsageOfDeprecatedEntityWithReplacement(@Nonnull List<OWLOntologyChange> changes) {
        info.getReplacementEntity().ifPresent(replacementEntity -> {
            Map<OWLEntity, IRI> replacementMap = new HashMap<>();
            replacementMap.put(info.getEntityToDeprecate(), replacementEntity.getIRI());
            OWLObjectDuplicator duplicator = new OWLObjectDuplicator(replacementMap, dataFactory);
            ontologies.forEach(o -> {
                o.getReferencingAxioms(info.getEntityToDeprecate()).stream()
                 // Only replace the entity in logical axioms - annotations remain on the deprecated entity
                 .filter(OWLAxiom::isLogicalAxiom)
                 // Don't replace axioms that define the entity to be deprecated
                 .filter(ax -> !new AxiomSubjectProvider().getSubject(ax).equals(info.getEntityToDeprecate()))
                 .forEach(ax -> {
                     OWLAxiom replacementAx = duplicator.duplicateObject(ax);
                     changes.add(new RemoveAxiom(o, ax));
                     changes.add(new AddAxiom(o, replacementAx));
                 });
            });
        });
    }

    private void replaceAnnotationAssertions(@Nonnull List<OWLOntologyChange> changes,
                                             @Nonnull Set<IRI> propertyIris,
                                             @Nonnull String replacement) {
        ontologies.forEach(o -> {
            IRI entityIri = info.getEntityToDeprecate().getIRI();
            o.getAnnotationAssertionAxioms(entityIri).stream()
             .filter(ax -> propertyIris.contains(ax.getProperty().getIRI()))
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
