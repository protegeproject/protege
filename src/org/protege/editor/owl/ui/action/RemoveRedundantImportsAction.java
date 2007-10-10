package org.protege.editor.owl.ui.action;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 03-Oct-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class RemoveRedundantImportsAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        System.out.println("NOT IMPLEMENTED: " + getClass());
//        try {
//            RemoveRedundantImports rem = new RemoveRedundantImports(getOWLModelManager().getActiveOntologies());
//            getOWLModelManager().applyChanges(rem.getChanges());
//        } catch (OWLException e1) {
//            e1.printStackTrace();
//        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
