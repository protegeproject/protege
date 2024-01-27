package org.protege.editor.owl.ui.util;

import javax.swing.JComponent;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 11 May 16
 */
public interface ProgressView {

    void setMessage(String message);

    void setSubMessage(String subMessage);

    void clearSubMessage();

    JComponent asJComponent();

}
