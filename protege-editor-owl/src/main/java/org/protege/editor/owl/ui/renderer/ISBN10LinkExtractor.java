package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class ISBN10LinkExtractor {

    private static final Pattern PATTERN = Pattern.compile("ISBN:(\\d{10})", Pattern.CASE_INSENSITIVE);

    public static final String URL_BASE = "http://www.isbnsearch.org/isbn/";

    public static final String REPLACEMENT = URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("ISBN-10", PATTERN, REPLACEMENT);
    }
}
