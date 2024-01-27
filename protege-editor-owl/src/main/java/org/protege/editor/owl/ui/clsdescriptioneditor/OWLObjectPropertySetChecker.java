package org.protege.editor.owl.ui.clsdescriptioneditor;

import java.util.Set;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLObjectPropertyExpression;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 22-Feb-2007<br><br>
 *
 */
class OWLObjectPropertySetChecker implements OWLExpressionChecker<Set<OWLObjectPropertyExpression>> {

    private OWLModelManager mngr;


    public OWLObjectPropertySetChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }


    public Set<OWLObjectPropertyExpression> createObject(String text) throws OWLExpressionParserException {
        ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParserImpl(OWLOntologyLoaderConfiguration::new, mngr.getOWLDataFactory());
        parser.setStringToParse(text);
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr.getOWLEntityFinder()));
        try {
            return parser.parseObjectPropertyList();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}