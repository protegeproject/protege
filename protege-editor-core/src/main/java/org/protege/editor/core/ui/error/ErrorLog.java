package org.protege.editor.core.ui.error;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.Date;
import java.util.List;


/**
 * Author: Matthew Horridge<br>
 * The University Of Manchester<br>
 * Bio-Health Informatics Group<br>
 * Date: 28-Feb-2007<br><br>
 */
public class ErrorLog {

    private final Logger logger = LoggerFactory.getLogger(ErrorLog.class);

    public ErrorLog() {

    }

    @Deprecated
    public void addListener(ErrorLogListener listener) {
        logger.error("The listener {} has not been added.  The addListener method is deprecated.",
                listener.getClass().getName());
    }

    @Deprecated
    public void removeListener(ErrorLogListener listener) {
        logger.error("The listener {} has not been removed.  The removeListener method is deprecated.",
                listener.getClass().getName());
    }

    @Deprecated
    public void clearListeners(){
        logger.warn("Clear listeners");
    }

    @Deprecated
    public void uncaughtException(Thread t, Throwable e) {
        logError(e);
    }


    @Deprecated
    public void logError(Throwable throwable) {
        logger.error("An error was thrown: {}", throwable.getMessage(), throwable);
    }


    @Deprecated
    public List<ErrorLogEntry> getEntries() {
        return Collections.emptyList();
    }

    @Deprecated
    public void clear() {

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

    @Deprecated
    public void handleError(Thread t, Throwable e) {
        logError(e);
    }
}
