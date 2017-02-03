package org.protege.editor.owl.ui.renderer;

import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class PubMedLinkExtractor {

    public static final Pattern PUB_MED_ID_PATTERN = Pattern.compile("PMID:\\s*(\\d+)", Pattern.CASE_INSENSITIVE);

    public static final String PUBMED_URL_BASE = "http://www.ncbi.nlm.nih.gov/pubmed/";

    public static final String replacementString = PUBMED_URL_BASE + "$1";

    public static RegExBasedLinkExtractor createExtractor() {
        return new RegExBasedLinkExtractor("PubMedId", PUB_MED_ID_PATTERN, replacementString);
    }
}
