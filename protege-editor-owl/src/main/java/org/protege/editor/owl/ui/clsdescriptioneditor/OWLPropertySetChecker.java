package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.model.OWLPropertyExpression;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;

import java.util.Set;


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
        ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParserImpl(
                OWLOntologyLoaderConfiguration::new,
                mngr.getOWLDataFactory());
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr.getOWLEntityFinder()));
        try {
        	/*
        	 * Degenericized to be compatible with changing OWLAPI interfaces
        	 */
            parser.setStringToParse(text);
            return parser.parsePropertyList();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}