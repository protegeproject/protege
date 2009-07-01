package org.protege.editor.owl.model.parser;

import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owlapi.expression.ParserException;

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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: May 8, 2008<br><br>
 */
public class ParserUtil {

    public static OWLExpressionParserException convertException(ParserException ex) {
        int endPos = ex.getStartPos() + ex.getCurrentToken().length();
        if (ex.getCurrentToken().equals("<EOF>")){
            endPos = ex.getStartPos(); // because start + length of <EOF> would push us past the end of the document
        }
        return new OWLExpressionParserException(ex.getMessage(),
                                                ex.getStartPos(),
                                                endPos,
                                                ex.isClassNameExpected(),
                                                ex.isObjectPropertyNameExpected(),
                                                ex.isDataPropertyNameExpected(),
                                                ex.isIndividualNameExpected(),
                                                ex.isDatatypeNameExpected(),
                                                ex.getExpectedKeywords());
    }

}
