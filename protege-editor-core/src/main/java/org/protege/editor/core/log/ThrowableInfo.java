package org.protege.editor.core.log;

import com.google.common.collect.ImmutableList;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 05/11/15
 */
public class ThrowableInfo {

    private final String className;

    private final String message;

    private final ImmutableList<StackTraceElement> stackTrace;

    public ThrowableInfo(String className, String message, ImmutableList<StackTraceElement> stackTrace) {
        this.className = className;
        this.message = message;
        this.stackTrace = stackTrace;
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
}
