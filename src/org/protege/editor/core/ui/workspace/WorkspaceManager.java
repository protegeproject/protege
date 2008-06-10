package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.ProtegeManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;


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
                    if (doClose(workspace)){
                        // Remove the listener
                        frame.removeWindowListener(this);
                        frame.dispose();
                    }
                }
            });
            workspaceFrameMap.put(workspace, frame);
            frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
            frame.setVisible(true);
        }
    }


    public boolean doClose(Workspace workspace) {
        int ret = JOptionPane.showConfirmDialog(ProtegeManager.getInstance().getFrame(workspace),
                                                "Close the current set of ontologies?",
                                                "Close?",
                                                JOptionPane.YES_NO_OPTION,
                                                JOptionPane.WARNING_MESSAGE);
        if (ret == JOptionPane.YES_OPTION) {
            ProtegeManager.getInstance().disposeOfEditorKit(workspace.getEditorKit());
        }

        return ret == JOptionPane.YES_OPTION;
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
