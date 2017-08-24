package org.protege.editor.owl.model.deprecation;

import org.semanticweb.owlapi.model.*;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class OboDeprecatedEntityStrategy implements DeprecatedEntityStrategy {

    private static final String LABEL_PREFIX = "obsolete";

    private static final String DEFINITION_PREFIX = "OBSOLETE";

    private static final IRI TERM_REPLACED_BY_IRI = IRI.create("http://purl.obolibrary.org/obo/IAO_0100001");

    private static final IRI CONSIDER_IRI = IRI.create("http://www.geneontology.org/formats/oboInOwl#consider");

    private static final IRI DEFINITION_PROPERTY_IRI = IRI.create("http://purl.obolibrary.org/obo/IAO_0000115");

    @Nonnull
    private final OWLDataFactory dataFactory;


    public OboDeprecatedEntityStrategy(@Nonnull OWLDataFactory dataFactory) {
        this.dataFactory = dataFactory;
    }

    @Nonnull
    @Override
    public OWLAnnotationProperty getReplacedByAnnotationProperty() {
        return dataFactory.getOWLAnnotationProperty(TERM_REPLACED_BY_IRI);
    }

    @Nonnull
    @Override
    public OWLAnnotationProperty getDeprecationReasonAnnotationProperty() {
        return dataFactory.getRDFSComment();
    }

    @Nonnull
    @Override
    public OWLAnnotationProperty getAlternateEntityAnnotationProperty() {
        return dataFactory.getOWLAnnotationProperty(CONSIDER_IRI);
    }

    @Nonnull
    @Override
    public String getDeprecatedEntityLabelPrefix() {
        return LABEL_PREFIX;
    }

    @Nonnull
    @Override
    public String getDeprecatedEntityAnnotationValuePrefix() {
        return DEFINITION_PREFIX;
    }

    @Nonnull
    @Override
    public Set<OWLAnnotationProperty> getPreservedAnnotationProperties() {
        return Collections.singleton(dataFactory.getOWLAnnotationProperty(DEFINITION_PROPERTY_IRI));
    }

    @Override
    public boolean shouldRemoveLogicalDefinition() {
        return true;
    }

    @Override
    public boolean shouldRemoveDanglingReferences() {
        return false;
    }

    @Override
    public boolean shouldRemoveAnnotationAssertions() {
        return false;
    }

    @Nonnull
    @Override
    public Optional<OWLClass> getDeprecatedClassParent() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<OWLObjectProperty> getDeprecatedObjectPropertyParent() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<OWLDataProperty> getDeprecatedDataPropertyParent() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<OWLAnnotationProperty> getDeprecatedAnnotationPropertyParent() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<OWLClass> getDeprecatedIndividualParent() {
        return Optional.empty();
    }
}
