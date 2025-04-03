package org.protege.editor.owl.model.bioregistry;

import java.net.URI;
import java.util.Optional;

import org.protege.editor.owl.ui.renderer.LinkExtractor;
import org.protege.editor.owl.ui.renderer.layout.HTTPLink;
import org.protege.editor.owl.ui.renderer.layout.Link;

/**
 * Author: Damien Goutte-Gattat<br>
 * University of Cambridge<br>
 * FlyBase Group<br>
 * Date: 03/04/2025
 */
public class BioregistryLinkExtractor implements LinkExtractor {

    public static BioregistryLinkExtractor createExtractor() {
        return new BioregistryLinkExtractor();
    }

    @Override
    public Optional<Link> extractLink(String s) {
        if ( s!= null) {
            String uri = Bioregistry.getInstance().resolve(s);
            if (uri != null) {
                return Optional.of(new HTTPLink(URI.create(uri)));
            }
        }
        return Optional.empty();
    }
}
