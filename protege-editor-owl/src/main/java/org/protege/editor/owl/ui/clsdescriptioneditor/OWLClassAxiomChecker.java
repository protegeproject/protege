package org.protege.editor.owl.ui.clsdescriptioneditor;

import java.util.Collections;

import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.classexpression.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owlapi.manchestersyntax.parser.ManchesterOWLSyntaxParserImpl;
import org.semanticweb.owlapi.manchestersyntax.renderer.ParserException;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClassAxiom;
import org.semanticweb.owlapi.model.OWLOntologyLoaderConfiguration;
import org.semanticweb.owlapi.util.mansyntax.ManchesterOWLSyntaxParser;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 11-Oct-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 */
class OWLClassAxiomChecker implements OWLExpressionChecker<OWLClassAxiom> {

    private OWLModelManager mngr;


    public OWLClassAxiomChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }


    public OWLClassAxiom createObject(String text) throws OWLExpressionParserException {
        ManchesterOWLSyntaxParser parser = new ManchesterOWLSyntaxParserImpl(
                OWLOntologyLoaderConfiguration::new, mngr.getOWLDataFactory());
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr.getOWLEntityFinder()));
        parser.setStringToParse(text);
        try {
            OWLAxiom ax = parser.parseAxiom();
            if(ax instanceof OWLClassAxiom) {
                return (OWLClassAxiom) ax;
            }
            else {
                throw new OWLExpressionParserException(
                        "Expected a class axiom of the form C SubClassOf D, C EquivalentTo D, or C DisjointWith D"
                        , 0, 0, true, true, true, false, false, false, Collections.emptySet());
            }
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}
