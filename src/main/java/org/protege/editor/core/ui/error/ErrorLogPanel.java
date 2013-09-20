package org.protege.editor.core.ui.error;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public class ErrorLogPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = -8745982593246886108L;

    private ErrorLog errorLog;

    private JTextArea textArea;

    private SendErrorReportHandler errorReportHandler;


    public ErrorLogPanel(ErrorLog errorLog, SendErrorReportHandler handler) {
        this.errorLog = errorLog;
        this.errorReportHandler = handler;
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        JPanel contentPane = new JPanel(new BorderLayout(7, 7));
        contentPane.add(new JScrollPane(textArea));
        if (handler != null) {
            JPanel buttonPanel = new JPanel(new BorderLayout());
            buttonPanel.add(new JButton(new AbstractAction("Clear Errors") {
                /**
                 * 
                 */
                private static final long serialVersionUID = 6061428199099888086L;

                public void actionPerformed(ActionEvent e) {
                    if (handleSendErrorReport()) {
                        ErrorLogPanel.this.errorLog.clear();
                        fillLog();
                        repaint();
                    }
                }
            }), BorderLayout.WEST);
            contentPane.add(buttonPanel, BorderLayout.SOUTH);
        }
        add(contentPane, BorderLayout.CENTER);
        fillLog();
    }


    private boolean handleSendErrorReport() {
        return errorReportHandler != null && errorReportHandler.sendErrorReport(errorLog);
    }


    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }


    public void setVisible(boolean b) {
        fillLog();
        Component parent = getParent();
        if (parent != null) {
            int w = (parent.getWidth() - getWidth()) / 2;
            int h = (parent.getHeight() - getHeight()) / 2;
            setLocation(w, h);
        }
        super.setVisible(b);
    }


    public void fillLog() {
        textArea.setText("");
        for (ErrorLog.ErrorLogEntry entry : errorLog.getEntries()) {
            textArea.append(entry.toString());
            textArea.append(
                    "---------------------------------------------------------------------------------------------------\n\n");
        }
    }


    protected ErrorLog getErrorLog(){
        return errorLog;
    }


    /**
     * Shows a local error dialog for displaying one exception
     * @param throwable The exception to be displayed
     */
    public static void showErrorDialog(Throwable throwable) {
        ErrorLog errorLog = new ErrorLog();
        if (throwable != null) {
            errorLog.logError(throwable);
        }
        ErrorLogPanel panel = new ErrorLogPanel(errorLog, null);
        JOptionPane.showMessageDialog(null, panel, "Error", JOptionPane.ERROR_MESSAGE);
    }

}
