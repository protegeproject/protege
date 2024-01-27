package org.protege.editor.owl.ui.deprecation;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.protege.editor.owl.model.deprecation.DeprecationProfile;
import org.semanticweb.owlapi.model.OWLAnnotationValue;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Aug 2017
 */
public class DeprecateEntityWizardState {

    private DeprecationProfile deprecationProfile;

    @Nonnull
    private String reasonForDeprecation = "";

    @Nullable
    private OWLAnnotationValue deprecationCode;

    @Nullable
    private OWLEntity replacementEntity;

    private final List<OWLEntity> alternateEntities = new ArrayList<>();

    public void setDeprecationProfile(@Nonnull DeprecationProfile deprecationProfile) {
        this.deprecationProfile = checkNotNull(deprecationProfile);
    }

    @Nonnull
    public Optional<DeprecationProfile> getDeprecationProfile() {
        return Optional.ofNullable(deprecationProfile);
    }

    @Nonnull
    public Optional<OWLAnnotationValue> getDeprecationCode() {
        return Optional.ofNullable(deprecationCode);
    }

    public void setDeprecationCode(@Nullable OWLAnnotationValue deprecationCode) {
        this.deprecationCode = deprecationCode;
    }

    @Nonnull
    public String getReasonForDeprecation() {
        return reasonForDeprecation;
    }

    public void setReasonForDeprecation(@Nonnull String reasonForDeprecation) {
        this.reasonForDeprecation = reasonForDeprecation;
    }

    public Optional<OWLEntity> getReplacementEntity() {
        return Optional.ofNullable(replacementEntity);
    }

    public void setReplacementEntity(@Nullable OWLEntity replacementEntity) {
        this.replacementEntity = replacementEntity;
    }

    public void setAlternateEntities(List<OWLEntity> alternateEntities) {
        this.alternateEntities.clear();
        this.alternateEntities.addAll(alternateEntities);
    }

    public List<OWLEntity> getAlternateEntities() {
        return alternateEntities;
    }
}
