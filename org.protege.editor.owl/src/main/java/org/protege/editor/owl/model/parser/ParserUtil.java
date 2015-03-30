package org.protege.editor.owl.model.parser;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.owlapi.apibinding.ProtegeOWLManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

/*
* Copyright (C) 2007, University of Manchester
*
*
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

    public static ManchesterOWLSyntaxParser manchesterParserFor(String text, OWLModelManager manager) {
        ManchesterOWLSyntaxParser parser=ProtegeOWLManager.createManchesterParser();
        parser.setStringToParse(text);
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(manager.getOWLEntityFinder()));
        return parser;
    }
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
                                                ex.isAnnotationPropertyNameExpected(),
                                                ex.getExpectedKeywords());
    }

}
