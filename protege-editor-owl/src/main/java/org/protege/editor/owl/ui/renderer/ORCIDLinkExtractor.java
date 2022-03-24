package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class ORCIDLinkExtractor {

    public static final Pattern ORCID_ID_PATTERN = Pattern.compile("ORCID:\\s*([^\\s]+)", Pattern.CASE_INSENSITIVE);

    public static final String ORCID_URL_BASE = "https://orcid.org/";

    public static final String replacementString = ORCID_URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("ORCID", ORCID_ID_PATTERN, replacementString);
    }
}
