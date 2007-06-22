package org.protege.editor.owl.model.parser;

import org.semanticweb.owl.model.OWLException;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
