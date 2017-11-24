package org.protege.editor.core.log;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 * The panel for previewing the result of changing the text foreground
 * 
 * @author Yevgeny Kazakov
 */
public class TextForegroundPreviewPanel extends JPanel
		implements ChangeListener {

	private static final long serialVersionUID = 1996059790160546273L;

	private static final int LINE_LIMIT = 128; // characters

	private final JTextArea text = new JTextArea();

	private Color color;

	public TextForegroundPreviewPanel(JColorChooser chooser) {
		chooser.getSelectionModel().addChangeListener(this);
		setForeground(color);
		setBackground(Color.WHITE);
		setOpaque(true);
		setFont(new Font("monospaced", Font.PLAIN, 12));
		text.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		add(text);
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		text.setForeground(color);
	}

	public void setText(String t) {
		if (t.length() > LINE_LIMIT) {
			t = t.substring(0, LINE_LIMIT - 3) + "...";
		}
		text.setText(t);
	}

}
