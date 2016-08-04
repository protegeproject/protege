package org.protege.editor.core.ui.util;

import javax.swing.*;
import java.util.Optional;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 4 Aug 16
 */
public interface TextFieldSearchHandler {

    /**
     * Handle a request to perform a search with reference to a specific text field.
     * @param textField the originator of the request.
     * @return The search result to be entered into the text field.
     */
    Optional<String> handleSearch(JTextField textField);
}
