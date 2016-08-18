package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class WikipediaLinkExtractor {

    private static final Pattern PATTERN = Pattern.compile("Wikipedia:([^\\s]+)", Pattern.CASE_INSENSITIVE);

    public static final String URL_BASE = "https://wikipedia.org/wiki/";

    public static final String REPLACEMENT = URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("Wikipedia", PATTERN, REPLACEMENT);
    }
}
