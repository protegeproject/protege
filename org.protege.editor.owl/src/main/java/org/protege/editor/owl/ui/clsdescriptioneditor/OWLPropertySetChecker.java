package org.protege.editor.owl.ui.clsdescriptioneditor;

import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owlapi.apibinding.OWLManager;
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


    @Override
    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }


    @Override
    public Set<OWLPropertyExpression> createObject(String text) throws OWLExpressionParserException {
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setStringToParse(text);
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr.getOWLEntityFinder()));
        try {
            return parser.parsePropertyList();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}