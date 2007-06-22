package org.protege.editor.core.ui.error;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
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
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public class ErrorLogPanel extends JPanel {

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
        JPanel buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.add(new JButton(new AbstractAction("Send bug report") {
            public void actionPerformed(ActionEvent e) {
                if (handleSendErrorReport()) {
                    ErrorLogPanel.this.errorLog.clear();
                }
            }
        }), BorderLayout.WEST);
        contentPane.add(buttonPanel, BorderLayout.SOUTH);
        add(contentPane);
    }


    private boolean handleSendErrorReport() {
        return errorReportHandler.sendErrorReport(errorLog);
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
}
