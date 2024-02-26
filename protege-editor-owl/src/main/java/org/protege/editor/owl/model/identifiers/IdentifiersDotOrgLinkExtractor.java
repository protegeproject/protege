package org.protege.editor.owl.model.identifiers;

import java.net.URI;
import java.util.Optional;

import org.protege.editor.owl.ui.renderer.LinkExtractor;
import org.protege.editor.owl.ui.renderer.layout.HTTPLink;
import org.protege.editor.owl.ui.renderer.layout.Link;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-16
 */
public class IdentifiersDotOrgLinkExtractor implements LinkExtractor {

    public static IdentifiersDotOrgLinkExtractor createExtractor() {
        return new IdentifiersDotOrgLinkExtractor();
    }

    @Override
    public Optional<Link> extractLink(String s) {
        IdentifiersDotOrg identifiersDotOrg = IdentifiersDotOrgManager.get();
        return identifiersDotOrg.getCollection(s)
                .map(collection -> new HTTPLink(URI.create("http://identifiers.org/" + s)));
    }
}
