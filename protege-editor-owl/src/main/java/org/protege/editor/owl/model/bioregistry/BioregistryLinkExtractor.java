package org.protege.editor.owl.model.bioregistry;

import org.protege.editor.owl.ui.renderer.LinkExtractor;
import org.protege.editor.owl.ui.renderer.layout.HTTPLink;
import org.protege.editor.owl.ui.renderer.layout.Link;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;

/**
 * Author: Damien Goutte-Gattat<br>
 * University of Cambridge<br>
 * FlyBase Group<br>
 * Date: 03/04/2025
 */
public class BioregistryLinkExtractor implements LinkExtractor {

    private static final Logger logger = LoggerFactory.getLogger(BioregistryLinkExtractor.class);

    public static BioregistryLinkExtractor createExtractor() {
        return new BioregistryLinkExtractor();
    }

    @Override
    public Optional<Link> extractLink(String s) {
        if (s != null) {
            String uri = Bioregistry.getInstance().resolve(s);
            if (uri != null) {
                try {
                    return Optional.of(new HTTPLink(new URI(uri)));
                } catch (URISyntaxException e) {
                    logger.warn("Link extractor (Bioregistry) returned invalid URI: {}", e.getMessage());
                }
            }
        }
        return Optional.empty();
    }
}
