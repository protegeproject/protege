package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 *
 */
class OWLPropertyChainChecker implements OWLExpressionChecker<List<OWLObjectPropertyExpression>> {

    private OWLModelManager mngr;


    public OWLPropertyChainChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }


    public List<OWLObjectPropertyExpression> createObject(String text) throws OWLExpressionParserException {
        try {
            ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParserImpl(OWLOntologyLoaderConfiguration::new, mngr.getOWLDataFactory());
            parser.setStringToParse(text);
            parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr.getOWLEntityFinder()));
            return parser.parseObjectPropertyChain();
        } catch (ParserException e) {
            throw new OWLExpressionParserException(text, e.getStartPos(), e.getStartPos() + 1, false, e.isObjectPropertyNameExpected(), false, false, false, false, e.getExpectedKeywords());
        }
    }
}
