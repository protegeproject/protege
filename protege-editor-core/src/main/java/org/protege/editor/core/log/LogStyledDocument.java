package org.protege.editor.core.log;

import javax.swing.event.DocumentEvent;
import javax.swing.event.UndoableEditEvent;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Element;
import javax.swing.text.MutableAttributeSet;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

/**
 * A {@link StyledDocument} for printing log messages
 * 
 * @author Yevgeny Kazakov
 */
public class LogStyledDocument extends DefaultStyledDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2499667671492637157L;

	/**
	 * Applies (possibly changed) styles of the document to the current
	 * attributes of the text
	 */
	void applyStyles() {
		int length = getLength();
		if (length == 0) {
			return;
		}
		try {
			writeLock();
			DefaultDocumentEvent changes = new DefaultDocumentEvent(0, length,
					DocumentEvent.EventType.CHANGE);
			buffer.change(0, length, changes);
			int lastEnd;
			for (int pos = 0; pos < length; pos = lastEnd) {
				Element run = getCharacterElement(pos);
				lastEnd = run.getEndOffset();
				if (pos == lastEnd) {
					break;
				}
				MutableAttributeSet attr = (MutableAttributeSet) run
						.getAttributes();
				String name = (String) attr
						.getAttribute(StyleConstants.NameAttribute);
				Style style = getStyle(name);
				changes.addEdit(new AttributeUndoableEdit(run, style, true));
				attr.removeAttributes(attr);
				attr.addAttributes(style);
			}
			changes.end();
			fireChangedUpdate(changes);
			fireUndoableEditUpdate(new UndoableEditEvent(this, changes));
		} finally {
			writeUnlock();
		}
	}

}
