package org.protege.editor.owl.model.merge;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.protege.editor.owl.model.util.OboUtilities;
import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.model.parameters.Imports;
import org.semanticweb.owlapi.util.OWLEntityRenamer;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.obolibrary.obo2owl.Obo2OWLConstants.Obo2OWLVocabulary.IRI_OIO_hasExactSynonym;
import static org.semanticweb.owlapi.vocab.SKOSVocabulary.ALTLABEL;
import static org.semanticweb.owlapi.vocab.SKOSVocabulary.PREFLABEL;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-03-19
 */
public class MergeEntitiesChangeListGenerator {

    @Nonnull
    private final OWLOntology rootOntology;

    @Nonnull
    private final OWLDataFactory dataFactory;

    @Nonnull
    private final ImmutableSet<OWLEntity> sourceEntities;

    @Nonnull
    private final OWLEntity targetEntity;


    public MergeEntitiesChangeListGenerator(@Nonnull OWLOntology rootOntology,
                                            @Nonnull OWLDataFactory dataFactory,
                                            @Nonnull ImmutableSet<OWLEntity> sourceEntities,
                                            @Nonnull OWLEntity targetEntity) {
        this.rootOntology = checkNotNull(rootOntology);
        this.dataFactory = checkNotNull(dataFactory);
        this.sourceEntities = checkNotNull(sourceEntities);
        this.targetEntity = checkNotNull(targetEntity);
    }

    public List<OWLOntologyChange> generateChanges() {

        // Generate changes to perform a merge.  The order of the generation of these changes
        // is important.  Usage changes must be generated first.
        ImmutableList.Builder<OWLOntologyChange> builder = ImmutableList.builder();

        // Generate changes to replace usage of the entity.  This will essentially merge the
        // entity into the target entity
        replaceUsage(builder);

        // Avoid conflicts with labels.  The merged term must not duplicate preferred labels for
        // a given language.
        replaceLabels(builder);

        // Deprecated, if necessary
//        deprecateSourceEntities(builder);

        return builder.build();

    }

    private void deprecateSourceEntities(ImmutableList.Builder<OWLOntologyChange> builder) {
        sourceEntities.forEach(sourceEntity -> {
            // Add an annotation assertion to deprecate the source entity
            OWLAnnotationAssertionAxiom depAx = dataFactory.getDeprecatedOWLAnnotationAssertionAxiom(sourceEntity.getIRI());
            builder.add(new AddAxiom(rootOntology, depAx));

            // Preserve labels and other annotations on the source entity
            ontologyStream(rootOntology, Imports.INCLUDED)
                    .forEach(ont -> ont.getAnnotationAssertionAxioms(sourceEntity.getIRI())
                            .forEach(ax -> builder.add(new AddAxiom(ont, ax))));
        });
    }

    private void replaceUsage(ImmutableList.Builder<OWLOntologyChange> builder) {
        sourceEntities.forEach(sourceEntity -> {
            OWLEntityRenamer renamer = new OWLEntityRenamer(rootOntology.getOWLOntologyManager(),
                                                            rootOntology.getImportsClosure());
            List<OWLOntologyChange> renameChanges = renamer.changeIRI(sourceEntity.getIRI(), targetEntity.getIRI());
            builder.addAll(renameChanges);
        });
    }

    private void replaceLabels(@Nonnull ImmutableList.Builder<OWLOntologyChange> builder) {
        // Replace rdfs:label with skos:altLabel.
        // Replace skos:prefLabel with skos:altLabel.
        // In both cases, language tags are preserved.
        ontologyStream(rootOntology, Imports.INCLUDED)
                .forEach(ont -> {
                    sourceEntities.forEach(sourceEntity -> {
                        // Get the annotation assertions that were originally on the source entity
                        ont.getAnnotationAssertionAxioms(sourceEntity.getIRI()).stream()
                                // Just deal with explicit rdfs:label and skos:prefLabel annotations
                                .filter(ax -> isRdfsLabelAnnotation(ax) || isSkosPrefLabelAnnotation(ax))
                                // Replace on the target entity with skos:altLabel as the property
                                .forEach(ax -> replaceWithLabelReplacement(ax, ont, builder));
                    });
                });
    }

    /**
     * Replaces the specified annotation assertion on the target entity with an annotation assertion whose
     * property is the replacement label property.
     *
     * @param ax        The annotation assertion under consideration.  This is the original annotation
     *                  assertion on the source entity (not the target entity).
     * @param ont        The ontology to make the changes in.
     * @param builder    The builder for adding changes to.
     */
    private void replaceWithLabelReplacement(@Nonnull OWLAnnotationAssertionAxiom ax,
                                             @Nonnull OWLOntology ont,
                                             @Nonnull ImmutableList.Builder<OWLOntologyChange> builder) {
        // Remove the original one (that will be on the target entity by now)
        OWLAxiom origAx = dataFactory.getOWLAnnotationAssertionAxiom(
                targetEntity.getIRI(),
                ax.getAnnotation(),
                ax.getAnnotations());
        builder.add(new RemoveAxiom(ont, origAx));

        // Generate a new annotation with a property of the label replacement property.
        // Preserve any annotations on the annotation.
        OWLAnnotation replAnno = dataFactory.getOWLAnnotation(getLabelReplacementProperty(),
                                                              ax.getAnnotation().getValue(),
                                                              ax.getAnnotation().getAnnotations());
        // Generate a new annotation assertion to replace the original one.
        // Preserve any annotations on the axiom.
        OWLAxiom replAx = dataFactory.getOWLAnnotationAssertionAxiom(
                targetEntity.getIRI(),
                replAnno,
                ax.getAnnotations());
        builder.add(new AddAxiom(ont, replAx));
    }

    /**
     * Determines if the given annotation assertion axiom provides an rdfs:label
     */
    private boolean isRdfsLabelAnnotation(@Nonnull OWLAnnotationAssertionAxiom ax) {
        return ax.getProperty().isLabel();
    }

    /**
     * Determines if the given annotation assertion axiom provides a skos:prefLabel
     */
    private boolean isSkosPrefLabelAnnotation(@Nonnull OWLAnnotationAssertionAxiom ax) {
        return PREFLABEL.getIRI().equals(ax.getProperty().getIRI());
    }

    /**
     * Gets the label replacement property.  This varies depending upon whether the
     * target entity has an OBO Id or not.
     */
    @Nonnull
    private OWLAnnotationProperty getLabelReplacementProperty() {
        // For OBO IRIs return the exact synonym property
        if(OboUtilities.isOboIri(targetEntity.getIRI())) {
            return dataFactory.getOWLAnnotationProperty(IRI_OIO_hasExactSynonym.getIRI());
        }
        else {
            // For non-OBO IRIs return skos:altLabel
            return dataFactory.getOWLAnnotationProperty(ALTLABEL.getIRI());
        }
    }


    private static Stream<OWLOntology> ontologyStream(OWLOntology ontology,
                                                      Imports imports) {
        if(imports == Imports.INCLUDED) {
            return ontology.getImportsClosure().stream();
        }
        else {
            return Stream.of(ontology);
        }
    }
}
