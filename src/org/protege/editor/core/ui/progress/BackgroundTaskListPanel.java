package org.protege.editor.core.ui.progress;

import javax.swing.*;
import java.awt.*;
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
 * Date: May 8, 2009<br><br>
 */
public class BackgroundTaskListPanel extends JPanel {

    private JTextArea textArea;

    private BackgroundTaskManager mngr;


    public BackgroundTaskListPanel(BackgroundTaskManager mngr) {
        this.mngr = mngr;
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        JPanel contentPane = new JPanel(new BorderLayout(7, 7));
        contentPane.add(new JScrollPane(textArea));
        add(contentPane, BorderLayout.CENTER);
        fillLog();
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
        int indent = 0;
        for (BackgroundTask entry : mngr.getRunningTasks()) {
            for (int i=0; i<indent; i++){
                textArea.append("\t");
            }
            textArea.append(entry.toString());
            textArea.append("\n");
            indent++;
        }
    }


    /**
     * Shows a local error dialog for displaying one exception
     */
    public static void showTaskDialog(BackgroundTaskManager mngr) {
        BackgroundTaskListPanel panel = new BackgroundTaskListPanel(mngr);
        JOptionPane optionPane = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE, JOptionPane.DEFAULT_OPTION);
        JDialog dlg = optionPane.createDialog(null, "Background Tasks");
        dlg.setResizable(true);
        dlg.setVisible(true);
    }
}
