package org.protege.editor.core.log;


import org.protege.editor.core.FileUtils;
import org.protege.editor.core.ui.action.TimestampOutputAction;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;
import ch.qos.logback.core.Layout;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import java.awt.*;
import java.util.Arrays;

/**
 * Matthew Horridge
 * Stanford Center for Biomedical Informatics Research
 * 04/11/15
 * 
 * @author Yevgeny Kazakov
 * 11/11/17: use JTextPane to display log and PatternLayout to format messages
 */
public class LogViewImpl extends AppenderBase<ILoggingEvent>
		implements LogView {

	private static String PATTERN = "%7level  %date{HH:mm:ss}  %message%n";
	
	private static final Color ERROR_COLOR = new Color(220, 0, 0),
			WARNING_COLOR = new Color(255, 135, 0);
	
	private static int[] LOG_LEVELS = { 0, Level.INFO_INT, Level.WARN_INT,
			Level.ERROR_INT };
	
    private final JComponent view;

    private final JTextPane text;
    
    private final StyledDocument doc;
    
    private final Layout<ILoggingEvent> layout;
    
	final Style[] styles = new Style[LOG_LEVELS.length]; // corresponding to LOG_LEVELS
    
	Style commentStyle;
	
    public LogViewImpl() {
        view = new JPanel(new BorderLayout(7, 7));
        view.setPreferredSize(new Dimension(800, 600));
        text = new JTextPane();
        doc = text.getStyledDocument();
        
        Style debugStyle = doc.addStyle("debug", null);        
        Style warningStyle = doc.addStyle("warning", null);
        Style errorStyle = doc.addStyle("error", null);
        commentStyle = doc.addStyle("comment", null);
        StyleConstants.setForeground(errorStyle, ERROR_COLOR);
		StyleConstants.setForeground(warningStyle, WARNING_COLOR);
		StyleConstants.setForeground(debugStyle, Color.LIGHT_GRAY);
		StyleConstants.setForeground(commentStyle, Color.GRAY);
		styles[0] = debugStyle;
		styles[1] = null;
		styles[2] = warningStyle;
		styles[3] = errorStyle;
		
        JScrollPane sp = new JScrollPane(text);
        sp.getVerticalScrollBar().setUnitIncrement(15);
        view.add(sp);
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton clearLogButton = new JButton("Clear log");
        clearLogButton.setToolTipText("Remove all log messages");
        clearLogButton.addActionListener(e -> clearView());
        JButton showLogFile = new JButton("Show log file");
        showLogFile.setToolTipText("Show the log file in the system file browser");
        showLogFile.addActionListener(e -> FileUtils.showLogFile());
        JButton timeStampButton = new JButton("Time stamp");
		timeStampButton.addActionListener(e -> TimestampOutputAction.createTimeStamp(view));
		timeStampButton.setToolTipText("Print a timestamp and optional message into the logs or console");
        buttonPanel.add(showLogFile);
        buttonPanel.add(timeStampButton);
        buttonPanel.add(clearLogButton);
        view.add(buttonPanel, BorderLayout.SOUTH);
        text.setFont(new Font("monospaced", Font.PLAIN, 12)); 
        PatternLayout pl = new PatternLayout();
        pl.setPattern(PATTERN);
        layout = pl;
    }

    @Override
    public JComponent asJComponent() {
        return view;
    }

    @Override
    public void clearView() {
        text.setText("");
    }

	@Override
	public void start() {
		layout.start();
		super.start();		
	}
	
	@Override
	public void stop() {
		super.stop();
		layout.stop();
	}
    
	@Override
	public void setContext(Context context) {
		layout.setContext(context);
		super.setContext(context);
	}
	
    @Override
    public void append(ILoggingEvent event) {
		SwingUtilities.invokeLater(() -> {			
			int level = event.getLevel().toInteger();
			// find the closest low or equal level from LOG_LEVELS
			int pos = Arrays.binarySearch(LOG_LEVELS, level);
			if (pos < 0) {
				// see binarySearch documentation
				pos = -pos -2;
			}
			Style style = styles[pos];
			if (level == Level.INFO_INT) {
				String message = event.getFormattedMessage();
				if (message.startsWith("---") && message.endsWith("---")) {
					style = commentStyle;
				}	
			}
			try {
				doc.insertString(doc.getLength(),
						layout.doLayout(event), style);
			} catch (BadLocationException e) {
				e.printStackTrace();
			}		
		});
    }

}
