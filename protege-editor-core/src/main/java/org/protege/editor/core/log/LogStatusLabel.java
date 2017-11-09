package org.protege.editor.core.log;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.Icon;
import javax.swing.JLabel;

import org.protege.editor.core.ProtegeApplication;
import org.protege.editor.core.ui.util.Icons;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;

/**
 * A label for showing status about log messages (if there are any errors or
 * warnings) and providing a quick access to open the log window
 * 
 * @author Yevgeny Kazakov
 *
 */
public class LogStatusLabel extends JLabel implements LogStatusListener {

	private static final long serialVersionUID = -6000196744393026164L;

	private static final Icon LOG_ERROR_ICON = Icons.getIcon("error.png"),
			LOG_WARNING_ICON = Icons.getIcon("warning.png"),
			LOG_ICON = Icons.getIcon("log.png");

	private static final String LOG_TOOLTIP = "Click to open the log",
			LOG_EVENT_TOOLTIP = "<html><body>...<br>%s<br>" + LOG_TOOLTIP + "</body></html>";

	private static int LOG_MESSAGE_LIMIT = 64; // characters

	private Level thresholdLevel;

	private ILoggingEvent lastImportantEvent;

	public LogStatusLabel() {
		statusCleared();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				ProtegeApplication.showLogView();
			}
		});
		setToolTipText(LOG_TOOLTIP);
	}

	@Override
	public synchronized void eventLogged(ILoggingEvent event) {
		Level logLevel = event.getLevel();
		if (logLevel.isGreaterOrEqual(thresholdLevel)) {
			thresholdLevel = logLevel;
			lastImportantEvent = event;
			if (logLevel.isGreaterOrEqual(Level.ERROR)) {
				setIcon(LOG_ERROR_ICON);
			} else {
				setIcon(LOG_WARNING_ICON);
			}
		}
	}

	@Override
	public synchronized void statusCleared() {
		lastImportantEvent = null;
		thresholdLevel = Level.WARN;
		setIcon(LOG_ICON);
	}

	@Override
	public synchronized String getToolTipText(MouseEvent event) {
		if (lastImportantEvent != null) {
			return String.format(LOG_EVENT_TOOLTIP,
					truncate(lastImportantEvent.toString()));
		} else {
			return super.getToolTipText(event);
		}

	}

	static String truncate(String message) {
		if (message.length() > LOG_MESSAGE_LIMIT) {
			message = message.substring(0, LOG_MESSAGE_LIMIT - 2) + "...";
		}
		return message;
	}

}
