package org.protege.editor.core.log;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JColorChooser;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.text.Utilities;
import javax.swing.undo.UndoManager;

import org.protege.editor.core.ui.preferences.PreferencesLayoutPanel;
import org.protege.editor.core.ui.preferences.PreferencesPanel;
import org.protege.editor.core.ui.util.ComponentFactory;
import org.protege.editor.core.ui.util.NativeBrowserLauncher;
import org.protege.editor.core.ui.view.HelpIcon;
import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;

/**
 * A {@link PreferencesPanel} for setting {@link LogPreferences}
 * 
 * @author Yevgeny Kazakov
 */
public class LogPreferencesPanel extends PreferencesPanel {

	private static final long serialVersionUID = -8899057690635957213L;

	private static final Logger LOGGER_ = (Logger) LoggerFactory
			.getLogger(LogPreferencesPanel.class);

	private static final Color LOG_PATTERN_HELP_BUTTON_COLOR_ = new Color(200,
			200, 200);

	private static final String LOG_PATTERN_HELP_URL_ = "https://logback.qos.ch/manual/layouts.html#conversionWord";

	private JComboBox<String> logLevel_;

	private JCheckBox wrapLinesBox_, limitLogOutputCheckBox_;

	private SpinnerNumberModel logCharacterLimitModel_;

	private JTextField patternField_;

	private UndoManager patternUndoManager_;

	private JColorChooser colorChooser_;

	private TextForegroundPreviewPanel colorPreview_;

	private LogViewImpl previewLog_;

	private boolean previewRefreshed_ = false;

	private void loadFrom(LogPreferences prefs) {
		logLevel_.setSelectedItem(prefs.logLevel);
		wrapLinesBox_.setSelected(prefs.wrapLines);
		limitLogOutputCheckBox_.setSelected(prefs.limitLogOutput);
		logCharacterLimitModel_.setValue(prefs.logCharacterLimit);
		patternField_.setText(prefs.logPattern);
		patternUndoManager_.discardAllEdits();
		previewLog_.setForegroundsFrom(prefs.logStyleForegrounds);
	}

	private void saveTo(LogPreferences prefs) {
		prefs.logLevel = logLevel_.getSelectedItem().toString();
		prefs.wrapLines = wrapLinesBox_.isSelected();
		prefs.limitLogOutput = limitLogOutputCheckBox_.isSelected();
		prefs.logCharacterLimit = logCharacterLimitModel_.getNumber()
				.intValue();
		prefs.logPattern = patternField_.getText();
		previewLog_.copyForegroundsTo(prefs);
	}

