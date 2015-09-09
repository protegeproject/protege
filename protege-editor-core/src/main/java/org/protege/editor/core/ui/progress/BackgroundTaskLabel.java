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
