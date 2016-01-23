package org.protege.editor.core.log;

import com.google.common.base.MoreObjects;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogRecord {

    private final LogLevel logLevel;

    private final long timestamp;

    private final String formattedMessage;

    private final Optional<ThrowableInfo> throwableInfo;

    private final String threadName;

    public LogRecord(LogLevel logLevel, long timestamp, String formattedMessage, Optional<ThrowableInfo> throwableInfo, String threadName) {
        this.logLevel = checkNotNull(logLevel);
        this.timestamp = timestamp;
        this.formattedMessage = checkNotNull(formattedMessage);
        this.throwableInfo = checkNotNull(throwableInfo);
        this.threadName = checkNotNull(threadName);
    }

    public LogLevel getLogLevel() {
        return logLevel;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getFormattedMessage() {
        return formattedMessage;
    }

    public Optional<ThrowableInfo> getThrowableInfo() {
        return throwableInfo;
    }

    public String getThreadName() {
        return threadName;
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(
                logLevel,
                timestamp,
                formattedMessage,
                throwableInfo,
                threadName
        );
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof LogRecord)) {
            return false;
        }
        LogRecord other = (LogRecord) obj;
        return this.logLevel == other.logLevel
                && this.timestamp == other.timestamp
                && this.formattedMessage.equals(other.formattedMessage)
                && this.throwableInfo.equals(other.throwableInfo)
                && this.threadName.equals(other.threadName);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper("LogRecord")
                .addValue(logLevel)
                .addValue(timestamp)
                .addValue(formattedMessage)
                .addValue(threadName)
                .addValue(throwableInfo)
                .toString();
    }
}
