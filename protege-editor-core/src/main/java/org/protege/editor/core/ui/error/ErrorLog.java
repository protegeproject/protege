package org.protege.editor.core.ui.error;

import com.google.common.collect.EvictingQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public class ErrorLog implements Thread.UncaughtExceptionHandler {

    public static final int MAX_NUMBER_OF_ERRORS = 100;


    private final Logger logger = LoggerFactory.getLogger(ErrorLog.class);

    private final AtomicInteger errorCount = new AtomicInteger();



    private final Queue<ErrorLogEntry> errors;

    private final List<WeakReference<ErrorLogListener>> listeners;

    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    private final Lock readLock = readWriteLock.readLock();

    private final Lock writeLock = readWriteLock.writeLock();

    public ErrorLog() {
        errors = EvictingQueue.create(MAX_NUMBER_OF_ERRORS);
        listeners = new ArrayList<>();
    }


    public void addListener(ErrorLogListener listener) {
        listeners.add(new WeakReference<>(listener));
    }


    public void removeListener(ErrorLogListener listener) {
        listeners.remove(new WeakReference<>(listener));
    }


    public void clearListeners(){
        listeners.clear();
    }


    public void uncaughtException(Thread t, Throwable e) {
        logError(e);
    }


    public void logError(Throwable throwable) {
        writeLock.lock();
        try {
            int id = errorCount.incrementAndGet();
            long timestamp = System.currentTimeMillis();
            ErrorLogEntry logEntry = new ErrorLogEntry(id, timestamp, throwable);
            logger.error("An error occurred: {}.  Details: {}", logEntry.toString(), throwable);
            errors.add(logEntry);
            fireErrorLoggedEvent();
        } finally {
            writeLock.unlock();
        }

    }


    public List<ErrorLogEntry> getEntries() {
        readLock.lock();
        try {
            return new ArrayList<>(errors);
        } finally {
            readLock.unlock();
        }

    }


    public void clear() {
        writeLock.lock();
        try {
            errors.clear();
            fireErrorLogClearedEvent();
        } finally {
            writeLock.unlock();
        }

    }


    private void fireErrorLoggedEvent() {
        for (WeakReference<ErrorLogListener> ref : new ArrayList<>(listeners)) {
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
        for (WeakReference<ErrorLogListener> ref : new ArrayList<>(listeners)) {
            ErrorLogListener listener = ref.get();
            if (listener != null) {
                listener.errorLogCleared(this);
            }
            else {
                listeners.remove(ref);
            }
        }
    }


    public static class ErrorLogEntry {

        private final int id;

        private final long timeStamp;

        private final Throwable throwable;


        public ErrorLogEntry(int id, long timeStamp, Throwable throwable) {
            this.id = id;
            this.timeStamp = timeStamp;
            this.throwable = throwable;
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
