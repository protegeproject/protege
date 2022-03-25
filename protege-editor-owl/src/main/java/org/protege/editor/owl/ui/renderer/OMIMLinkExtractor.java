package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * A RegEx based link extractor for the 'OMIM:' prefix. 
 * 25 Mar 2022
 */
public class OMIMLinkExtractor {

    public static final Pattern OMIM_ID_PATTERN = Pattern.compile("OMIM:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public static final String OMIM_URL_BASE = "https://omim.org/entry/";

    public static final String replacementString = OMIM_URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("OMIM", OMIM_ID_PATTERN, replacementString);
    }
}
