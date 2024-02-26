package org.protege.editor.owl.ui.deprecation;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.Nonnull;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.util.OboUtilities;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 24 Aug 2017
 */
public class DeprecationWizardEntityRenderer {

    @Nonnull
    private final OWLModelManager modelManager;

    public DeprecationWizardEntityRenderer(@Nonnull OWLModelManager modelManager) {
        this.modelManager = checkNotNull(modelManager);
    }

    public static DeprecationWizardEntityRenderer renderer(@Nonnull OWLModelManager modelManager) {
        return new DeprecationWizardEntityRenderer(modelManager);
    }


    public String getRendering(@Nonnull OWLEntity entity) {
        return modelManager.getRendering(entity) + getOboIdBracketedRendering(entity);
    }

    public String getHtmlRendering(@Nonnull OWLEntity entity) {
        return "<span style=\"font-weight: bold;\">"
                + modelManager.getRendering(entity)
                + "</span>"
                + getOboIdBracketedRendering(entity);
    }

    private String getOboIdBracketedRendering(@Nonnull OWLEntity entity) {
        return OboUtilities.getOboIdFromIri(entity.getIRI()).map(id -> " (" + id + ")").orElse("");
    }

}
