package org.protege.editor.owl.ui.action;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.editorkit.EditorKitFactoryPlugin;
import org.protege.editor.core.ui.OpenFromURLPanel;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.core.ui.util.OpenRequestHandler;
import org.protege.editor.core.ui.util.UIUtil;
import org.protege.editor.core.ui.workspace.Workspace;

import java.awt.event.ActionEvent;
import java.net.URI;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 20-Dec-2006<br><br>
 */
public class OpenFromURLAction extends ProtegeOWLAction implements OpenRequestHandler {

    public void actionPerformed(ActionEvent e) {
        try {
            UIUtil.openRequest(this);
        }
        catch (Exception e1) {
            ErrorLogPanel.showErrorDialog(e1);
        }
    }


    private URI getPhysicalURI() {
        return OpenFromURLPanel.showDialog();
    }


    public void dispose() {
    }


    public void initialise() throws Exception {
    }


    public Workspace getCurrentWorkspace() {
        return getWorkspace();
    }


    public void openInNewWorkspace() throws Exception {
        URI uri = getPhysicalURI();
        if (uri != null) {
            for (EditorKitFactoryPlugin plugin : ProtegeManager.getInstance().getEditorKitFactoryPlugins()) {
                if (plugin.getId().equals(getEditorKit().getEditorKitFactory().getId())) {
                    ProtegeManager.getInstance().loadAndSetupEditorKitFromURI(plugin, uri);
                    break;
                }
            }
        }
    }


    public void openInCurrentWorkspace() throws Exception {
        URI uri = getPhysicalURI();
        if (uri != null) {
            getOWLEditorKit().handleLoadFrom(uri);
        }
    }
}
