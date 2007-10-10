package org.protege.editor.core.ui.workspace;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JFrame;

import org.protege.editor.core.ProtegeManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: Mar 27, 2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class WorkspaceManager {


    private Map<Workspace, WorkspaceFrame> workspaceFrameMap;


    public WorkspaceManager() {
        this.workspaceFrameMap = new HashMap<Workspace, WorkspaceFrame>();
    }


    public void addWorkspace(final Workspace workspace) {
        if (!workspaceFrameMap.containsKey(workspace)) {
            // Add the workspace
            final WorkspaceFrame frame = new WorkspaceFrame(workspace);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    // Remove the listener
                    frame.removeWindowListener(this);
                    // Dispose of the clsdescriptioneditor kit
                    ProtegeManager.getInstance().disposeOfEditorKit(workspace.getEditorKit());
                }
            });
            workspaceFrameMap.put(workspace, frame);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setVisible(true);
        }
    }


    public void removeWorkspace(Workspace workspace) {
        WorkspaceFrame frame = workspaceFrameMap.get(workspace);
        if (frame != null) {
            frame.dispose();
            workspaceFrameMap.remove(workspace);
        }
    }


    public WorkspaceFrame getFrame(Workspace workspace) {
        return workspaceFrameMap.get(workspace);
    }
}
