package org.protege.editor.owl.ui.clsdescriptioneditor;

import org.protege.editor.owl.OWLEditorKit;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 06-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OWLClassAxiomEditor extends ExpressionEditor {

    public OWLClassAxiomEditor(OWLEditorKit owlEditorKit) {
        super(owlEditorKit, owlEditorKit.getModelManager().getOWLExpressionCheckerFactory().getClassAxiomChecker());
    }
}
