package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class OrphanetLinkExtractor {

    public static final Pattern ORPHANET_ID_PATTERN = Pattern.compile("Orphanet:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public static final String ORPHANET_URL_BASE = "https://www.orpha.net/consor/www/cgi-bin/OC_Exp.php?Expert=";

    public static final String replacementString = ORPHANET_URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("Orphanet", ORPHANET_ID_PATTERN, replacementString);
    }
}
