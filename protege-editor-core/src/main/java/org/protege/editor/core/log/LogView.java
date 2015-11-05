package org.protege.editor.core.log;

import javax.swing.*;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public interface LogView {

    void clearView();

    JComponent asJComponent();

    void append(LogRecord logRecord);

}
