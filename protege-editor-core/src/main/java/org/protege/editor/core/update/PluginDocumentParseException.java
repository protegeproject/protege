package org.protege.editor.core.update;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 18/11/15
 */
public class PluginDocumentParseException extends Exception {

    public PluginDocumentParseException(String message) {
        super(message);
    }

    public PluginDocumentParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
