package org.protege.editor.core.ui.progress;

import java.util.ArrayList;
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
public class BackgroundTaskManager {

    private List<BackgroundTaskListener> listeners = new ArrayList<BackgroundTaskListener>();

    private List<BackgroundTask> runningTasks = new ArrayList<BackgroundTask>();


    public void addBusyListener(final BackgroundTaskListener listener){
        listeners.add(listener);
    }


    public void removeBusyListener(final BackgroundTaskListener listener){
        listeners.remove(listener);
    }


    public void startTask(final BackgroundTask task){
        runningTasks.add(task);
        for (BackgroundTaskListener l : listeners){
            l.startTask(task);
        }
    }


    /**
     * Convenience method that creates a simple task with the label given
     * @param taskName - the label of the task
     * @return a simple BusyTask that will be needed to end the task
     */
    public BackgroundTask startTask(final String taskName){
        BackgroundTask t = new BackgroundTask(){
            public String toString() {
                return taskName;
            }
        };
        startTask(t);
        return t;
    }


    public void endTask(final BackgroundTask task){
        runningTasks.remove(task);
        for (BackgroundTaskListener l : listeners){
            l.endTask(task);
        }
    }


    public List<BackgroundTask> getRunningTasks(){
        return new ArrayList<BackgroundTask>(runningTasks);
    }


    public boolean isBusy(){
        return !runningTasks.isEmpty();
    }
}
