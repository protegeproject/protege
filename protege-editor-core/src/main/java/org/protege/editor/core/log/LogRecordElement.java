package org.protege.editor.core.log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Optional;

/**
* Matthew Horridge
* Stanford Center for Biomedical Informatics Research
* 22/01/16
*/
public final class LogRecordElement {

    private final LogRecord logRecord;

    public LogRecordElement(LogRecord logRecord) {
        this.logRecord = logRecord;
    }

    public LogRecord getLogRecord() {
        return logRecord;
    }

    @Override
    public String toString() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        pw.format("Level:       %s\n", logRecord.getLogLevel());
        pw.format("Time:        %s\n", logRecord.getTimestamp());
        pw.format("Message:     %s\n", logRecord.getFormattedMessage());
        Optional<ThrowableInfo> throwableInfo = logRecord.getThrowableInfo();
        if (throwableInfo.isPresent()) {
            pw.format("%s\n", throwableInfo.get().getClassName());
            pw.format("StackTrace:  \n");
            formatThrowableInfo(1, pw, throwableInfo);
        }
        pw.flush();
        return sw.toString();
    }

    private static void formatThrowableInfo(int level, PrintWriter pw, Optional<ThrowableInfo> throwableInfo) {
        if (!throwableInfo.isPresent()) {
            return;
        }
        ThrowableInfo info = throwableInfo.get();
        for(StackTraceElement e : info.getStackTrace()) {
            for(int i = 0; i < level; i++) {
                pw.format("    ");
            }
            pw.format("%s\n", e.toString());
        }
        if (info.getCause().isPresent()) {
            pw.format("          Caused by:\n");
            formatThrowableInfo(level + 1, pw, info.getCause());
        }
    }
    
    String getTooltip() {
    	   String message = logRecord.getFormattedMessage();
    	   if (message.length() <= 80) {
    		   return null; // should be visible fine
    	   }
    	   return "<html><p width=\"500\">" + message + "</p></html>";
    }

    @Override
    public int hashCode() {
        return logRecord.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LogRecordElement)) {
            return false;
        }
        LogRecordElement other = (LogRecordElement) obj;
        return this.logRecord.equals(other.logRecord);
    }
}
