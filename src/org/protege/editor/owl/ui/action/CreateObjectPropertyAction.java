package org.protege.editor.owl.ui.action;

import org.semanticweb.owl.model.AddAxiom;
import org.semanticweb.owl.model.OWLDeclarationAxiom;
import org.semanticweb.owl.model.OWLObjectProperty;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 03-Feb-2007<br><br>
 * 
 * @deprecated now use <code>CreateNewChildAction</code>
 */
public class CreateObjectPropertyAction extends SelectedOWLObjectPropertyAction {

    protected void initialiseAction() throws Exception {
    }


    /**
     * Invoked when an action occurs.
     */
    public void actionPerformed(ActionEvent e) {
        OWLObjectProperty prop = getOWLWorkspace().createOWLObjectProperty().getOWLEntity();
        OWLDeclarationAxiom ax = getOWLDataFactory().getOWLDeclarationAxiom(prop);
        getOWLModelManager().applyChange(new AddAxiom(getOWLModelManager().getActiveOntology(), ax));
    }
}
