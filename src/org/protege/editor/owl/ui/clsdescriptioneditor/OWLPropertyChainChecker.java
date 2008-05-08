package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;

import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLPropertyChainChecker implements OWLExpressionChecker<List<OWLObjectPropertyExpression>> {

    private OWLModelManager mngr;


    public OWLPropertyChainChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public void check(String text) throws OWLExpressionParserException, OWLException {
        createObject(text);
    }


    public List<OWLObjectPropertyExpression> createObject(String text) throws OWLExpressionParserException,
                                                                              OWLException {
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(mngr.getOWLDataFactory(), text);
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr));
        parser.setBase(mngr.getActiveOntology().getURI().toString());
        try {
            return parser.parseObjectPropertyChain();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}
