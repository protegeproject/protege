package org.protege.editor.owl.model.parser;

import org.semanticweb.owl.model.OWLException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 19-Jan-2007<br><br>
 */
public class OWLParseException extends OWLException {

    public OWLParseException(Throwable cause) {
        super(cause);
    }


    public OWLParseException(String message) {
        super(message);
    }


    public OWLParseException(String message, Throwable cause) {
        super(message, cause);
    }

//    abstract int getStartIndex();
//
//    abstract int getEndIndex();
//
//    abstract boolean isClassExpected();
//
//    abstract boolean isObjectPropertyExpected();
//
//    abstract boolean isDataPropertyExpected();
//
//    abstract boolean isDatatypeExpected();
//
//    abstract boolean isIndividualExpected();
//
//    abstract boolean isTypedConstantExpected();
//
//    abstract boolean isUntypedConstantExpected();
}
