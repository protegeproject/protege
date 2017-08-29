package org.protege.editor.owl.model.deprecation;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.Optional;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 *
 * Represents configurable vocabulary for the purposes of Entity deprecation.
 */
public interface DeprecationProfile {

    /**
     * Gets the name of this strategy
     */
    @Nonnull
    String getName();

    /**
     * Determines if the logical definition of the entity that is being deprecated should be removed.
     * @return true if the logical definition should be removed, otherwise false.
     */
    boolean shouldRemoveLogicalDefinition();

    /**
     * Determines if annotation assertions that are along properties other than rdfs:label and annotation properties
     * that are returned by the {@link #getPreservedAnnotationValuePropertiesIris()} method should be removed.
     * @return true if annotation assertions should be removed, otherwise false.
     */
    boolean shouldRemoveAnnotationAssertions();

    /**
     * Specifies an annotation property that will be used to create an annotation assertion that will point to
     * a replacement entity.  In the GO workflow this is the property "term replaced by", which has the IRI
     * {@code http://purl.obolibrary.org/obo/IAO_0100001}.
     */
    @Nonnull
    IRI getReplacedByAnnotationPropertyIri();

    /**
     * Gets the annotation property IRI that is used to specify the reason for the deprecation.  In the GO workflow
     * this is rdfs:comment.
     */
    @Nonnull
    IRI getDeprecationReasonAnnotationPropertyIri();

    /**
     * Gets the annotation property IRI that is used to annotated the deprecated entity with pointers to alternate
     * entities if the entity was NOT replaced by another entity.
     * Alternate entities are entities that should be used in place of the deprecated entity by consumers
     * of the ontology (e.g. biocurators).  In the GO workflow this is the property "consider", which has the IRI
     * {@code http://www.geneontology.org/formats/oboInOwl#consider}
     */
    @Nonnull
    Optional<IRI> getAlternateEntityAnnotationPropertyIri();

    /**
     * Gets a string that represents a prefix that will be placed in front of the
     * rdfs:label annotation value of the deprecated entity.  In the GO workflow this prefix is "obsolete".
     * This value may be the empty string.
     */
    @Nonnull
    String getDeprecatedEntityLabelPrefix();

    /**
     * Gets a string that represents a prefix that will be placed in front of annotation values that annotate
     * the deprecated entity.  This value may be the empty string.
     */
    @Nonnull
    String getPreservedAnnotationValuePrefix();

    /**
     * A set of annotation properties IRIs (other than rdfs:label, which has special treatment) that identify
     * annotation assertions that should be preserved on the deprecated entity.  Note that this works in tandem
     * with the value returned by {@link #getPreservedAnnotationValuePrefix()}
     */
    @Nonnull
    Set<IRI> getPreservedAnnotationValuePropertiesIris();

    /**
     * An optional parent class for the deprecated classes.  If present, deprecated classes will be made subclasses
     * of this class.
     */
    @Nonnull
    Optional<IRI> getDeprecatedClassParentIri();

    /**
     * An optional parent object property IRI for the deprecated object properties.
     * If present, deprecated object properties will be made sub properties of this property.
     */
    @Nonnull
    Optional<IRI> getDeprecatedObjectPropertyParentIri();

    /**
     * An optional parent data property IRI for the deprecated data properties.
     * If present, deprecated data properties will be made sub properties of this property.
     */
    @Nonnull
    Optional<IRI> getDeprecatedDataPropertyParentIri();

    /**
     * An optional parent annotation property IRI for the deprecated annotation properties.
     * If present, deprecated annotation properties will be made sub properties of this property.
     */
    @Nonnull
    Optional<IRI> getDeprecatedAnnotationPropertyParentIri();

    /**
     * An optional class IRI that will be used as a type for deprecated individuals.
     * If present, deprecated individuals will be made instances of this class.
     */
    @Nonnull
    Optional<IRI> getDeprecatedIndividualParentClassIri();

}
