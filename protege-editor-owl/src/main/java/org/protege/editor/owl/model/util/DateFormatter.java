package org.protege.editor.owl.model.util;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;

import java.util.Date;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public interface DateFormatter {

    OWLLiteral formatDate(Date date, OWLDataFactory dataFactory);
}
