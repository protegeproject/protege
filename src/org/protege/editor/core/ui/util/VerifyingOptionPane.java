package org.protege.editor.core.ui.util;

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
 * Author: drummond<br>
 * http://www.cs.man.ac.uk/~drummond/<br><br>
 * <p/>
 * The University Of Manchester<br>
 * Bio Health Informatics Group<br>
 * Date: Mar 14, 2008<br><br>
 *
 * A pane that resembles an OK_CANCEL_OPTION JOptionPane,
 * but allows us to disable options to allow for user input
 * verification.
 */
public class VerifyingOptionPane extends JComponent {

    private final JComponent c;
    private JDialog dlg;

    private int result;

    private Action okAction = new AbstractAction("OK"){
            public void actionPerformed(ActionEvent actionEvent) {
                result = JOptionPane.OK_OPTION;
                dlg.setVisible(false);
            }
        };

    private Action cancelAction = new AbstractAction("Cancel"){
            public void actionPerformed(ActionEvent actionEvent) {
                result = JOptionPane.CANCEL_OPTION;
                dlg.setVisible(false);
            }
        };


    public VerifyingOptionPane(JComponent c) {
        this.c = c;
    }

    public JDialog createDialog(Component parent, String title){
        while (!(parent instanceof Frame)){
            parent = parent.getParent();
        }
        dlg = new JDialog((Frame) parent, title);
        dlg.setLayout(new BorderLayout(6, 6));
        dlg.add(c, BorderLayout.CENTER);

        JButton okButton = new JButton(okAction);
        JButton cancelButton = new JButton(cancelAction);

        JPanel optionsPanel = new JPanel();
        optionsPanel.add(cancelButton);
        optionsPanel.add(okButton);
        dlg.add(optionsPanel, BorderLayout.SOUTH);

        dlg.getRootPane().setDefaultButton(okButton);
        okAction.setEnabled(true); // by default OK is enabled

        return dlg;
    }

    public int getValue(){
        return result;
    }

    public void setOKEnabled(boolean enabled){
        okAction.setEnabled(enabled);
    }
}
