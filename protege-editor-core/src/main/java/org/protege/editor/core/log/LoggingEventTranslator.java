package org.protege.editor.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.IThrowableProxy;
import ch.qos.logback.classic.spi.StackTraceElementProxy;
import com.google.common.collect.ImmutableList;

import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 05/11/15
 */
public class LoggingEventTranslator {

    public LogRecord toLogRecord(ILoggingEvent event) {
        return new LogRecord(
                toLogLevel(event.getLevel()),
                event.getTimeStamp(),
                event.getFormattedMessage(),
                toThrowableInfo(event),
                event.getThreadName()
        );
    }


    private static LogLevel toLogLevel(Level level) {
        if(level.equals(Level.ERROR)) {
            return LogLevel.ERROR;
        }
        else if(level.equals(Level.WARN)) {
            return LogLevel.WARN;
        }
        else if(level.equals(Level.INFO)) {
            return LogLevel.INFO;
        }
        else if(level.equals(Level.DEBUG)) {
            return LogLevel.DEBUG;
        }
        else {
            return LogLevel.TRACE;
        }
    }

    private static Optional<ThrowableInfo> toThrowableInfo(ILoggingEvent event) {
        IThrowableProxy throwableProxy = event.getThrowableProxy();
        if(throwableProxy == null) {
            return Optional.empty();
        }
        ImmutableList.Builder<StackTraceElement> result = ImmutableList.builder();
        for(StackTraceElementProxy proxy : throwableProxy.getStackTraceElementProxyArray()) {
            result.add(proxy.getStackTraceElement());
        }
        ThrowableInfo throwableInfo = new ThrowableInfo(
                throwableProxy.getClassName(),
                throwableProxy.getMessage(),
                result.build());

        return Optional.of(throwableInfo);
    }
}
