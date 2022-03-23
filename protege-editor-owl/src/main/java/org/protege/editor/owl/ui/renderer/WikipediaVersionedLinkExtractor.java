package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class WikipediaVersionedLinkExtractor {

    private static final Pattern WIKI_VERSIONED_PATTERN = Pattern.compile("WikipediaVersioned:([^\\s]+)", Pattern.CASE_INSENSITIVE);

    public static final String WIKI_VERSIONED_URL_BASE = "https://wikipedia.org/wiki/index.php?title=";

    public static final String WIKI_VERSIONED_REPLACEMENT = WIKI_VERSIONED_URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("WikipediaVersioned", WIKI_VERSIONED_PATTERN, WIKI_VERSIONED_REPLACEMENT);
    }
}
