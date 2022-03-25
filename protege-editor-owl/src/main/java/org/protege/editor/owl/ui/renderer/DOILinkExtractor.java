package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * A RegEx based link extractor for the 'DOI:' prefix. 
 * 25 Mar 2022
 */
public class DOILinkExtractor {

    public static final Pattern DOI_ID_PATTERN = Pattern.compile("DOI:\\s*([^\\s]+)", Pattern.CASE_INSENSITIVE);

    public static final String DOI_URL_BASE = "https://doi.org/";

    public static final String replacementString = DOI_URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("DOI", DOI_ID_PATTERN, replacementString);
    }
}
