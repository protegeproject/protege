package org.protege.editor.owl.ui.renderer.layout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

/**
 * Author: Matthew Horridge<br>
 * Stanford University<br>
 * Bio-Medical Informatics Research Group<br>
 * Date: 01/11/2011
 * <p>
 *     Represents a linit that is a URL with an HTTP address.  When the link is clicked the default web browser is
 *     activated to display the document at the link.
 * </p>
 * <p>
 * <b>This class is subject to change and it is therefore not recommended for public use</b>.
 * </p>
 */
public class HTTPLink extends Link {

    private final Logger logger = LoggerFactory.getLogger(HTTPLink.class);

    private URI linkURI;

    public HTTPLink(URI linkURI) {
        super(linkURI.toString());
        this.linkURI = linkURI;
    }

    public URI getLinkURI() {
        return linkURI;
    }

    @Override
    public boolean isRollOverLink() {
        return false;
    }

    @Override
    public void activate(Component component, MouseEvent event) {
        if (!event.isPopupTrigger()) {
            try {
                Desktop.getDesktop().browse(linkURI);
            }
            catch (IOException e) {
                logger.error("An error occurred whilst trying to activate an http link.  Link: {}", linkURI, e.getMessage(), e);
            }
        }
    }

}
