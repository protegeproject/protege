package org.protege.editor.core.ui.error;

import org.protege.editor.core.ui.util.Icons;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
public class ErrorNotificationLabel extends JLabel {

    private ErrorLogPanel panel;

    private SendErrorReportHandler handler;

    private ErrorLog errorLog;

    private ErrorLogListener listener;


    public ErrorNotificationLabel(ErrorLog errorLog, SendErrorReportHandler handler) {
        super(Icons.getIcon("error.png"));
        this.handler = handler;
        setToolTipText("Protege-Gaurd: Click to view errors");
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
