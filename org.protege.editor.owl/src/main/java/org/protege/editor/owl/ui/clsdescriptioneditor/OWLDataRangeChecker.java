package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLDataRange;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Dec 8, 2008<br><br>
 */
public class OWLDataRangeChecker implements OWLExpressionChecker<OWLDataRange>{

    private OWLModelManager mngr;


    public OWLDataRangeChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }


    public OWLDataRange createObject(String text) throws OWLExpressionParserException {
        ManchesterOWLSyntaxParser parser = ParserUtil.manchesterParserFor(text, mngr);
        try {
            return parser.parseDataRange();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}
