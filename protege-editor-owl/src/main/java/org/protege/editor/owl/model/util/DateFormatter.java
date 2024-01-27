package org.protege.editor.owl.model.util;

import java.util.Date;

import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLLiteral;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 20/11/15
 */
public interface DateFormatter {

    OWLLiteral formatDate(Date date, OWLDataFactory dataFactory);
}
