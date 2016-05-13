package org.protege.editor.owl.ui.util;

import org.protege.editor.core.ProtegeManager;
import org.protege.editor.core.ui.workspace.WorkspaceManager;
import org.protege.editor.core.ui.workspace.WorkspaceViewManager;
import org.protege.editor.owl.model.OWLModelManager;

import javax.swing.*;
import java.awt.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 11 May 16
 */
public class ProgressDialog {

    private final JDialog dlg = new JDialog((JFrame) null, "", true);

    private final ProgressView view;

    public ProgressDialog() {
        dlg.setUndecorated(true);
        dlg.getContentPane().setLayout(new BorderLayout());
        view = new ProgressViewImpl();
        dlg.getContentPane().add(view.asJComponent(), BorderLayout.NORTH);
    }

    public void setVisible(boolean visible) {
        dlg.pack();
        Dimension prefSize = dlg.getPreferredSize();
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        dlg.setLocation(
                (screenSize.width - prefSize.width) / 2,
                (screenSize.height - prefSize.height) / 2);
        dlg.setVisible(visible);
    }

    public void setMessage(String message) {
        view.setMessage(message);
    }

    public void setSubMessage(String subMessage) {
        view.setSubMessage(subMessage);
    }

    public void clearSubMessage() {
        view.clearSubMessage();
    }

}
