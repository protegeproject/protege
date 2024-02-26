package org.protege.editor.core.log;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.protege.editor.core.FileUtils;
import org.protege.editor.core.ui.action.TimestampOutputAction;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.Appender;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 */
public class LogManager {

	private final LogView logView;
	
    private final Appender<ILoggingEvent> appender;

    private final List<LogStatusListener> listenerList = new ArrayList<>();

    private final JDialog logViewDialog;

    public LogManager(LogView logView) {
    	
    		this.logView = logView;
        
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

        JComponent holder = new JPanel(new BorderLayout(7, 7));
        holder.setPreferredSize(new Dimension(800, 600));
        JScrollPane sp = new JScrollPane(logView.asJComponent());
        sp.getVerticalScrollBar().setUnitIncrement(15);
        holder.add(sp);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearLogButton = new JButton("Clear log");
        clearLogButton.setToolTipText("Remove all log messages");
        clearLogButton.addActionListener(e -> clearLogView());
        JButton showLogFile = new JButton("Show log file");
        showLogFile.setToolTipText("Show the log file in the system file browser");
        showLogFile.addActionListener(e -> FileUtils.showLogFile());        
		JButton preferencesButton = new JButton("Preferences");		
		preferencesButton.addActionListener(e -> showPreferences());
		preferencesButton.setToolTipText("Display log preferences");
		JButton timeStampButton = new JButton("Time stamp");
		timeStampButton.addActionListener(e -> TimestampOutputAction.createTimeStamp(holder));
		timeStampButton.setToolTipText("Print a timestamp and optional message into the logs or console");
        buttonPanel.add(showLogFile);        
        buttonPanel.add(preferencesButton);
        buttonPanel.add(timeStampButton);
        buttonPanel.add(clearLogButton);
        holder.add(buttonPanel, BorderLayout.SOUTH);
        
        JOptionPane op = new JOptionPane(holder, JOptionPane.PLAIN_MESSAGE);
        logViewDialog = op.createDialog(null, "Log");
        logViewDialog.setModal(false);
        logViewDialog.setResizable(true);
        logViewDialog.addComponentListener(new ComponentAdapter() {
        	@Override
        	public void componentHidden(ComponentEvent e) {
        		fireErrorsCleared();
        	}
		});
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

    private Logger getRootLogger() {
        return (Logger) LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME);
    }

    public void bind() {
    	    applyPreferences();
        Logger rootLogger = getRootLogger();
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

    public void clearLogView() {
        logView.clearView();
        fireErrorsCleared();
    }

    private void showPreferences() {
		LogPreferencesPanel panel = new LogPreferencesPanel();
		panel.initialise();
		JOptionPane op = new JOptionPane(panel, JOptionPane.PLAIN_MESSAGE,
				JOptionPane.OK_CANCEL_OPTION);
		JDialog dlg = op.createDialog(logViewDialog, "Log Preferences");
		dlg.setResizable(true);
		dlg.setVisible(true);
		Object value = op.getValue();
		if (value != null && (int) value == JOptionPane.OK_OPTION) {
			panel.applyChanges();
		}
	}
    
	public void applyPreferences() {
		getRootLogger().setLevel(
				Level.toLevel(LogPreferences.create().load().logLevel));
		logView.applyPreferences();		
	}
}
