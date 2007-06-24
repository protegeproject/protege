package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.OWLEditorKit;
import org.protege.editor.owl.model.description.OWLExpressionParserException;
import org.semanticweb.owl.model.OWLDescription;
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
public class OWLDescriptionChecker implements OWLExpressionChecker<OWLDescription> {

    private OWLEditorKit owlEditorKit;


    public OWLDescriptionChecker(OWLEditorKit owlEditorKit) {
        this.owlEditorKit = owlEditorKit;
    }


    public void check(String text) throws OWLExpressionParserException, OWLException {
        owlEditorKit.getOWLModelManager().getOWLDescriptionParser().isWellFormed(text);
    }


    public OWLDescription createObject(String text) throws OWLExpressionParserException, OWLException {
        return owlEditorKit.getOWLModelManager().getOWLDescriptionParser().createOWLDescription(text);
    }
}
