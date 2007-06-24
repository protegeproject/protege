package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class NewAction extends ProtegeAction {

    private static final Logger logger = Logger.getLogger(NewAction.class);


    public void actionPerformed(ActionEvent e) {
        try {
            int ret = JOptionPane.showConfirmDialog(getWorkspace(),
                                                    "Do you want to create the ontology in a new frame?",
                                                    "Create in new frame",
                                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE);
            if (ret == JOptionPane.NO_OPTION) {
                getEditorKit().handleNewRequest();
            }
            else if (ret == JOptionPane.YES_OPTION) {
                for (EditorKitFactoryPlugin plugin : ProtegeManager.getInstance().getEditorKitFactoryPlugins()) {
                    if (plugin.getId().equals(getEditorKit().getEditorKitFactory().getId())) {
                        ProtegeManager.getInstance().createAndSetupNewEditorKit(plugin);
                        break;
                    }
                }
            }
        }
        catch (Exception e1) {
            logger.error(e1);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }
}
