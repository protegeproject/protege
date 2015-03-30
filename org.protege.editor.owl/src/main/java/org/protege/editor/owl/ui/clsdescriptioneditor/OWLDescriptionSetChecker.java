package org.protege.editor.owl.ui.clsdescriptioneditor;

import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 11-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 */
class OWLClassExpressionSetChecker implements OWLExpressionChecker<Set<OWLClassExpression>> {

    private OWLModelManager mngr;


    public OWLClassExpressionSetChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }


    public Set<OWLClassExpression> createObject(String text) throws OWLExpressionParserException {
        ManchesterOWLSyntaxParser parser = ParserUtil.manchesterParserFor(text, mngr);
        try {
            return parser.parseClassExpressionList();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}