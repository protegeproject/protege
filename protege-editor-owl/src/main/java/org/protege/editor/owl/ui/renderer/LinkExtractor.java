package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.renderer.layout.Link;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public interface LinkExtractor {

    Optional<Link> extractLink(String s);
}
