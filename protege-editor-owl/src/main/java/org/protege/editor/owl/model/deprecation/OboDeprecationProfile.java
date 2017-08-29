package org.protege.editor.owl.model.deprecation;

import org.semanticweb.owlapi.model.*;
import org.semanticweb.owlapi.vocab.OWLRDFVocabulary;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 23 Aug 2017
 */
public class OboDeprecationProfile implements DeprecationProfile {

    private static final String LABEL_PREFIX = "obsolete";

    private static final String DEFINITION_PREFIX = "OBSOLETE";

    private static final IRI TERM_REPLACED_BY_IRI = IRI.create("http://purl.obolibrary.org/obo/IAO_0100001");

    private static final IRI CONSIDER_IRI = IRI.create("http://www.geneontology.org/formats/oboInOwl#consider");

    private static final IRI DEFINITION_PROPERTY_IRI = IRI.create("http://purl.obolibrary.org/obo/IAO_0000115");

    public OboDeprecationProfile() {
    }

    @Nonnull
    @Override
    public String getName() {
        return "OBO (Gene Ontology)";
    }

    @Nonnull
    @Override
    public IRI getReplacedByAnnotationPropertyIri() {
        return TERM_REPLACED_BY_IRI;
    }

    @Nonnull
    @Override
    public IRI getDeprecationReasonAnnotationPropertyIri() {
        return OWLRDFVocabulary.OWL_DEPRECATED.getIRI();
    }

    @Nonnull
    @Override
    public Optional<IRI> getAlternateEntityAnnotationPropertyIri() {
        return Optional.of(CONSIDER_IRI);
    }

    @Nonnull
    @Override
    public String getDeprecatedEntityLabelPrefix() {
        return LABEL_PREFIX;
    }

    @Nonnull
    @Override
    public String getPreservedAnnotationValuePrefix() {
        return DEFINITION_PREFIX;
    }

    @Nonnull
    @Override
    public Set<IRI> getPreservedAnnotationValuePropertiesIris() {
        return Collections.singleton(DEFINITION_PROPERTY_IRI);
    }

    @Override
    public boolean shouldRemoveLogicalDefinition() {
        return true;
    }

    @Override
    public boolean shouldRemoveAnnotationAssertions() {
        return true;
    }

    @Nonnull
    @Override
    public Optional<IRI> getDeprecatedClassParentIri() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IRI> getDeprecatedObjectPropertyParentIri() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IRI> getDeprecatedDataPropertyParentIri() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IRI> getDeprecatedAnnotationPropertyParentIri() {
        return Optional.empty();
    }

    @Nonnull
    @Override
    public Optional<IRI> getDeprecatedIndividualParentClassIri() {
        return Optional.empty();
    }
}
