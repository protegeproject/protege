package org.protege.editor.core.ui.workspace;

import org.protege.editor.core.ProtegeManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.HashMap;
import java.util.Map;
/*
 * Copyright (C) 2007, University of Manchester
 *
 * Modifications to the initial code base are copyright of their
 * respective authors, or their employers as appropriate.  Authorship
 * of the modifications may be determined from the ChangeLog placed at
 * the end of this file.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.

 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.

 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */


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
