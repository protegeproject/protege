package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;
import org.protege.editor.core.ui.OpenFromURIPanel;
import org.protege.editor.core.ui.error.ErrorLogPanel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Dec-2006<br><br>
 */
public class OpenFromURIAction extends ProtegeOWLAction {

    public void actionPerformed(ActionEvent e) {
        try {
            int ret = JOptionPane.showConfirmDialog(getWorkspace(),
                                                    "Do you want to open the ontology in a new frame?",
                                                    "Open in new frame",
                                                    JOptionPane.YES_NO_CANCEL_OPTION,
                                                    JOptionPane.QUESTION_MESSAGE);

            if (ret == JOptionPane.NO_OPTION) {
                URI uri = getURI();
                if (uri != null) {
                    getOWLEditorKit().handleLoadFrom(uri);
                }
            }
            else if (ret == JOptionPane.YES_OPTION) {
                URI uri = getURI();
                if (uri != null) {
                    for (EditorKitFactoryPlugin plugin : ProtegeManager.getInstance().getEditorKitFactoryPlugins()) {
                        if (plugin.getId().equals(getEditorKit().getEditorKitFactory().getId())) {
                            ProtegeManager.getInstance().loadAndSetupEditorKitFromURI(plugin, uri);
                            break;
                        }
                    }
                }
            }
        }
        catch (Exception e1) {
            ErrorLogPanel.showErrorDialog(e1);
        }
    }


    private URI getURI() {
        return OpenFromURIPanel.showDialog();
    }


    public void dispose() {
    }


    public void initialise() throws Exception {
    }
}
