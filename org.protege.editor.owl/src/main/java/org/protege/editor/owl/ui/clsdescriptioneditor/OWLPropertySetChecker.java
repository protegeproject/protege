package org.protege.editor.owl.ui.clsdescriptioneditor;


import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 *
 */
class OWLPropertySetChecker implements OWLExpressionChecker<Set<OWLPropertyExpression>> {

    private OWLModelManager mngr;


    public OWLPropertySetChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }


    public Set<OWLPropertyExpression> createObject(String text) throws OWLExpressionParserException {
        ManchesterOWLSyntaxParser parser = ParserUtil.manchesterParserFor(text, mngr);
        try {
            return parser.parsePropertyList();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}