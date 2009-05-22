package org.protege.editor.core.ui.error;

import org.protege.editor.core.ui.util.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public class ErrorNotificationLabel extends JLabel {

    private ErrorLogPanel panel;

    private SendErrorReportHandler handler;

    private ErrorLog errorLog;

    private ErrorLogListener listener;


    public ErrorNotificationLabel(ErrorLog errorLog, SendErrorReportHandler handler) {
        super(Icons.getIcon("error.png"));
        this.handler = handler;
        setToolTipText("Protege-Guard: Click to view errors");
        setupMouseHandler();
        this.errorLog = errorLog;
        listener = new ErrorLogListener() {

            public void errorLogged(ErrorLog errorLog) {
                setVisible(true);
            }


            public void errorLogCleared(ErrorLog errorLog) {
                setVisible(false);
            }
        };
        errorLog.addListener(listener);
        setVisible(false);
        panel = new ErrorLogPanel(errorLog, handler);
    }


    private void setupMouseHandler() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showErrors();
            }
        });
    }


    private void showErrors() {
        panel.fillLog();
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dlg = optionPane.createDialog(SwingUtilities.getAncestorOfClass(Frame.class, this), "Errors");
        dlg.setResizable(true);
        dlg.setVisible(true);
    }
}
