package org.protege.editor.core.log;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;

/**
 * Methods for generating preview log messages
 * 
 * @author Yevgeny Kazakov
 */
public class LogPreview implements Runnable {

	private final static Logger LOGGER_ = (Logger) LoggerFactory
			.getLogger(LogPreview.class);

	private final static Runnable RUN_ = new LogPreview();

	static {
		LOGGER_.setAdditive(false);
	}

	static void setLevel(Level newLevel) {
		LOGGER_.setLevel(newLevel);
	}

	static void addAppender(Appender<ILoggingEvent> newAppender) {
		LOGGER_.addAppender(newAppender);
	}

	static void log() {
		new Thread(RUN_).start();
	}

	@Override
	public void run() {
		foo();
	}

	private static void foo() {
		bar();
	}

	private static void bar() {
		if (LOGGER_.isDebugEnabled()) {
			Runtime runtime = Runtime.getRuntime();
			LOGGER_.debug(
					"About to print some log messages. Here is some lengthy diagnostic information possibly with manual line breaks:\nAvailable processors: {} cores \nTotal memory available to JVM: {} MBytes",
					runtime.availableProcessors(),
					runtime.totalMemory() / 1024 / 1024);
		}
		LOGGER_.info("--- Log Output ---");
		LOGGER_.info("Printing log message");
		LOGGER_.warn("There is a potential problem!");
		LOGGER_.error("Something bad just happened!",
				new RuntimeException("Some Exception"));
	}

}
