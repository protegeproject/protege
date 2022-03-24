package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class OMIMPSLinkExtractor {

    public static final Pattern OMIMPS_ID_PATTERN = Pattern.compile("OMIMPS:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public static final String OMIMPS_URL_BASE = "https://www.omim.org/phenotypicSeries/";

    public static final String replacementString = OMIMPS_URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("OMIMPS", OMIMPS_ID_PATTERN, replacementString);
    }
}
