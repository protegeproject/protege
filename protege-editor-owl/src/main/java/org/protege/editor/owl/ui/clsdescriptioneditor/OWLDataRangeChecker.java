package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLDataRange;
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
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr.getOWLEntityFinder()));
        parser.setStringToParse(text);
        try {
            return parser.parseDataRange();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}
