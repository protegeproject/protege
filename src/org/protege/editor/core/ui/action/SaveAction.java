package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 26-May-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class SaveAction extends ProtegeAction {

    private static final Logger logger = Logger.getLogger(SaveAction.class);


    public void actionPerformed(ActionEvent e) {
        try {
            ProtegeManager.getInstance().saveEditorKit(getEditorKit());
        }
        catch (Exception e1) {
            logger.error(e);
        }
    }


    public void dispose() {
    }


    public void initialise() throws Exception {
    }
}
