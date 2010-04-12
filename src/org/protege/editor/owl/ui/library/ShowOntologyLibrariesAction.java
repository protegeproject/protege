package org.protege.editor.owl.ui.library;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.owl.ui.action.ProtegeOWLAction;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 24-Aug-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ShowOntologyLibrariesAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
    	try {
    		OntologyLibraryPanel.showDialog(getOWLEditorKit());
    	}
    	catch (Exception ex) {
    		ProtegeApplication.getErrorLog().logError(ex);
    	}
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
