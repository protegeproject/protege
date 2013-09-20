package org.protege.editor.core.ui.progress;

import org.protege.editor.core.ui.util.Icons;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
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
public class BackgroundTaskLabel extends JLabel implements BackgroundTaskListener {

    /**
     * 
     */
    private static final long serialVersionUID = -5229610259844307503L;

    private BackgroundTaskManager mngr;

    private static final int DELAY_MILLIS = 1000;

    private ActionListener timeout = new ActionListener(){
        public void actionPerformed(ActionEvent event) {
            setVisible(true);
        }
    };

    private Timer t;

    public BackgroundTaskLabel(BackgroundTaskManager mngr) {
        super(Icons.getIcon("busy.gif"));
        setOpaque(false);
        setVisible(false);

        this.mngr = mngr;

        t = new Timer(DELAY_MILLIS, timeout);
        t.setRepeats(false);
        mngr.addBusyListener(this);

        setupMouseHandler();
    }


    public void startTask(BackgroundTask task) {
        if (!isVisible() && !t.isRunning()){
            t.start();
        }
    }


    public void endTask(BackgroundTask task) {
        t.stop();
        runInSwingThread(new Runnable(){
            public void run() {
                setVisible(mngr.isBusy());
            }
        });
    }


    public String getToolTipText() {
        final List<BackgroundTask> tasks = mngr.getRunningTasks();
        String tt = tasks.get(tasks.size()-1).toString();
        System.out.println(tt);
        return tt;
    }

    private void setupMouseHandler() {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                showTasks();
            }
        });
    }


    private void showTasks() {
        BackgroundTaskListPanel.showTaskDialog(mngr);
    }


    private void runInSwingThread(Runnable r){
        if (SwingUtilities.isEventDispatchThread()) {
            r.run();
        }
        else {
            SwingUtilities.invokeLater(r);
        }
    }
}
