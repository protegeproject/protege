package org.protege.editor.core.ui.error;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.Icons;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public class ErrorNotificationLabel extends JLabel {


    public ErrorNotificationLabel() {
        super(Icons.getIcon("error.png"));
        setToolTipText("Error Log: Click to view errors");
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showErrors();
            }
        });
        setVisible(false);
    }

    private void showErrors() {
        ProtegeApplication.showLogView();
        setVisible(false);
    }
}
