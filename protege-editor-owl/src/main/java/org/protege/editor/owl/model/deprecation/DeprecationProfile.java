package org.protege.editor.owl.model.deprecation;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 *
 * Represents configurable vocabulary for the purposes of Entity deprecation.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DeprecationProfile {

    @Nonnull
    private final String name;

    @Nonnull
    private final String description;

    @Nullable
    private final String activatedBy;

    private final boolean removeLogicalDefinition;

    private final boolean removeAnnotationAssertions;

    @Nullable
    private final String replacedByAnnotationPropertyIri;

    @Nullable
    private final String textualReasonAnnotationPropertyIri;

    @Nullable
    private final String alternateEntityAnnotationPropertyIri;

    @Nullable
    private final String labelPrefix;

    @Nullable
    private final String annotationValuePrefix;

    @Nonnull
    private final Set<String> preservedAnnotationAssertionPropertyIris;

    @Nullable
    private final String deprecatedClassParentIri;

    @Nullable
    private final String deprecatedObjectPropertyParentIri;

    @Nullable
    private final String deprecatedDataPropertyParentIri;

    @Nullable
    private final String deprecatedAnnotationPropertyParentIri;

    @Nullable
    private final String deprecatedIndividualParentClassIri;

    @JsonCreator
    public DeprecationProfile(@Nonnull @JsonProperty(value = "name", required = true) String name,
                              @Nonnull @JsonProperty(value = "description", required = true) String description,
                              @Nullable @JsonProperty(value = "activatedBy") String activatedBy,
                              @JsonProperty("removeLogicalDefinition") boolean removeLogicalDefinition,
                              @JsonProperty("removeAnnotationAssertions") boolean removeAnnotationAssertions,
                              @JsonProperty("replacedByAnnotationPropertyIri") String replacedByAnnotationPropertyIri,
                              @JsonProperty("textualReasonAnnotationPropertyIri") String textualReasonAnnotationPropertyIri,
                              @JsonProperty("alternateEntityAnnotationPropertyIri") String alternateEntityAnnotationPropertyIri,
                              @Nonnull @JsonProperty(value = "labelPrefix", defaultValue = "") String labelPrefix,
                              @Nonnull @JsonProperty(value = "annotationValuePrefix",
                                                     defaultValue = "") String annotationValuePrefix,
                              @Nullable @JsonProperty(value = "preservedAnnotationAssertionPropertyIris") Set<String> preservedAnnotationAssertionPropertyIris,
                              @Nullable @JsonProperty("deprecatedClassParentIri") String deprecatedClassParentIri,
                              @Nullable @JsonProperty("deprecatedObjectPropertyParentIri") String deprecatedObjectPropertyParentIri,
                              @Nullable @JsonProperty("deprecatedDataPropertyParentIri") String deprecatedDataPropertyParentIri,
                              @Nullable @JsonProperty("deprecatedAnnotationPropertyParentIri") String deprecatedAnnotationPropertyParentIri,
                              @Nullable @JsonProperty("deprecatedIndividualParentClassIri") String deprecatedIndividualParentClassIri) {
        this.name = name;
        this.description = description;
        this.activatedBy = activatedBy;
        this.removeLogicalDefinition = removeLogicalDefinition;
        this.removeAnnotationAssertions = removeAnnotationAssertions;
        this.replacedByAnnotationPropertyIri = replacedByAnnotationPropertyIri;
        this.textualReasonAnnotationPropertyIri = textualReasonAnnotationPropertyIri;
        this.alternateEntityAnnotationPropertyIri = alternateEntityAnnotationPropertyIri;
        this.labelPrefix = labelPrefix;
        this.annotationValuePrefix = annotationValuePrefix;
        this.preservedAnnotationAssertionPropertyIris = preservedAnnotationAssertionPropertyIris != null ? preservedAnnotationAssertionPropertyIris : Collections.emptySet();
        this.deprecatedClassParentIri = deprecatedClassParentIri;
        this.deprecatedObjectPropertyParentIri = deprecatedObjectPropertyParentIri;
        this.deprecatedDataPropertyParentIri = deprecatedDataPropertyParentIri;
        this.deprecatedAnnotationPropertyParentIri = deprecatedAnnotationPropertyParentIri;
        this.deprecatedIndividualParentClassIri = deprecatedIndividualParentClassIri;
    }

    /**
     * Gets the name of this strategy
     */
    @Nonnull
    public String getName() {
        return name;
    }

    /**
     * Gets a description of this profile
     */
    @Nonnull
    public String getDescription() {
        return description;
    }

    /**
     * Gets an IRI representing an ontology IRI that indicates this profile should be active by default
     * for the specified ontology IRI.
     */
    @Nonnull
    public Optional<IRI> getActivatedBy() {
        return Optional.ofNullable(activatedBy).map(IRI::create);
    }

    /**
     * Determines if the logical definition of the entity that is being deprecated should be removed.
     * @return true if the logical definition should be removed, otherwise false.
     */
    public boolean shouldRemoveLogicalDefinition() {
        return removeLogicalDefinition;
    }

    /**
     * Determines if annotation assertions that are along properties other than rdfs:label and annotation properties
     * that are returned by the {@link #getPreservedAnnotationValuePropertiesIris()} method should be removed.
     * @return true if annotation assertions should be removed, otherwise false.
     */
    public boolean shouldRemoveAnnotationAssertions() {
        return removeAnnotationAssertions;
    }

    /**
     * Specifies an annotation property that will be used to create an annotation assertion that will point to
     * a replacement entity.  In the GO workflow this is the property "term replaced by", which has the IRI
     * {@code http://purl.obolibrary.org/obo/IAO_0100001}.
     */
    @Nonnull
    public Optional<IRI> getReplacedByAnnotationPropertyIri() {
        return expandIri(replacedByAnnotationPropertyIri);
    }

    /**
     * Gets the annotation property IRI that is used to specify the textual reason for the deprecation.  In the GO workflow
     * this is rdfs:comment.  In some schemes this isn't used.
     */
    @Nonnull
    public Optional<IRI> getDeprecationTextualReasonAnnotationPropertyIri() {
        return expandIri(textualReasonAnnotationPropertyIri);
    }

    /**
     * Gets the annotation property IRI that is used to annotated the deprecated entity with pointers to alternate
     * entities if the entity was NOT replaced by another entity.
     * Alternate entities are entities that should be used in place of the deprecated entity by consumers
     * of the ontology (e.g. biocurators).  In the GO workflow this is the property "consider", which has the IRI
     * {@code http://www.geneontology.org/formats/oboInOwl#consider}
     */
    @Nonnull
    public Optional<IRI> getAlternateEntityAnnotationPropertyIri() {
        return expandIri(alternateEntityAnnotationPropertyIri);
    }

    /**
     * Gets a string that represents a prefix that will be placed in front of the
     * rdfs:label annotation value of the deprecated entity.  In the GO workflow this prefix is "obsolete".
     * This value may be the empty string.
     */
    @Nonnull
    public String getDeprecatedEntityLabelPrefix() {
        return labelPrefix != null ? labelPrefix : "";
    }

    /**
     * Gets a string that represents a prefix that will be placed in front of annotation values that annotate
     * the deprecated entity.  This value may be the empty string.
     */
    @Nonnull
    public String getPreservedAnnotationValuePrefix() {
        return annotationValuePrefix != null ? annotationValuePrefix : "";
    }

    /**
     * A set of annotation properties IRIs (other than rdfs:label, which has special treatment) that identify
     * annotation assertions that should be preserved on the deprecated entity.  Note that this works in tandem
     * with the value returned by {@link #getPreservedAnnotationValuePrefix()}
     */
    @Nonnull
    public Set<IRI> getPreservedAnnotationValuePropertiesIris() {
        return preservedAnnotationAssertionPropertyIris.stream()
                                                       .map(DeprecationProfile::expandIri)
                                                       .filter(Optional::isPresent)
                                                       .map(Optional::get)
                                                       .collect(Collectors.toSet());
    }

    /**
     * An optional parent class for the deprecated classes.  If present, deprecated classes will be made subclasses
     * of this class.
     */
    @Nonnull
    public Optional<IRI> getDeprecatedClassParentIri() {
        return expandIri(deprecatedClassParentIri);
    }

    /**
     * An optional parent object property IRI for the deprecated object properties.
     * If present, deprecated object properties will be made sub properties of this property.
     */
    @Nonnull
    public Optional<IRI> getDeprecatedObjectPropertyParentIri() {
        return expandIri(deprecatedObjectPropertyParentIri);
    }

    /**
     * An optional parent data property IRI for the deprecated data properties.
     * If present, deprecated data properties will be made sub properties of this property.
     */
    @Nonnull
    public Optional<IRI> getDeprecatedDataPropertyParentIri() {
        return expandIri(deprecatedDataPropertyParentIri);
    }

    /**
     * An optional parent annotation property IRI for the deprecated annotation properties.
     * If present, deprecated annotation properties will be made sub properties of this property.
     */
    @Nonnull
    public Optional<IRI> getDeprecatedAnnotationPropertyParentIri() {
        return expandIri(deprecatedAnnotationPropertyParentIri);
    }

    /**
     * An optional class IRI that will be used as a type for deprecated individuals.
     * If present, deprecated individuals will be made instances of this class.
     */
    @Nonnull
    public Optional<IRI> getDeprecatedIndividualParentClassIri() {
        return expandIri(deprecatedIndividualParentClassIri);
    }


    private static Optional<IRI> expandIri(@Nullable String iri) {
        return IRIExpander.expand(iri);
    }
}
