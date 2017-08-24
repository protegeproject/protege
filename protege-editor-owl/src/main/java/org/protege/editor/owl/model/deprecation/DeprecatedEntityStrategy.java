package org.protege.editor.owl.model.deprecation;

import org.semanticweb.owlapi.model.OWLAnnotationProperty;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataProperty;
import org.semanticweb.owlapi.model.OWLObjectProperty;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Optional;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 *
 * Represents configurable vocabulary for the purposes of Entity deprecation.
 */
public interface DeprecatedEntityStrategy {

    /**
     * Determines if the logical definition of the entity that is being deprecated should be removed.
     * @return true if the logical definition should be removed, otherwise false.
     */
    boolean shouldRemoveLogicalDefinition();

    /**
     * Determines if any logical axioms that reference the entity that is being deprecated should be removed.
     * If an replacement entity is specified in the deprecation process then mentions of the deprecated entity will be
     * replaced by the replacement entity.  If a replacement entity is not specified in the deprecation process then
     * this method determines whether axioms that mention the deprecated entity should be deleted.
     * @return true if axioms that mention the deprecated entity should be deleted otherwise false.
     */
    boolean shouldRemoveDanglingReferences();

    /**
     * Specifies an annotation property that will be used to create an annotation assertion that will point to
     * a replacement entity.  In the GO workflow this is the property "term replaced by", which has the IRI
     * {@code http://purl.obolibrary.org/obo/IAO_0100001}.
     */
    @Nonnull
    OWLAnnotationProperty getReplacedByAnnotationProperty();

    /**
     * Gets the annotation property that is used to specify the reason for the deprecation.  In the GO workflow
     * this is rdfs:comment
     */
    @Nonnull
    OWLAnnotationProperty getDeprecationReasonAnnotationProperty();

    /**
     * Gets the annotation property that is used to annotated the deprecated entity with pointers to alternate
     * entities.  Alternate entities are entities that should be used in place of the deprecated entity by consumers
     * of the ontology.  In the GO workflow this is the property "consider", which has the IRI
     * {@code http://www.geneontology.org/formats/oboInOwl#consider}
     */
    @Nonnull
    OWLAnnotationProperty getAlternateEntityAnnotationProperty();

    /**
     * Gets a string that represents a prefix that will be placed in front of the
     * rdfs:label annotation value of the deprecated entity.  In the GO workflow this prefix is "obsolete"
     */
    @Nonnull
    String getDeprecatedEntityLabelPrefix();

    /**
     * Gets a string that represents a prefix that will be placed in front of annotation values that annotate
     * the deprecated entity
     */
    @Nonnull
    String getDeprecatedEntityAnnotationValuePrefix();

    /**
     * A set of annotation properties other than rdfs:label that identify annotation assertions that should be
     * preserved on the deprecated entity and whose values should be prefixed with a string indicating that the
     * entity has been deprecated.
     */
    @Nonnull
    Set<OWLAnnotationProperty> getPreservedAnnotationProperties();

    /**
     * Determines if annotation assertions that are along properties other than rdfs:label and annotation properties
     * that are returned by the {@link #getPreservedAnnotationProperties()} method should be removed.
     * @return true if annotation assertions should be removed, otherwise false.
     */
    boolean shouldRemoveAnnotationAssertions();


    /**
     * An optional parent class for the deprecated classes.  If present, deprecated classes will be made subclasses
     * of this class.
     */
    @Nonnull
    Optional<OWLClass> getDeprecatedClassParent();

    /**
     * An optional parent object property for the deprecated object properties.
     * If present, deprecated object properties will be made sub properties of this property.
     */
    @Nonnull
    Optional<OWLObjectProperty> getDeprecatedObjectPropertyParent();

    /**
     * An optional parent data property for the deprecated data properties.
     * If present, deprecated data properties will be made sub properties of this property.
     */
    @Nonnull
    Optional<OWLDataProperty> getDeprecatedDataPropertyParent();

    /**
     * An optional parent annotation property for the deprecated annotation properties.
     * If present, deprecated annotation properties will be made sub properties of this property.
     */
    @Nonnull
    Optional<OWLAnnotationProperty> getDeprecatedAnnotationPropertyParent();

    /**
     * An optional class that will be used as a type for deprecated individuals.
     * If present, deprecated individuals will be made instances of this class.
     */
    @Nonnull
    Optional<OWLClass> getDeprecatedIndividualParent();

}
