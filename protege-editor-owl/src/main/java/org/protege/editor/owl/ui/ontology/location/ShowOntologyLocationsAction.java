package org.protege.editor.owl.ui.ontology.location;

import org.protege.editor.owl.ui.action.ProtegeOWLAction;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 02-Sep-2006<br><br>

 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class ShowOntologyLocationsAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        PhysicalLocationPanel.showDialog(getOWLEditorKit());
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
