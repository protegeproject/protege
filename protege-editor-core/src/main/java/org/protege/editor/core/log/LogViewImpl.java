package org.protege.editor.core.log;


import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import ch.qos.logback.core.Context;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

	private static final Color ERROR_COLOR_ = new Color(220, 0, 0),
			WARNING_COLOR_ = new Color(255, 135, 0);
	
	private static int[] LOG_LEVELS_ = { 0, Level.INFO_INT, Level.WARN_INT,
			Level.ERROR_INT };
	
	private static Color[] LOG_DEFAULT_FOREGROUND_COLORS_ = { Color.LIGHT_GRAY, null,
			WARNING_COLOR_, ERROR_COLOR_ };

	private final LogStyledDocument doc;
	
	private final JTextPane text;

	private final PatternLayout layout;

	/**
	 * styles corresponding to {@link #LOG_LEVELS_}
	 */
	private final Style[] styles = new Style[LOG_LEVELS_.length];

	private final Style commentStyle;

	private int charLimit;

	private int displayedChars = 0;

	private final LogEditorKit editorKit;

	public LogViewImpl() {
		text = new JTextPane();
		text.setFont(new Font("monospaced", Font.PLAIN, 12));
		editorKit = new LogEditorKit();
		text.setEditorKit(editorKit);
		doc = new LogStyledDocument();
		text.setDocument(doc);

		Style debugStyle = doc.addStyle("debug", null);
		Style infoStyle = doc.addStyle("info", null);
		Style warningStyle = doc.addStyle("warning", null);
		Style errorStyle = doc.addStyle("error", null);
		commentStyle = doc.addStyle("comment", null);		
		styles[0] = debugStyle;
		styles[1] = infoStyle;
		styles[2] = warningStyle;
		styles[3] = errorStyle;
		layout = new PatternLayout();

		applyPreferences();
	}

	public boolean isLineWrap() {
		return editorKit.isLineWrap();
	}

	public void setLineWrap(boolean wrap) {
		editorKit.setLineWrap(wrap);
		text.revalidate();
		text.repaint();
	}
	
	public void setForegroundsFrom(Map<String, Color> foregrounds) {
		for (int i = 0; i < LOG_DEFAULT_FOREGROUND_COLORS_.length; i++) {
			Color foreground = LOG_DEFAULT_FOREGROUND_COLORS_[i];
			setForeground(styles[i], foreground, foregrounds);
		}
		setForeground(commentStyle, Color.GRAY, foregrounds);
	}
	
	private void setForeground(Style style, Color defaultColor, Map<String, Color> overriden) {		
		Color foreground = overriden.get(style.getName());
		if (foreground == null) {
			foreground = defaultColor;
		}
		StyleConstants.setForeground(style,
				foreground == null
						? StyleConstants.getForeground(
								doc.getStyle(StyleContext.DEFAULT_STYLE))
						: foreground);
	}
	
	public void copyForegroundsTo(LogPreferences prefs) {
		Map<String, Color> foregrounds = new HashMap<String, Color>(); 
		for (int i = 0; i < LOG_DEFAULT_FOREGROUND_COLORS_.length; i++) {
			Color foreground = LOG_DEFAULT_FOREGROUND_COLORS_[i];
			copyForeground(styles[i], foreground, foregrounds);
		}
		copyForeground(commentStyle, Color.GRAY, foregrounds);
		prefs.logStyleForegrounds = foregrounds;
	}
	
	private void copyForeground(Style style, Color defaultColor, Map<String, Color> overriden) {
		Color newColor = StyleConstants.getForeground(style);
		if (!newColor.equals(defaultColor)) {
			overriden.put(style.getName(), newColor);			
		}
	}
	
	public int getCharLimit() {
		return charLimit;
	}

	public void setCharLimit(int limit) {
		this.charLimit = limit;
	}

	public String getPattern() {
		return layout.getPattern();
	}

	public void setPattern(String pattern) {
		boolean started = isStarted();
		if (started) {
			layout.stop();
			layout.setContext(getContext());
		}
		layout.setPattern(pattern);
		if (started) {
			startLayout();
		}
	}

	private void startLayout() {
		PatternLayout testLayout = new PatternLayout();
		testLayout.setPattern(layout.getPattern());
		testLayout.setContext(getContext());
		try {
			testLayout.start();
			if (!testLayout.isStarted()) {
				setParserExceptionPattern();
			}
		} catch (Throwable e) {
			setParserExceptionPattern();
		}
		layout.start();
	}

	private void setParserExceptionPattern() {
		layout.setPattern("INVALID LOG PATTERN!%n%nopex");
	}

	@Override
	public JTextPane asJComponent() {
		return text;
	}

	@Override
	public void clearView() {
		SwingUtilities.invokeLater(() -> {
			text.setText("");
			displayedChars = 0;
		});
	}

	@Override
	public void start() {
		startLayout();
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
			// find the closest low or equal level from LOG_LEVELS_
			int pos = Arrays.binarySearch(LOG_LEVELS_, level);
			if (pos < 0) {
				// see binarySearch documentation
				pos = -pos - 2;
			}
			Style style = styles[pos];
			if (level == Level.INFO_INT) {
				String message = event.getFormattedMessage();
				if (message.startsWith("---") && message.endsWith("---")) {
					style = commentStyle;
				}
			}
			try {
				String message = layout.doLayout(event);
				int increment = message.length();
				doc.insertString(doc.getLength(), message, style);
				displayedChars += increment;
				while (displayedChars > charLimit) {
					Element root = doc.getDefaultRootElement();
					Element line = root.getElement(0);
					int end = line.getEndOffset();
					doc.remove(0, end);
					displayedChars -= end;
				}
			} catch (BadLocationException e) {
				e.printStackTrace();
			}
		});
	}

	@Override
	public void applyPreferences() {
		LogPreferences prefs = LogPreferences.create().load();
		setLineWrap(prefs.wrapLines);
		if (prefs.limitLogOutput) {
			setCharLimit(prefs.logCharacterLimit);
		} else {
			setCharLimit(Integer.MAX_VALUE);
		}
		setPattern(prefs.logPattern);
		setForegroundsFrom(prefs.logStyleForegrounds);
		doc.applyStyles();
	}

}