	@Override
	public void initialise() {
		setLayout(new BorderLayout());
		PreferencesLayoutPanel panel = new PreferencesLayoutPanel();
		add(panel, BorderLayout.NORTH);

		panel.addGroup("Log window");
		wrapLinesBox_ = new JCheckBox("Line wrap");
		wrapLinesBox_.setToolTipText(
				"Wraps lines that exceed the width of the window");
		panel.addGroupComponent(wrapLinesBox_);
		wrapLinesBox_.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				refreshPreview();
			}
		});

		limitLogOutputCheckBox_ = new JCheckBox("Limit displayed characters");
		limitLogOutputCheckBox_.setToolTipText(
				"Deletes old log messages when the window is full");
		panel.addGroupComponent(limitLogOutputCheckBox_);
		logCharacterLimitModel_ = new SpinnerNumberModel(1, 1,
				Integer.MAX_VALUE, 10000);
		JSpinner spinner = new JSpinner(logCharacterLimitModel_);
		logCharacterLimitModel_.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				refreshPreview();
			}
		});
		spinner.setToolTipText(
				"The maximal number of characters displayed in the log window");
		panel.addGroupComponent(spinner);
		spinner.setEnabled(limitLogOutputCheckBox_.isSelected());
		limitLogOutputCheckBox_.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				spinner.setEnabled(limitLogOutputCheckBox_.isSelected());
				refreshPreview();
			}
		});

		panel.addSeparator();

		panel.addGroup("Log level");
		logLevel_ = new JComboBox<>(new String[] { Level.OFF.toString(),
				Level.ERROR.toString(), Level.WARN.toString(),
				Level.INFO.toString(), Level.DEBUG.toString() });
		logLevel_.setToolTipText(
				"Messages below this log level will not appear in the log");
		logLevel_.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				refreshPreview();
			}
		});
		panel.addGroupComponent(logLevel_);

		panel.addSeparator();

		panel.addGroup("Log pattern");
		patternField_ = new JTextField(30);
		patternField_.getDocument().addDocumentListener(new DocumentListener() {
			@Override
			public void changedUpdate(DocumentEvent e) {
				refreshPreview();
			}

			@Override
			public void removeUpdate(DocumentEvent e) {
				refreshPreview();
			}

			@Override
			public void insertUpdate(DocumentEvent e) {
				refreshPreview();
			}
		});
		patternUndoManager_ = new UndoManager();
		patternField_.getDocument()
				.addUndoableEditListener(patternUndoManager_);
		Action undoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (patternUndoManager_.canUndo()) {
					patternUndoManager_.undo();
				}
			}
		};
		Action redoAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (patternUndoManager_.canRedo()) {
					patternUndoManager_.redo();
				}
			}
		};
		patternField_.registerKeyboardAction(undoAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.META_MASK),
				JComponent.WHEN_FOCUSED);
		patternField_.registerKeyboardAction(redoAction,
				KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.META_MASK),
				JComponent.WHEN_FOCUSED);
		Icon helpIcon = HelpIcon.get();
		JButton patternHelpButton = new JButton(
				new AbstractAction(null, helpIcon) {
					private static final long serialVersionUID = 0L;

					@Override
					public void actionPerformed(ActionEvent e) {
						NativeBrowserLauncher.openURL(LOG_PATTERN_HELP_URL_);
					}
				});
		patternHelpButton.setPreferredSize(new Dimension(
				helpIcon.getIconWidth(), helpIcon.getIconHeight()));
		patternHelpButton
				.setBorder(BorderFactory.createEmptyBorder(0, 1, 0, 1));
		patternHelpButton.setToolTipText("Help");
		patternHelpButton.setBorder(null);
		patternHelpButton.setBackground(LOG_PATTERN_HELP_BUTTON_COLOR_);
		patternHelpButton.setOpaque(true);
		JPanel patternPanel = new JPanel() {
			private static final long serialVersionUID = 1L;

			@Override
			public int getBaseline(int width, int height) {
				return patternField_.getBaseline(width, height);
			}
		};
		patternPanel.setLayout(new BoxLayout(patternPanel, BoxLayout.LINE_AXIS));
		patternPanel.add(patternField_);
		patternPanel.add(patternHelpButton);
		patternPanel.setBackground(patternField_.getBackground());
		patternPanel.setBorder(patternField_.getBorder());
		patternField_.setBorder(null);
		patternField_.setToolTipText(
				"The format string that determines how log messages are printed");
		patternPanel.setMinimumSize(patternPanel.getPreferredSize());
		panel.addGroupComponent(patternPanel);

		colorChooser_ = new JColorChooser();
		colorPreview_ = new TextForegroundPreviewPanel(colorChooser_);
		colorChooser_.setPreviewPanel(colorPreview_);

		previewLog_ = new LogViewImpl();
		previewLog_.setContext(LOGGER_.getLoggerContext());
		previewLog_.start();
		JTextPane previewText = previewLog_.asJComponent();
		previewText.setPreferredSize(new Dimension(300, 120));
		LogPreview.addAppender(previewLog_);

		JPanel holder = new JPanel(new BorderLayout());
		holder.setBorder(ComponentFactory.createTitledBorder("Preview"));
		holder.add(previewText);
		previewText.setCursor(new Cursor(Cursor.HAND_CURSOR));
		previewText.addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseMoved(MouseEvent e) {
				int pos = previewText.viewToModel(e.getPoint());
				StyledDocument doc = previewText.getStyledDocument();
				Element element = doc.getCharacterElement(pos);
				AttributeSet addtributes = element.getAttributes();
				Style style = doc.getStyle((String) addtributes
						.getAttribute(StyleConstants.NameAttribute));
				previewText.setToolTipText(
						"Click to change the " + style.getName() + " color");
			}
		});

		previewText.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				int pos = previewText.viewToModel(e.getPoint());
				Style style = getStyleAtPoint(previewText, e.getPoint());
				Color color = StyleConstants.getForeground(style);
				try {
					int rowStart = getStyleStart(previewText, pos, style);
					int rowEnd = getStyleEnd(previewText, pos, style);
					colorPreview_.setText(previewText.getStyledDocument()
							.getText(rowStart, rowEnd - rowStart));
				} catch (BadLocationException e1) {
					LOGGER_.error("Error creating message preview", e1);
				}

				JOptionPane op = new JOptionPane(colorChooser_,
						JOptionPane.PLAIN_MESSAGE,
						JOptionPane.OK_CANCEL_OPTION);
				JDialog dlg = op.createDialog(previewText, "Choose Text Color");
				dlg.setLocationRelativeTo(previewText);
				colorChooser_.setColor(color);
				dlg.setVisible(true);
				if (op.getValue() != null) {
					color = colorChooser_.getColor();
					StyleConstants.setForeground(style, color);
					refreshPreview();
				}
			}
		});

		add(holder);

		panel.addSeparator();
		JButton resetButton = new JButton("Reset");
		resetButton.addActionListener(e -> handleReset());
		resetButton.setToolTipText("Resets all settings to default values");
		panel.addGroupComponent(resetButton);

		loadFrom(LogPreferences.create().load());
		setMinimumSize(getPreferredSize());
	}

	private static int getStyleStart(JTextPane text, int pos, Style style)
			throws BadLocationException {
		StyledDocument doc = text.getStyledDocument();
		String styleName = style.getName();
		for (;;) {
			pos = Utilities.getRowStart(text, pos);
			if (pos == 0) {
				return pos;
			}
			Element element = doc.getCharacterElement(pos - 1);
			AttributeSet addtributes = element.getAttributes();
			if (!styleName.equals(
					addtributes.getAttribute(StyleConstants.NameAttribute))) {
				return pos;
			}
			// else
			pos--;
		}
	}

	private static int getStyleEnd(JTextPane text, int pos, Style style)
			throws BadLocationException {
		StyledDocument doc = text.getStyledDocument();
		int maxPos = doc.getLength();
		String styleName = style.getName();
		for (;;) {
			pos = Utilities.getRowEnd(text, pos);
			if (pos == maxPos) {
				return pos;
			}
			Element element = doc.getCharacterElement(pos + 1);
			AttributeSet addtributes = element.getAttributes();
			if (!styleName.equals(
					addtributes.getAttribute(StyleConstants.NameAttribute))) {
				return pos;
			}
			// else
			pos++;
		}
	}

	private static Style getStyleAtPoint(JTextPane text, Point point) {
		int pos = text.viewToModel(point);
		StyledDocument doc = text.getStyledDocument();
		Element element = doc.getCharacterElement(pos);
		AttributeSet addtributes = element.getAttributes();
		return doc.getStyle((String) addtributes
				.getAttribute(StyleConstants.NameAttribute));
	}

	@Override
	public void dispose() throws Exception {
		// nothing
	}

	@Override
	public void applyChanges() {
		LogPreferences prefs = LogPreferences.create();
		saveTo(prefs);
		prefs.save();
	}

	private void handleReset() {
		loadFrom(LogPreferences.create());
	}

	private void refreshPreview() {
		previewRefreshed_ = false;
		SwingUtilities.invokeLater(() -> {
			if (previewRefreshed_) {
				return;
			}
			previewLog_.clearView();
			previewLog_.setPattern(patternField_.getText());
			previewLog_.setLineWrap(wrapLinesBox_.isSelected());
			previewLog_.setCharLimit(limitLogOutputCheckBox_.isSelected()
					? logCharacterLimitModel_.getNumber().intValue()
					: Integer.MAX_VALUE);
			LogPreview.setLevel(
					Level.toLevel(logLevel_.getSelectedItem().toString()));
			LogPreview.log();
			previewRefreshed_ = true;
		});
	}

}
