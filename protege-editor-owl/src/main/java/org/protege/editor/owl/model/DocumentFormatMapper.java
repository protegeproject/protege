package org.protege.editor.owl.model;

import org.semanticweb.owlapi.formats.RioTurtleDocumentFormat;
import org.semanticweb.owlapi.formats.TurtleDocumentFormat;
import org.semanticweb.owlapi.model.OWLDocumentFormat;

import javax.annotation.Nonnull;

import static org.semanticweb.owlapi.util.OWLAPIPreconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 8 Feb 2017
 */
public class DocumentFormatMapper {

    @Nonnull
    public OWLDocumentFormat mapFormat(@Nonnull OWLDocumentFormat format) {
        checkNotNull(format);
        if(format instanceof RioTurtleDocumentFormat) {
            RioTurtleDocumentFormat rioTurtleDocumentFormat = (RioTurtleDocumentFormat) format;
            TurtleDocumentFormat turtleDocumentFormat = new TurtleDocumentFormat();
            turtleDocumentFormat.copyPrefixesFrom(rioTurtleDocumentFormat);
            return turtleDocumentFormat;
        }
        return format;
    }
}
