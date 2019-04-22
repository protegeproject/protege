package org.protege.editor.owl.model.obofoundry;

import org.protege.editor.owl.ui.renderer.layout.Link;
import org.semanticweb.owlapi.model.IRI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URI;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 2019-04-22
 */
public class OboFoundryLink extends Link {

    private static final Logger logger = LoggerFactory.getLogger(OboFoundryLink.class);

    private final IRI termIri;

    private final OboFoundryEntry entry;

    public OboFoundryLink(@Nonnull String oboId,
                          @Nonnull IRI termIri,
                          @Nonnull OboFoundryEntry entry) {
        super(format(oboId, entry));
        this.termIri = checkNotNull(termIri);
        this.entry = checkNotNull(entry);
    }

    private static String format(String oboId, OboFoundryEntry entry) {
        if(entry.isObsolete()) {
            return oboId + " (obsolete)";
        }
        else {
            return oboId;
        }
    }

    @Override
    public void activate(Component component,
                         MouseEvent event) {
        if(event.isPopupTrigger()) {
           return;
        }
        URI termUri = termIri.toURI();
        try {
            Desktop.getDesktop().browse(termUri);
        }
        catch (IOException e) {
            logger.error("An error occurred whilst trying to activate an http link.  Link: {}", termUri, e.getMessage(), e);
        }
    }

    @Override
    public boolean isRollOverLink() {
        return false;
    }
}
