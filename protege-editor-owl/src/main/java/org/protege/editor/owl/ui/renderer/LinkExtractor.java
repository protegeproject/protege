package org.protege.editor.owl.ui.renderer;

import java.util.Optional;

import org.protege.editor.owl.ui.renderer.layout.Link;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public interface LinkExtractor {

    Optional<Link> extractLink(String s);
}
