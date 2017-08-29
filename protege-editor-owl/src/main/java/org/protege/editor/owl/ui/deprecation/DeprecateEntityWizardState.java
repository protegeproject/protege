package org.protege.editor.owl.ui.deprecation;

import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Aug 2017
 */
public class DeprecateEntityWizardState {

    @Nonnull
    private String reasonForDeprecation = "";

    @Nullable
    private OWLEntity replacementEntity;

    private final List<OWLEntity> alternateEntities = new ArrayList<>();

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
