package org.protege.editor.core.ui.workspace;

import org.apache.log4j.Logger;
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

    private static final Logger logger = Logger.getLogger(WorkspaceManager.class);

    public static final String SAVE_CONFIRMATION_MESSAGE = "<html><body><b>Do you want to save changes that " +
            "you made to the ontologies in this workspace?</b><br>" +
            "Your changes will be lost if you don't save them.</body></html>";

    private Map<Workspace, WorkspaceFrame> workspaceFrameMap;


    public WorkspaceManager() {
        this.workspaceFrameMap = new HashMap<Workspace, WorkspaceFrame>();
    }


    /**
     * Adds an open workspace to the manager which causes the workspace to be shown.
     * The manager will handle the closing of the workspace.
     * @param workspace The workspace to tbe added.
     */
    public void addWorkspace(final Workspace workspace) {
        if (!workspaceFrameMap.containsKey(workspace)) {
            // Add the workspace
            final WorkspaceFrame frame = new WorkspaceFrame(workspace);
            frame.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent e) {
                    if (doClose(workspace)) {
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


    /**
     * Asks the manager to close the workspace.  Any frame and other paraphernalia associated with the workspace
     * will be disposed of.
     * @param workspace The workspace.
     * @return <code>true</code> if the workspace was closed, otherwise <code>false</code>.  The workspace may not
     *         actually be closed because the user may cancel the close operation.
     */
    public boolean doClose(Workspace workspace) {
        try {
            boolean dirty = workspace.getEditorKit().getModelManager().isDirty();
            if (dirty) {
                // Ask user if they want to save?
                int ret = JOptionPane.showConfirmDialog(workspace,
                        SAVE_CONFIRMATION_MESSAGE,
                        "Save workspace?",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                // The default option is returned when the escape button is pressed.
                if (ret == JOptionPane.CANCEL_OPTION || ret == JOptionPane.DEFAULT_OPTION) {
                    return false;
                }
                if (ret == JOptionPane.YES_OPTION) {
                    workspace.getEditorKit().handleSave();
                    if(workspace.getEditorKit().getModelManager().isDirty()) {
                        // Wasn't saved - i.e. cancelled
                        return false;
                    }
                }
            }
            ProtegeManager.getInstance().disposeOfEditorKit(workspace.getEditorKit());
            return true;
        }
        catch (Exception e) {
            logger.error(e);
            return false;
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
