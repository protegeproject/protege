package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class DOILinkExtractor {

    public static final Pattern DOI_ID_PATTERN = Pattern.compile("DOI:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public static final String DOI_URL_BASE = "https://doi.org/";

    public static final String replacementString = DOI_URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("DOI", DOI_ID_PATTERN, replacementString);
    }
}
