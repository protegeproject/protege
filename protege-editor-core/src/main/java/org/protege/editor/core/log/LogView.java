package org.protege.editor.core.log;

import javax.swing.*;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public interface LogView extends Appender<ILoggingEvent> {

	void applyPreferences();
	
    void clearView();

    JComponent asJComponent();

}
