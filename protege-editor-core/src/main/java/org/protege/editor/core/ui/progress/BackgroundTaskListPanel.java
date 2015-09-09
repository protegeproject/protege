package org.protege.editor.core.ui.progress;

import javax.swing.*;
import java.awt.*;
/*
* Copyright (C) 2007, University of Manchester
*
*
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

    /**
     * 
     */
    private static final long serialVersionUID = -3923645519834624950L;

    private JTextArea textArea;

    private BackgroundTaskManager mngr;


    public BackgroundTaskListPanel(BackgroundTaskManager mngr) {
        this.mngr = mngr;
        setLayout(new BorderLayout());
        textArea = new JTextArea();
        textArea.setEditable(false);
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
