package org.protege.editor.core.ui.util;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
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
 * Date: 21-Sep-2006<br><br>
 * <p/>
 * matthew.horridge@cs.man.ac.uk<br>
 * www.cs.man.ac.uk/~horridgm<br><br>
 */
public class JOptionPaneEx extends JOptionPane {

    public static int showConfirmDialog(JComponent parent, String title, JComponent content, int messageType,
                                        int optionType, final JComponent defaultFocusedComponent) {

        JOptionPane optionPane = new JOptionPane(content, messageType, optionType);
        JDialog dlg = optionPane.createDialog(parent, title);
        dlg.addWindowListener(new WindowAdapter() {
            public void windowOpened(WindowEvent e) {
                if (defaultFocusedComponent != null) {
                    defaultFocusedComponent.requestFocusInWindow();
                }
            }
        });
        dlg.setResizable(true);
        dlg.setVisible(true);
        Object value = optionPane.getValue();
        return (value != null) ? (Integer) value : JOptionPane.CLOSED_OPTION;
    }
}
