package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.OWLClassAxiom;
import org.semanticweb.owl.model.OWLException;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 11-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassAxiomChecker implements OWLExpressionChecker<OWLClassAxiom> {

    private OWLModelManager mngr;


    public OWLClassAxiomChecker(OWLEditorKit eKit) {
        this.mngr = eKit.getOWLModelManager();
    }


    public void check(String text) throws OWLExpressionParserException, OWLException {
        createObject(text);
    }


    public OWLClassAxiom createObject(String text) throws OWLExpressionParserException, OWLException {
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(mngr.getOWLDataFactory(), text);
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr));
        parser.setBase(mngr.getActiveOntology().getURI().toString());
        try {
            return parser.parseClassAxiom();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}
