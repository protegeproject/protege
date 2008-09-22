package org.protege.editor.core.ui.action;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.core.ui.util.OpenRequestHandler;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.workspace.Workspace;

import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OpenAction extends ProtegeAction implements OpenRequestHandler {


    public void actionPerformed(ActionEvent e) {
        try {
            UIUtil.openRequest(this);
        }
        catch (Exception e1) {
            ErrorLogPanel.showErrorDialog(e1);
        }
    }


    public void initialise() throws Exception {
    }


    public void dispose() {
    }


    public Workspace getCurrentWorkspace() {
        return getWorkspace();
    }


    public void openInNewWorkspace() throws Exception {
        for (EditorKitFactoryPlugin plugin : ProtegeManager.getInstance().getEditorKitFactoryPlugins()) {
            if (plugin.getId().equals(getEditorKit().getEditorKitFactory().getId())) {
                ProtegeManager.getInstance().openAndSetupEditorKit(plugin);
                break;
            }
        }
    }


    public void openInCurrentWorkspace() throws Exception {
        getEditorKit().handleLoadRequest();
    }
}
