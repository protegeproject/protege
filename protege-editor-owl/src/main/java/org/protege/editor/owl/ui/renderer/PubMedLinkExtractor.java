package org.protege.editor.owl.ui.renderer;

import org.protege.editor.owl.ui.renderer.layout.Link;
import org.protege.editor.owl.ui.renderer.layout.PMIDLink;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18 Aug 16
 */
public class PubMedLinkExtractor implements LinkExtractor {

    public static final Pattern PUB_MED_ID_PATTERN = Pattern.compile("PMID:(\\d+)", Pattern.CASE_INSENSITIVE);

    @Override
    public Optional<Link> extractLink(String s) {
        return extractPubMedId(s).map(PMIDLink::new);
    }

    /**
     * Extracts the PubMedId from the specified PubMedId string.  This is a string that starts with PMID: and then
     * contains a sequence of digits.
     * @param s The string.
     * @return The extracted Id or an empty value if the specified string is not a valid PubMedId
     */
    public Optional<Integer> extractPubMedId(String s) {
        Matcher matcher = PUB_MED_ID_PATTERN.matcher(s);
        if(!matcher.matches()) {
            return Optional.empty();
        }
        String id = matcher.group(1);
        return Optional.of(Integer.parseInt(id));
    }
}
