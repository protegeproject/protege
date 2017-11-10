package org.protege.editor.core.log;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogManager {

    private final Appender<ILoggingEvent> appender;

    private final List<LogStatusListener> listenerList = new ArrayList<>();

    private final JDialog logViewDialog;

    public LogManager(LogView logView) {
        
        appender = new AppenderBase<ILoggingEvent>() {
        	
        	    @Override
        	    public void start() {
       	    	   logView.start();
        	    	   super.start();  
        	    }
        	    
        	    @Override
			public void stop() {
				super.stop();
				logView.stop();
			}
        	    
        	    @Override
        	    public void setContext(Context context) {
        	    	    logView.setContext(context);
          	    super.setContext(context);
        	    }
        	
            @Override
			protected void append(ILoggingEvent event) {
            	    logView.doAppend(event);
				fireEvent(event);
			}
        };

        JOptionPane op = new JOptionPane(logView.asJComponent(), JOptionPane.PLAIN_MESSAGE);
        logViewDialog = op.createDialog(null, "Log");
        logViewDialog.setModal(false);
        logViewDialog.setResizable(true);
    }

    public synchronized void addErrorLogListener(LogStatusListener listener) {
        listenerList.add(listener);
    }

	public synchronized void removeErrorLogListener(LogStatusListener listener) {
		listenerList.remove(listener);
	}

	private synchronized void fireEvent(ILoggingEvent event) {	    		
		for (int i = 0; i < listenerList.size(); i++) {
			listenerList.get(i).eventLogged(event);			
		}
	}

	private synchronized void fireErrorsCleared() {
		listenerList.stream().forEach(LogStatusListener::statusCleared);
	}

    private ch.qos.logback.classic.Logger getRootLogger() {
        return (ch.qos.logback.classic.Logger) LoggerFactory.getLogger(Logger.ROOT_LOGGER_NAME);
    }

    public void bind() {
        ch.qos.logback.classic.Logger rootLogger = getRootLogger();
        appender.setContext(rootLogger.getLoggerContext());
        appender.start();
        rootLogger.addAppender(appender);
    }

    public void unbind() {        
        getRootLogger().detachAppender(appender);
        appender.stop();
    }

    public void showLogView() {
        logViewDialog.setVisible(true);
        fireErrorsCleared();
    }
}
