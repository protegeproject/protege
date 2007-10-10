package org.protege.editor.core.ui.about;

import java.awt.event.ActionEvent;

import org.protege.editor.core.ui.action.ProtegeAction;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 01-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class AboutProtegeAction extends ProtegeAction {

    public void actionPerformed(ActionEvent e) {
        AboutPanel.showDialog();
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
