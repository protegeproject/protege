package org.protege.editor.core.log;

import com.google.common.collect.ImmutableList;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 05/11/15
 */
public class ThrowableInfo {

    private final String className;

    private final String message;

    private final ImmutableList<StackTraceElement> stackTrace;

    private final Optional<ThrowableInfo> cause;

    public ThrowableInfo(String className, String message, ImmutableList<StackTraceElement> stackTrace, Optional<ThrowableInfo> cause) {
        this.className = checkNotNull(className);
        this.message = checkNotNull(message);
        this.stackTrace = checkNotNull(stackTrace);
        this.cause = checkNotNull(cause);
    }

    public String getClassName() {
        return className;
    }

    public String getMessage() {
        return message;
    }

    public ImmutableList<StackTraceElement> getStackTrace() {
        return stackTrace;
    }

    public Optional<ThrowableInfo> getCause() {
        return cause;
    }
}
