package org.protege.editor.core.ui.error;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.log.LogView;
import org.protege.editor.core.log.LogViewImpl;
import org.protege.editor.core.ui.util.Icons;
import org.protege.editor.core.ui.workspace.Workspace;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public class ErrorNotificationLabel extends JLabel {

    /**
     * 
     */
    private static final long serialVersionUID = -1816375999125772505L;

    private final Workspace workspace;

    private final LogView logView;


    public ErrorNotificationLabel(Workspace workspace) {
        super(Icons.getIcon("error.png"));
        this.workspace = workspace;
        logView = new LogViewImpl();
        setToolTipText("Error Log: Click to view errors");
        setupMouseHandler();
        setVisible(false);

    }


    private void setupMouseHandler() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showErrors();
            }
        });
    }


    private void showErrors() {
        ProtegeApplication.showLogView();
    }
}
