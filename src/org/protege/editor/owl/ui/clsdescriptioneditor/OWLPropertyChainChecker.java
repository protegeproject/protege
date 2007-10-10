package org.protege.editor.owl.ui.clsdescriptioneditor;

import java.util.List;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.model.description.manchester.ManchesterSyntaxParser;
import org.semanticweb.owl.model.OWLException;
import org.semanticweb.owl.model.OWLObjectPropertyExpression;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 */
public class OWLPropertyChainChecker implements OWLExpressionChecker<List<OWLObjectPropertyExpression>> {

    private ManchesterSyntaxParser parser;


    public OWLPropertyChainChecker(OWLModelManager owlModelManager) {
        parser = new ManchesterSyntaxParser();
        parser.setOWLModelManager(owlModelManager);
    }


    public void check(String text) throws OWLExpressionParserException, OWLException {
        parser.isWellFormedObjectPropertyChain(text);
    }


    public List<OWLObjectPropertyExpression> createObject(String text) throws OWLExpressionParserException,
                                                                              OWLException {
        return parser.createObjectPropertyChain(text);
    }
}
