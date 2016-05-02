package org.protege.editor.core.ui.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.PrintWriter;
import java.io.StringWriter;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public class ErrorLogPanel extends JPanel {

    private static final Logger logger = LoggerFactory.getLogger(ErrorLogPanel.class);

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
     * @param throwable The exception to be displayed.
     */
    public static void showErrorDialog(Throwable throwable) {
        logger.error("An error was thrown: {}", throwable.getMessage(), throwable);
        JTextArea textPane = new JTextArea(15, 80);
        textPane.setLineWrap(false);
        textPane.setEditable(false);
        StringWriter sw = new StringWriter();
        throwable.printStackTrace(new PrintWriter(sw));
        textPane.setText(sw.toString());
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane sp = new JScrollPane(textPane);
        sp.setPreferredSize(new Dimension(800, 300));
        JOptionPane.showMessageDialog(null, sp, "An error has occurred", JOptionPane.ERROR_MESSAGE);
    }

}
