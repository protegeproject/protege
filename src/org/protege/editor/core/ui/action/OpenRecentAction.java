package org.protege.editor.core.ui.action;

import java.awt.event.ActionEvent;

import javax.swing.*;

import org.apache.log4j.Logger;
import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.error.ErrorLogPanel;
import org.protege.editor.core.ui.error.ErrorLog;
import org.protege.editor.core.editorkit.EditorKitDescriptor;
import org.protege.editor.core.editorkit.RecentEditorKitManager;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Medical Informatics Group<br>
 * Date: 17-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class OpenRecentAction extends ProtegeDynamicAction {

    private static final Logger logger = Logger.getLogger(OpenRecentAction.class);


    public void actionPerformed(ActionEvent e) {
    }


    public void initialise() throws Exception {
    }


    public void rebuildChildMenuItems(JMenu thisMenuItem) {
        RecentEditorKitManager man = RecentEditorKitManager.getInstance();
        for (EditorKitDescriptor descriptor : man.getDescriptors()) {
            thisMenuItem.add(new RecentEditorKitAction(descriptor));
        }
        thisMenuItem.addSeparator();
        thisMenuItem.add(new AbstractAction("Clear Menu") {
            public void actionPerformed(ActionEvent e) {
                RecentEditorKitManager.getInstance().clear();
            }
        });
    }


    public void dispose() {
    }


    private class RecentEditorKitAction extends AbstractAction {

        private EditorKitDescriptor descriptor;


        public RecentEditorKitAction(EditorKitDescriptor descriptor) {
            super(descriptor.getLabel());
            this.descriptor = descriptor;
        }


        public void actionPerformed(ActionEvent e) {
            try {
                ProtegeManager.getInstance().openAndSetupRecentEditorKit(descriptor);
            }
            catch (Exception e1) {
                ErrorLogPanel.showErrorDialog(e1);
            }
        }
    }
}
