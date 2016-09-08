package org.protege.editor.owl.ui.view;

import org.protege.editor.owl.OWLEditorKit;
import org.semanticweb.owlapi.model.OWLEntity;

import javax.annotation.Nonnull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 7 Sep 16
 */
public interface EntityBannerFormatter {

    /**
     * Formats a banner for the specified entity under the context of the specified editor kit.
     * @param entity The entity.
     * @param editorKit The editor kit.
     * @return A string representing the banner.  May be empty.
     */
    @Nonnull
    String formatBanner(@Nonnull OWLEntity entity, @Nonnull OWLEditorKit editorKit);
}
