package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntax;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.SWRLRule;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Jul 23, 2008<br><br>
 *
 */
class SWRLRuleChecker implements OWLExpressionChecker<SWRLRule> {

    private Logger logger = Logger.getLogger(SWRLRuleChecker.class);

    private OWLModelManager mngr;


    public SWRLRuleChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    @Override
    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }

  /*
   * Workaround for owlapi feature request 2896097.  Remove this fix when 
   * the simple rule renderer and parser is implemented.  Svn at time of 
   * commit is approximately 16831
   */
    @Override
    public SWRLRule createObject(String text) throws OWLExpressionParserException {
        ManchesterOWLSyntaxParser parser = OWLManager.createManchesterParser();
        parser.setStringToParse(ManchesterOWLSyntax.RULE + " " + text);
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr.getOWLEntityFinder()));
        try {
            return (SWRLRule) parser.parseRuleFrame().iterator().next().getAxiom();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}
