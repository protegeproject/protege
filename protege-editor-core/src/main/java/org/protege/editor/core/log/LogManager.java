package org.protege.editor.core.log;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import com.google.common.collect.ImmutableList;
import org.protege.editor.core.ui.error.ErrorLogListener;
import org.protege.editor.core.ui.workspace.Workspace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogManager {

    private final LogView logView;

    private final AppenderBase<ILoggingEvent> appender;

    private final List<ErrorLogListener> listenerList = new ArrayList<>();

    private final LoggingEventTranslator translator = new LoggingEventTranslator();

    public LogManager() {
        logView = new LogViewImpl();
        appender = new AppenderBase<ILoggingEvent>() {
            @Override
            protected void append(ILoggingEvent event) {
                logView.append(translator.toLogRecord(event));
                if(event.getLevel() == Level.ERROR) {
                    fireErrorLogged();
                }
            }
        };
    }

    public void addErrorLogListener(ErrorLogListener listener) {
        listenerList.add(listener);
    }

    public void removeErrorLogListener(ErrorLogListener listener) {
        listenerList.remove(listener);
    }

    private void fireErrorLogged() {
        new ArrayList<>(listenerList)
                .stream()
                .forEach(ErrorLogListener::errorLogged);
    }




    private ch.qos.logback.classic.Logger getRootLogger() {
        return (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }

    public void bind() {
        ch.qos.logback.classic.Logger rootLogger = getRootLogger();
        rootLogger.addAppender(appender);
        appender.start();
    }



    public void unbind() {
        appender.stop();
        getRootLogger().detachAppender(appender);
    }

    public void showLogView() {
        JOptionPane op = new JOptionPane(logView.asJComponent(), JOptionPane.PLAIN_MESSAGE);
        JDialog dlg = op.createDialog(null, "Log");
        dlg.setModal(false);
        dlg.setResizable(true);
        dlg.setVisible(true);
    }
}
