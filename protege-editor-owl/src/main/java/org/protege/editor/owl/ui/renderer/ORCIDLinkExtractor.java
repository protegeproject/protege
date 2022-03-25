package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * A RegEx based link extractor for the 'ORCID:' prefix. 
 * 25 Mar 2022
 */
public class ORCIDLinkExtractor {

    public static final Pattern ORCID_ID_PATTERN = Pattern.compile("ORCID:\\s*([^\\s]+)", Pattern.CASE_INSENSITIVE);

    public static final String ORCID_URL_BASE = "https://orcid.org/";

    public static final String replacementString = ORCID_URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("ORCID", ORCID_ID_PATTERN, replacementString);
    }
}
