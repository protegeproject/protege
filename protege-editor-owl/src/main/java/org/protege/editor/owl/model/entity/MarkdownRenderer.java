package org.protege.editor.owl.model.entity;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.jgit.annotations.NonNull;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.ui.renderer.RenderingEscapeUtils;
import org.semanticweb.owlapi.model.OWLEntity;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 29 Sep 2017
 */
public class MarkdownRenderer {

    @NonNull
    private final OWLModelManager modelManager;

    public MarkdownRenderer(OWLModelManager modelManager) {
        this.modelManager = checkNotNull(modelManager);
    }

    public String renderMarkdown(OWLEntity entity) {
        String displayName = modelManager.getRendering(entity);
        String unqotedDisplayName = RenderingEscapeUtils.unescape(displayName);
        return String.format("[%s](%s)", unqotedDisplayName, entity.getIRI().toString());
    }
}
