package org.protege.editor.core.ui.error;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public class ErrorLog implements Thread.UncaughtExceptionHandler {
	private static final Logger logger = Logger.getLogger(ErrorLog.class);

    private static int errorCount;

    public static final int MAX_NUMBER_OF_ERRORS = 100;

    private List<ErrorLogEntry> errors;

    private List<WeakReference<ErrorLogListener>> listeners;


    public ErrorLog() {
        errors = new ArrayList<ErrorLogEntry>();
        listeners = new ArrayList<WeakReference<ErrorLogListener>>();
    }


    public void addListener(ErrorLogListener listener) {
        listeners.add(new WeakReference<ErrorLogListener>(listener));
    }


    public void removeListener(ErrorLogListener listener) {
        listeners.remove(new WeakReference<ErrorLogListener>(listener));
    }


    public void uncaughtException(Thread t, Throwable e) {
        logError(e);
    }


    public void logError(Throwable throwable) {
    	logger.warn("Error logged", throwable);
        errorCount++;
        errors.add(new ErrorLogEntry(throwable));
        if (errors.size() > MAX_NUMBER_OF_ERRORS) {
            errors.remove(0);
        }
        fireErrorLoggedEvent();
    }


    public List<ErrorLogEntry> getEntries() {
        return Collections.unmodifiableList(errors);
    }


    public void clear() {
        errors.clear();
        fireErrorLogClearedEvent();
    }


    private void fireErrorLoggedEvent() {
        for (WeakReference<ErrorLogListener> ref : new ArrayList<WeakReference<ErrorLogListener>>(listeners)) {
            ErrorLogListener listener = ref.get();
            if (listener != null) {
                listener.errorLogged(this);
            }
            else {
                listeners.remove(ref);
            }
        }
    }


    private void fireErrorLogClearedEvent() {
        for (WeakReference<ErrorLogListener> ref : new ArrayList<WeakReference<ErrorLogListener>>(listeners)) {
            ErrorLogListener listener = ref.get();
            if (listener != null) {
                listener.errorLogCleared(this);
            }
            else {
                listeners.remove(ref);
            }
        }
    }


    public class ErrorLogEntry {

        private int id;

        private long timeStamp;

        private Throwable throwable;


        public ErrorLogEntry(Throwable throwable) {
            this.timeStamp = System.currentTimeMillis();
            this.throwable = throwable;
            id = errorCount;
        }


        public int getId() {
            return id;
        }


        public long getTimeStamp() {
            return timeStamp;
        }


        public Throwable getThrowable() {
            return throwable;
        }


        public String toString() {
            StringBuilder sb = new StringBuilder();
            sb.append("Error ");
            sb.append(id);
            sb.append(" Logged at ");
            sb.append(new Date(timeStamp));
            sb.append("\n");
            sb.append(throwable.getClass().getSimpleName());
            sb.append(": ");
            sb.append(throwable.getMessage());
            sb.append("\n");
            for (StackTraceElement element : throwable.getStackTrace()) {
                sb.append("    ");
                sb.append(element.toString());
                sb.append("\n");
            }
            return sb.toString();
        }
    }


    public void handleError(Thread t, Throwable e) {
        logError(e);
    }
}
