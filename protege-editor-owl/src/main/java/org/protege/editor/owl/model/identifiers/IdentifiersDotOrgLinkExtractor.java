package org.protege.editor.owl.model.identifiers;

import org.protege.editor.owl.ui.renderer.LinkExtractor;
import org.protege.editor.owl.ui.renderer.layout.HTTPLink;
import org.protege.editor.owl.ui.renderer.layout.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-16
 */
public class IdentifiersDotOrgLinkExtractor implements LinkExtractor {

    private static final Logger logger = LoggerFactory.getLogger(IdentifiersDotOrgLinkExtractor.class);

    public static IdentifiersDotOrgLinkExtractor createExtractor() {
        return new IdentifiersDotOrgLinkExtractor();
    }

    @Override
    public Optional<Link> extractLink(String s) {
        IdentifiersDotOrg identifiersDotOrg = IdentifiersDotOrgManager.get();
        return identifiersDotOrg.getCollection(s)
                .flatMap(collection -> {
                    try {
                        return Optional.of(new HTTPLink(new URI("http://identifiers.org/" + s)));
                    } catch (URISyntaxException e) {
                        logger.warn("Link extractor (Identifiers.org) returned invalid URI: {}", e.getMessage());
                        return Optional.empty();
                    }
                });
    }
}
