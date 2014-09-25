package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.util.JOptionPaneEx;
import org.protege.editor.owl.ui.selector.OWLOntologySelectorPanel2;
import org.semanticweb.owlapi.model.OWLOntology;

import javax.swing.*;
import java.awt.event.ActionEvent;
/*
* Copyright (C) 2007, University of Manchester
*
*
*/

/**
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Sep 30, 2008<br><br>
 */
public class CloseOntologiesAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent event) {
        OWLOntologySelectorPanel2 ontologyList = new OWLOntologySelectorPanel2(getOWLEditorKit(),
                                                                               getOWLModelManager().getOntologies());
        ontologyList.checkAll(false);

        if (JOptionPaneEx.showConfirmDialog(getWorkspace(),
                                                      "Close ontologies",
                                                      ontologyList,
                                                      JOptionPane.PLAIN_MESSAGE,
                                                      JOptionPane.OK_CANCEL_OPTION,
                                                      ontologyList) == JOptionPane.OK_OPTION){
            if (ontologyList.getSelectedOntologies().size() == getOWLModelManager().getOntologies().size()){
                ProtegeManager.getInstance().disposeOfEditorKit(getOWLEditorKit());
            }
            else{
                for (OWLOntology ont : ontologyList.getSelectedOntologies()){
                    getOWLModelManager().removeOntology(ont);
                }
            }
        }
    }


    public void initialise() throws Exception {
        // do nothing
    }


    public void dispose() throws Exception {
        // do nothing
    }
}
