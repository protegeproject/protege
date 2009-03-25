package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.coode.manchesterowlsyntax.ManchesterOWLSyntaxEditorParser;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.protege.editor.owl.model.parser.ParserUtil;
import org.protege.editor.owl.model.parser.ProtegeOWLEntityChecker;
import org.semanticweb.owl.expression.ParserException;
import org.semanticweb.owl.model.OWLDescription;

import java.util.Set;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 11-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 *
 * @@TODO should be package visibility
 */
class OWLDescriptionSetChecker implements OWLExpressionChecker<Set<OWLDescription>> {

    private OWLModelManager mngr;


    public OWLDescriptionSetChecker(OWLModelManager mngr) {
        this.mngr = mngr;
    }


    public void check(String text) throws OWLExpressionParserException {
        createObject(text);
    }


    public Set<OWLDescription> createObject(String text) throws OWLExpressionParserException {
        ManchesterOWLSyntaxEditorParser parser = new ManchesterOWLSyntaxEditorParser(mngr.getOWLDataFactory(), text);
        parser.setOWLEntityChecker(new ProtegeOWLEntityChecker(mngr));
        parser.setBase(mngr.getActiveOntology().getURI().toString() + "#");
        try {
            return parser.parseDescriptionList();
        }
        catch (ParserException e) {
            throw ParserUtil.convertException(e);
        }
    }
}